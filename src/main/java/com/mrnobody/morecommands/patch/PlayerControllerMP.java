package com.mrnobody.morecommands.patch;

import com.mrnobody.morecommands.util.EntityCamera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

/**
 * The patched class of {@link net.minecraft.client.multiplayer.PlayerControllerMP} <br>
 * This class sets the client player ({@link Minecraft#thePlayer}), which is the actual target
 * to be modified. <br> By modifying the {@link PlayerControllerMP#func_147493_a(World, StatFileWriter)}
 * method, a custom patched class of {@link EntityClientPlayerMP} is used. Another point is that
 * this class determines the block reach distance, which can be modified by the reach command.
 * 
 * @author MrNobody98
 *
 */
public class PlayerControllerMP extends net.minecraft.client.multiplayer.PlayerControllerMP {
	private NetHandlerPlayClient netClientHandler;
	private Minecraft mc;
	private int currentPlayerItem;
	
	private float reachDistance = 5.0F;
	
	public PlayerControllerMP(Minecraft mc, NetHandlerPlayClient netClientHandler) {
		super(mc, netClientHandler);
		this.netClientHandler = netClientHandler;
		this.mc = mc;
	}
	
	@Override
    public float getBlockReachDistance()
    {
        return this.reachDistance;
    }
	
    public void setBlockReachDistance(float distance)
    {
    	this.reachDistance = distance;
    }
    
    @Override
    public EntityPlayerSP func_178892_a(World worldIn, StatFileWriter p_178892_2_)
    {
    	return new com.mrnobody.morecommands.patch.EntityPlayerSP(this.mc, worldIn, this.netClientHandler, p_178892_2_);
    }
    
    @Override
    public void attackEntity(EntityPlayer player, Entity target) {
    	if (!(Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityCamera))
    			super.attackEntity(player, target);
    }
}
