/*
 *  Author: Sam6982
 */
package nekto.controller.core;

import java.util.List;

import nekto.controller.ref.GeneralRef;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLinker extends ItemBase {

    public ItemLinker(int id)
    {
        super(id);
        setUnlocalizedName("controllerLinker");
    }
    
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
    	if(!par3World.isRemote)
        {
        	TileEntityController tempTile = null;
        	if(isController(par4, par5, par6, par3World))
        	{   
            	tempTile = (TileEntityController) par3World.getBlockTileEntity(par4, par5, par6);
        	}
            if(par1ItemStack.hasTagCompound() && par1ItemStack.stackTagCompound.hasKey(KEYTAG))
            {
            	int[] pos = null;
            	if(this.link == null)
                {
                	pos = par1ItemStack.getTagCompound().getIntArray(KEYTAG);
                	//Try to find the old controller block to set its linker
                	if(isController(pos[0], pos[1], pos[2], par3World))
                	{
                		this.link = ((TileEntityController)par3World.getBlockTileEntity(pos[0], pos[1], pos[2]));
                	}
                	else//It had data on a block that doesn't exist anymore
            		{
            			par1ItemStack.getTagCompound().removeTag(KEYTAG);
            			player.sendChatToPlayer("Unlinked from Controller.");
            			return false;
            		}
                }
                if(tempTile != null)
            	{   
                    if(tempTile.getLinker() == null && this.link == tempTile)
                    {
                    	tempTile.setLinker(this);
                    	player.sendChatToPlayer("Linked to Controller at " + par4 + ", " + par5 + ", " + par6);
                    }
                    else if(tempTile.getLinker() == this)
                    {
                    	tempTile.setLinker(null);
                    	tempTile.setEditing(false);
                    	par1ItemStack.getTagCompound().removeTag(KEYTAG);
                    	this.resetLinker();
                    	player.sendChatToPlayer("Unlinked from Controller.");
            			return false;
                    }
                    else
                    {
                    	player.sendChatToPlayer("Controller is already linked to another Linker.");
                    	//Another player might be editing, let's avoid any issue and do nothing.
                    	return false;
                    }
                    this.link.setEditing(true);
                    NBTTagCompound tag = new NBTTagCompound();
                	tag.setIntArray(KEYTAG, new int[]{par4, par5, par6});
                	par1ItemStack.setTagCompound(tag);
                }
                else if(!par3World.isAirBlock(par4, par5, par6))
                {
                	this.link.setEditing(true);
                    if(player.capabilities.isCreativeMode)
                    {
                        this.link.add(player, par3World.getBlockId(par4, par5, par6), par4, par5, par6, par3World.getBlockMetadata(par4, par5, par6));
                    } 
                    else if (par3World.getBlockId(par4, par5, par6) != 7)
                    {//Bedrock case removed
                        this.link.add(player, par3World.getBlockId(par4, par5, par6), par4, par5, par6, par3World.getBlockMetadata(par4, par5, par6));                
                    }
                } 
            }   
            else if(isController(par4, par5, par6, par3World) && ((TileEntityController) par3World.getBlockTileEntity(par4, par5, par6)).getLinker() == null)           
            {
            	player.sendChatToPlayer("Linked to Controller at " + par4 + ", " + par5 + ", " + par6);
        		this.link = (TileEntityController) par3World.getBlockTileEntity(par4, par5, par6);
        		this.link.setLinker(this);
            	NBTTagCompound tag = new NBTTagCompound();
            	tag.setIntArray(KEYTAG, new int[]{par4, par5, par6});
            	par1ItemStack.setTagCompound(tag);
            	this.link.setEditing(true);
            }
            else
            	player.sendChatToPlayer("The Linker is not connected. Right click on a controller block to begin linking.");
        }
        return false;
    }
    @Override
    protected boolean isController(int x, int y, int z, World world)
    {
    	return world.getBlockTileEntity(x, y, z) instanceof TileEntityController;
    }
}
