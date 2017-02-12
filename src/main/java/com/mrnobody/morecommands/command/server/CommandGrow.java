package com.mrnobody.morecommands.command.server;

import java.lang.reflect.Field;
import java.util.Random;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.CommandException;
import com.mrnobody.morecommands.command.CommandRequirement;
import com.mrnobody.morecommands.command.CommandSender;
import com.mrnobody.morecommands.command.ServerCommandProperties;
import com.mrnobody.morecommands.command.StandardCommand;
import com.mrnobody.morecommands.core.MoreCommands.ServerType;
import com.mrnobody.morecommands.util.ObfuscatedNames.ObfuscatedField;
import com.mrnobody.morecommands.util.Coordinate;
import com.mrnobody.morecommands.util.ReflectionHelper;
import com.mrnobody.morecommands.util.WorldUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@Command(
		name = "grow",
		description = "command.grow.description",
		example = "command.grow.example",
		syntax = "command.grow.syntax",
		videoURL = "command.grow.videoURL"
		)
public class CommandGrow extends StandardCommand implements ServerCommandProperties {
	private final Field field_149877_a = ReflectionHelper.getField(ObfuscatedField.BlockStem_field_149877_a);
	
	@Override
	public String getCommandName() {
		return "grow";
	}

	@Override
	public String getCommandUsage() {
		return "command.grow.syntax";
	}

	@Override
	public String execute(CommandSender sender, String[] params) throws CommandException {
		int radius = 16;
		
		if (params.length > 0) {
			try {radius = Integer.parseInt(params[0]);}
			catch (NumberFormatException nfe) {throw new CommandException("command.grow.invalidArg", sender);}
		}
		
		Coordinate pos = sender.getPosition();
		World world = sender.getWorld();
		Random rand = new Random();
		
		for (int i = 0; i < radius; i++) {
			for (int j = 0; j < radius; j++) {
				if (pos.getBlockY() - j < 0 || pos.getBlockY() + j > 256) continue;
				
				for (int k = 0; k < radius; k++) {
					this.growPlant(world, pos.getBlockX() + i, pos.getBlockY() + j, pos.getBlockZ() + k, rand);
					this.growPlant(world, pos.getBlockX() - i, pos.getBlockY() + j, pos.getBlockZ() + k, rand);
					this.growPlant(world, pos.getBlockX() - i, pos.getBlockY() + j, pos.getBlockZ() - k, rand);
					this.growPlant(world, pos.getBlockX() + i, pos.getBlockY() + j, pos.getBlockZ() - k, rand);
					this.growPlant(world, pos.getBlockX() + i, pos.getBlockY() - j, pos.getBlockZ() + k, rand);
					this.growPlant(world, pos.getBlockX() - i, pos.getBlockY() - j, pos.getBlockZ() + k, rand);
					this.growPlant(world, pos.getBlockX() - i, pos.getBlockY() - j, pos.getBlockZ() - k, rand);
					this.growPlant(world, pos.getBlockX() + i, pos.getBlockY() - j, pos.getBlockZ() - k, rand);
				}
			}
		}
		
		sender.sendLangfileMessage("command.grow.grown");
		return null;
	}
	
	private void growPlant(World world, int x, int y, int z, Random rand) {
		Block block = world.getBlock(x, y, z);
		
		if (block instanceof BlockSapling) {
			((BlockSapling) block).func_149853_b(world, rand, x, y, z);
		}
		else if (block instanceof BlockCrops) {
			WorldUtils.setBlockMeta(world, new Coordinate(x, y, z), 7);
			((BlockCrops) block).func_149853_b(world, rand, x, y, z);
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
			WorldUtils.setBlockMeta(world, new Coordinate(x, y, z), 7);
			Block stemBlock = ReflectionHelper.get(ObfuscatedField.BlockStem_field_149877_a, field_149877_a, (BlockStem) block);
			
			if (stemBlock != null) {
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

                if (world.isAirBlock(j, y, k) && (b.canSustainPlant(world, j, y - 1, k, ForgeDirection.UP, (BlockStem) block) || b == Blocks.dirt || b == Blocks.grass))
                {
                    world.setBlock(j, y, k, stemBlock);
                }
			}
		}
	}
	
	@Override
	public CommandRequirement[] getRequirements() {
		return new CommandRequirement[0];
	}

	@Override
	public ServerType getAllowedServerType() {
		return ServerType.ALL;
	}
	
	@Override
	public int getDefaultPermissionLevel(String[] args) {
		return 2;
	}
	
	@Override
	public boolean canSenderUse(String commandName, ICommandSender sender, String[] params) {
		return true;
	}
}
