package nekto.controller.item;

import java.util.Arrays;
import java.util.List;

import nekto.controller.tile.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemBase extends Item {
	public TileEntityBase<?> link = null;
	public final static String KEYTAG = "Control";
	public final static String MESS = "chat.item.message";
	public String MESSAGE_0 = StatCollector.translateToLocal(getUnlocalizedName()) + " " + StatCollector.translateToLocal(MESS + 1);
	public String MESSAGE_1 = StatCollector.translateToLocal(MESS + 2) + " " + StatCollector.translateToLocal(getControlName()) + " at ";
	public String MESSAGE_2 = StatCollector.translateToLocal(MESS + 3) + " " + StatCollector.translateToLocal(getControlName());
	public String MESSAGE_3 = StatCollector.translateToLocal(getControlName()) + " " + StatCollector.translateToLocal(MESS + 4) + " " + StatCollector.translateToLocal(getUnlocalizedName());
	public String MESSAGE_4 = StatCollector.translateToLocal(MESS + "5.part1") + " " + StatCollector.translateToLocal(getControlName()) + " " + StatCollector.translateToLocal(MESS + "5.part2");
	public final static String MESSAGE_5 = StatCollector.translateToLocal(MESS + 6) + " ";
	public final static String MESSAGE_6 = StatCollector.translateToLocal(MESS + 7);
	public final static String MESSAGE_7 = StatCollector.translateToLocal(MESS + 8);
	public int[] corner = null;
	private boolean isCornerMode = false;

	public ItemBase() {
		super();
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(KEYTAG)) {
			int[] pos = stack.getTagCompound().getIntArray(KEYTAG);
			par3List.add(MESSAGE_1 + pos[0] + ", " + pos[1] + ", " + pos[2]);
		} else {
			par3List.add(MESSAGE_4);
		}
	}

	public boolean isInCornerMode() {
		return this.isCornerMode;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);
			if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				int i = movingobjectposition.blockX;
				int j = movingobjectposition.blockY;
				int k = movingobjectposition.blockZ;
				TileEntityBase<?> tempTile = null;
				if (isController(i, j, k, world)) {
					tempTile = (TileEntityBase<?>) world.getTileEntity(i, j, k);
				}
				if (itemStack.hasTagCompound() && itemStack.stackTagCompound.hasKey(KEYTAG)) {
					int[] pos = null;
					if (this.link == null) {
						pos = itemStack.getTagCompound().getIntArray(KEYTAG);
						//Try to find the old controller block to set its linker
						if (isController(pos[0], pos[1], pos[2], world)) {
							setItemVar(world, pos);
						} else//It had data on a block that doesn't exist anymore
						{
							itemStack.getTagCompound().removeTag(KEYTAG);
							player.addChatComponentMessage(new ChatComponentText(MESSAGE_2));
							return itemStack;
						}
					}
					if (tempTile != null) {
						if (!onControlUsed(tempTile, player, i, j, k, itemStack))
							player.addChatComponentMessage(new ChatComponentText(MESSAGE_3));
						//Another player might be editing, let's avoid any issue and do nothing.
					} else if (!world.isAirBlock(i, j, k)) {
						onBlockSelected(player, world, world.getBlock(i, j, k), i, j, k, world.getBlockMetadata(i, j, k));
					}
				} else if (isController(i, j, k, world) && ((TileEntityBase<?>) world.getTileEntity(i, j, k)).getLinker() == null) {
					player.addChatComponentMessage(new ChatComponentText(MESSAGE_1 + i + ", " + j + ", " + k));
					this.link = (TileEntityBase<?>) world.getTileEntity(i, j, k);
					this.link.setLinker(this);
					setEditAndTag(getStartData(i, j, k), itemStack);
				} else
					player.addChatComponentMessage(new ChatComponentText(MESSAGE_0 + MESSAGE_4));
			}
		}
		return itemStack;
	}

	public void resetLinker() {
		this.link = null;
	}

	public void setCornerMode(boolean bool) {
		this.isCornerMode = bool;
	}

	protected abstract Class<? extends TileEntityBase<?>> getControl();

	protected abstract String getControlName();

	protected int[] getStartData(int par4, int par5, int par6) {
		return new int[] { par4, par5, par6 };
	}

	/**
	 * Fired if player selected a block which isn't a valid control
	 * 
	 * @param player
	 * @param id
	 *            blockID from {@link World#getBlock(int, int, int)}
	 * @param meta
	 *            block metadata {@link World#getBlockMetadata(int, int, int)}
	 */
	protected void onBlockSelected(EntityPlayer player, World world, Block id, int par4, int par5, int par6, int meta) {
		this.link.setEditing(true);
		if (player.capabilities.isCreativeMode || id != Blocks.bedrock/* bedrock case out */) {
			if (!isCornerMode && !player.isSneaking())
				this.link.add(player, id, par4, par5, par6, meta, true);
			else {
				if (corner == null) {
					corner = new int[] { par4, par5, par6 };
					player.addChatComponentMessage(new ChatComponentText(MESSAGE_5 + par4 + ", " + par5 + ", " + par6));
				} else {
					if (!Arrays.equals(corner, new int[] { par4, par5, par6 })) {
						onMultipleSelection(player, world, corner, new int[] { par4, par5, par6 });
						player.addChatComponentMessage(new ChatComponentText(MESSAGE_6));
					} else
						player.addChatComponentMessage(new ChatComponentText(MESSAGE_7));
					corner = null;
				}
			}
		}
	}

	/**
	 * What should happen if the selected {@link TileEntityBase} by a player is
	 * marked as already used by someone else
	 * 
	 * @param tempTile
	 *            The selected TileEntity
	 * @param player
	 *            The player doing the use
	 * @param stack
	 *            The ItemStack used by player
	 * @return false to send {@link #MESSAGE_3} to player
	 */
	protected abstract boolean onControlUsed(TileEntityBase<?> tempTile, EntityPlayer player, int par4, int par5, int par6, ItemStack stack);

	/**
	 * Helper function to set data into the item and its Control in editing mode
	 * 
	 * @param pos
	 *            Data to set into the item {@link NBTTagCompound}
	 * @param par1ItemStack
	 *            The item that will get the data
	 */
	protected void setEditAndTag(int[] pos, ItemStack par1ItemStack) {
		this.link.setEditing(true);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setIntArray(KEYTAG, pos);
		par1ItemStack.setTagCompound(tag);
	}

	/**
	 * Fired if link is null but item has NBTTag data pointing to a valid
	 * control
	 * 
	 * @param world
	 * @param data
	 *            from the item {@link NBTTagCompound}
	 */
	protected void setItemVar(World world, int... data) {
		this.link = (TileEntityBase<?>) world.getTileEntity(data[0], data[1], data[2]);
	}

	/**
	 * Check for a controller on the coordinates
	 * 
	 * @return True only if there is a tile entity which extends from
	 *         {@link #getControl()}
	 */
	private boolean isController(int x, int y, int z, World world) {
		return getControl().isInstance(world.getTileEntity(x, y, z));
	}

	/**
	 * Fired if corner mode is true or player is sneaking and selected two
	 * different corner blocks
	 * 
	 * @param corner
	 *            the first selected block position
	 * @param endCorner
	 *            the second selected block position
	 */
	private void onMultipleSelection(EntityPlayer player, World world, int[] corner, int[] endCorner) {
		int temp = 0;
		//Sort the corners
		for (int i = 0; i < corner.length; i++) {
			if (corner[i] > endCorner[i]) {
				temp = corner[i];
				corner[i] = endCorner[i];
				endCorner[i] = temp;
			}
		}
		//Corner is now minimum, endCorner is maximum
		for (int x = corner[0]; x <= endCorner[0]; x++)
			for (int y = corner[1]; y <= endCorner[1]; y++)
				for (int z = corner[2]; z <= endCorner[2]; z++) {
					if (!world.isAirBlock(x, y, z))
						this.link.add(player, world.getBlock(x, y, z), x, y, z, world.getBlockMetadata(x, y, z), false);
				}
	}
}
