package com.mrnobody.morecommands.command.server;

import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.handler.EventHandler;
import com.mrnobody.morecommands.handler.Listener;
import com.mrnobody.morecommands.util.ReflectionHelper;
import com.mrnobody.morecommands.util.ServerPlayerSettings;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Coordinate;
import com.mrnobody.morecommands.wrapper.World;

@Command(
		name = "instantgrow",
		description = "command.instantgrow.description",
		example = "command.instantgrow.example",
		syntax = "command.instantgrow.syntax",
		videoURL = "command.instantgrow.videoURL"
		)
public class CommandInstantgrow extends ServerCommand implements Listener<PlaceEvent> {
	public CommandInstantgrow() {
		EventHandler.BLOCK_PLACEMENT.getHandler().register(this);
	}
	

	@Override
	public void onEvent(PlaceEvent event) {
		if (ServerPlayerSettings.playerSettingsMapping.containsKey(event.player) && ServerPlayerSettings.playerSettingsMapping.get(event.player).instantgrow) 
			this.growPlant(new World(event.world), event.x, event.y, event.z, new Random());
	}
	
	@Override
	public String getCommandName() {
		return "instantgrow";
	}

	@Override
	public String getUsage() {
		return "command.instantgrow.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		ServerPlayerSettings ability = ServerPlayerSettings.playerSettingsMapping.get(sender.getMinecraftISender());
    		
        boolean instant = false;
        boolean success = false;
        	
        if (params.length >= 1) {
        	if (params[0].toLowerCase().equals("true")) {instant = true; success = true;}
        	else if (params[0].toLowerCase().equals("false")) {instant = false; success = true;}
        	else if (params[0].toLowerCase().equals("0")) {instant = false; success = true;}
        	else if (params[0].toLowerCase().equals("1")) {instant = true; success = true;}
        	else if (params[0].toLowerCase().equals("on")) {instant = true; success = true;}
        	else if (params[0].toLowerCase().equals("off")) {instant = false; success = true;}
        	else {success = false;}
        }
        else {instant = !ability.instantgrow; success = true;}
        	
        if (success) ability.instantgrow = instant;
        	
        sender.sendLangfileMessageToPlayer(success ? instant ? "command.instantgrow.on" : "command.instantgrow.off" : "command.instantgrow.failure", new Object[0]);
	}
	
	private void growPlant(World world, int x, int y, int z, Random rand) {
		Block block = world.getBlock(x, y, z);
		
		if (block instanceof BlockSapling) {
			((BlockSapling) block).func_149878_d(world.getMinecraftWorld(), x, y, z, rand);
		}
		else if (block instanceof BlockCrops) {
			world.setBlockMeta(new Coordinate(x, y, z), 7);
		}
		else if (block instanceof BlockCactus || block instanceof BlockReed) {
			int length = 1;
			
			while (true) {
				int blen = length;
				
				if (world.getBlock(x, y + length, z) == block) length++;
				if (world.getBlock(x, y - length, z) == block) length++;
				
				if (blen == length) break;
			}
			
			if (length < 3) {
				for (int i = 0; i <= 3 - length; i++) {
					world.setBlock(x,  y + i, z, block);
				}
			}
		}
		else if (block instanceof BlockStem) {
			world.setBlockMeta(new Coordinate(x, y, z), 7);
			Field stemBlockField = ReflectionHelper.getField(BlockStem.class, "field_149877_a");
			
			if (stemBlockField != null) {
				try {
					Block stemBlock = (Block) stemBlockField.get(block);
					
                    if (world.getBlock(x - 1, y, z) == stemBlock) return;
                    if (world.getBlock(x + 1, y, z) == stemBlock) return;
                    if (world.getBlock(x, y, z - 1) == stemBlock) return;
                    if (world.getBlock(x, y, z + 1) == stemBlock) return;

                    int i = rand.nextInt(4);
                    int j = x;
                    int k = z;

                    if (i == 0) j = x - 1;
                    if (i == 1) ++j;
                    if (i == 2) k = z - 1;
                    if (i == 3) ++k;

                    Block b = world.getBlock(j, y - 1, k);

                    if (world.getMinecraftWorld().isAirBlock(j, y, k) && (b.canSustainPlant(world.getMinecraftWorld(), j, y - 1, k, ForgeDirection.UP, (BlockStem) block) || b == Blocks.dirt || b == Blocks.grass))
                    {
                        world.setBlock(j, y, k, stemBlock);
                    }
				}
				catch (Exception ex) {ex.printStackTrace();}
			}
		}
	}
	
	@Override
	public Requirement[] getRequirements() {
		return new Requirement[0];
	}
	
	@Override
	public void unregisterFromHandler() {
		EventHandler.BLOCK_PLACEMENT.getHandler().unregister(this);
	}

	@Override
	public ServerType getAllowedServerType() {
		return ServerType.ALL;
	}
	
	@Override
	public int getPermissionLevel() {
		return 2;
	}
}