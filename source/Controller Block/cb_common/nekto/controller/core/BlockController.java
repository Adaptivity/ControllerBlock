/*
 *  Author: Sam6982
 */
package nekto.controller.core;

import nekto.controller.ref.GeneralRef;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockController extends BlockContainer {

    private Icon textureSide;
    private Icon textureTop;
    private boolean activated;
    
    public BlockController(int id)
    {
        super(id, Material.rock);
        setUnlocalizedName("controllerBlock");
        setCreativeTab(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2)
    {
        return par1 <= 1 ? this.textureTop : this.textureSide);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.textureSide = par1IconRegister.registerIcon(GeneralRef.TEXTURE_PATH + "controller_side");
        this.textureTop = par1IconRegister.registerIcon(GeneralRef.TEXTURE_PATH + "controller_top");
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityController();
    }
    
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
<<<<<<< HEAD
    {
        TileEntityController tile = (TileEntityController) par1World.getBlockTileEntity(par2, par3, par4);
        if(!activated){
		tile.activate();
		activated=true;
	}
	else{
		tile.deactivate();
		activated=false;
	}
=======
    {        
        TileEntityController tile = (TileEntityController)par1World.getBlockTileEntity(par2, par3, par4);
        
        if(tile != null)
        {
            tile.activate(!(tile.state));
        }
        
>>>>>>> upstream/master
        return false;
        
        /*if (par1World.isRemote)
        {
            return false;
        }
        else
        {
            if(par5EntityPlayer.getItemInUse() == (new ItemStack(Controller.controllerLinker)))
            {
                return false;
            }
            
            TileEntityController tileentitycontroller = (TileEntityController)par1World.getBlockTileEntity(par2, par3, par4);

            if (tileentitycontroller != null)
            {
                par5EntityPlayer.openGui(Controller.instance, 0, par1World, par2, par3, par4);
            }

            return false;
        }*/
    }
    
    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        switch(side) 
        {
            case 0: return true;
            case 1: return true;
            case 2: return true;
            case 3: return true;
            default: return false;
  
        }
    }
    
    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        TileEntityController tile = (TileEntityController) par1World.getBlockTileEntity(par2, par3, par4);
        
        if(tile.isPowered && tile.state)
        {
            tile.activate(true);
        } 
        else if(tile.isPowered && !tile.state) 
        {
            tile.activate(true);
        }
        else if(!tile.isPowered && tile.state)
        {
            tile.activate(false);
        }
        else if(!tile.isPowered && !tile.state)
        {
<<<<<<< HEAD
            TileEntityController tile = (TileEntityController) par1World.getBlockTileEntity(par2, par3, par4);
		if(!activated){
            	tile.activate();
		activated=true;
		}
		else{
		tile.deactivate();
		activated=false;
		}
=======
            tile.activate(false);
>>>>>>> upstream/master
        }
    }
}
