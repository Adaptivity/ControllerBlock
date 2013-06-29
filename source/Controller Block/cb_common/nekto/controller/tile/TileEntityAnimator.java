package nekto.controller.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nekto.controller.animator.Mode;
import nekto.controller.item.ItemRemote;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;

public class TileEntityAnimator extends TileEntityBase<List<int[]>> {
    
    private int frame, delay;
    private Mode currMode = Mode.ORDER;
	
    public TileEntityAnimator()
    {
    	super(1);
    }
    
    @Override
	public void add(EntityPlayer player, int blockID, int par4, int par5, int par6, int metaData) 
    {
		this.add(player, 0, blockID, par4, par5, par6, metaData);
	}
    @Override
	public void add(EntityPlayer player, int frame, int blockID, int x, int y, int z, int metaData) 
    {	
        boolean removed = false;
        int[] temp = new int[]{blockID,x,y,z,metaData};
        
        while(getBaseList().size() <= frame)
        	getBaseList().add(new ArrayList());
        
        Iterator itr = ((List) getBaseList().get(frame)).listIterator();    
        
        while(itr.hasNext())
        {
        	if(Arrays.equals((int[]) itr.next(),temp)){
        		itr.remove();
        		removed = true;
        		break;
        	}
        }
        
        if(removed) {
        	player.sendChatToPlayer("Removed from list " + blockID + " " + x + " " + y + " " + z + " " + metaData);
        } else {
        	player.sendChatToPlayer("Added to list " + blockID + " " + x + " " + y + " " + z + " " + metaData);
        	getBaseList().get(frame).add(temp);
            //this.worldObj.setBlockToAir(temp[1], temp[2], temp[3]);
        }
    }
	
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("frame", this.frame);
        par1NBTTagCompound.setInteger("delay", this.delay);
        par1NBTTagCompound.setShort("mode", (short) this.getMode().ordinal());
        
        NBTTagList tags = new NBTTagList("frames");
        
        for(int index = 0; index < getBaseList().size(); index++ )
        {
        	NBTTagCompound tag = new NBTTagCompound(Integer.toString(index));
			for(int block = 0; block < getBaseList().get(index).size(); block++)
			{
        		tag.setIntArray( Integer.toString(block), ((int[]) getBaseList().get(index).get(block)));
			}
        	tags.appendTag(tag);
        }
        par1NBTTagCompound.setTag("frames", tags);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
        int count = par1NBTTagCompound.getInteger("length");
        this.setFrame(par1NBTTagCompound.getInteger("frame"));
        this.delay = par1NBTTagCompound.getInteger("delay");
        this.setMode(Mode.values()[par1NBTTagCompound.getShort("mode")]);
        
        for(int i = 0; i < count; i++)
        {
            NBTTagCompound tag = ((NBTTagCompound) par1NBTTagCompound.getTagList("frames").tagAt(i));
            List list = new ArrayList();
            Iterator itr = tag.getTags().iterator();
            while(itr.hasNext())
            	list.add(((NBTTagIntArray)itr.next()).intArray);
            this.getBaseList().add(list);
        }
    }

	@Override
	public String getInvName() 
	{
		return "Animator.inventory";
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) 
	{
		return (i == 0 && itemstack.getItem() instanceof ItemRemote);
	}
	   
    public void setDelay(float f)
    {
    	this.delay += (int)(f * 1000);
    }

    public int getDelay() 
    {
        return this.delay;
    }
    
    public void setFrame(int i) 
	{
    	this.frame = i;
	}

	public int getFrame() 
	{
		return this.frame;
	}
	
    public void setMode(Mode par1Mode)
    {
    	this.currMode = par1Mode;
    }
    
    public Mode getMode()
    {
    	return this.currMode;
    }
}
