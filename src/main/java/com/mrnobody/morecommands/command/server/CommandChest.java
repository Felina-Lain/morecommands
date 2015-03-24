package com.mrnobody.morecommands.command.server;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntityChest;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.command.CommandBase.Requirement;
import com.mrnobody.morecommands.command.CommandBase.ServerType;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Coordinate;
import com.mrnobody.morecommands.wrapper.Player;

import cpw.mods.fml.relauncher.Side;

@Command(
		name = "chest",
		description = "command.chest.description",
		example = "command.chest.example",
		syntax = "command.chest.syntax",
		videoURL = "command.chest.videoURL"
		)
public class CommandChest extends ServerCommand {

	@Override
	public String getCommandName() {
		return "chest";
	}

	@Override
	public String getUsage() {
		return "command.chest.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params)throws CommandException {
		if (params.length < 1) {
			sender.sendLangfileMessageToPlayer("command.chest.invalidUsage", new Object[0]);
			return;
		}
		
		Player player = sender.toPlayer();
		Coordinate coord = player.trace(128.0D);
		
		if (coord == null) {
			sender.sendLangfileMessageToPlayer("command.chest.noBlockInSight", new Object[0]);
			return;
		}
		
		Block block = player.getWorld().getBlock(coord);
		
        int x1 = coord.getBlockX();
        int y1 = coord.getBlockY();
        int z1 = coord.getBlockZ();
        int x2 = coord.getBlockX() + 1;
        int y2 = coord.getBlockY();
        int z2 = coord.getBlockZ();
		
		if (params[0].equalsIgnoreCase("drop")) {
            y1 += 1; y2 += 1;
			player.getWorld().setBlock(x1, y1, z1, Blocks.chest);
			player.getWorld().setBlock(x2, y2, z2, Blocks.chest);
		}
		else if (params[0].equalsIgnoreCase("fill") || params[0].equalsIgnoreCase("get") || params[0].equalsIgnoreCase("swap") || params[0].equalsIgnoreCase("clear")) {
			if (player.getWorld().getBlock(coord) == Blocks.chest) {
				if (player.getWorld().getBlock(x2, y2, z2) == Blocks.chest);
				else if (player.getWorld().getBlock(x1 - 1, y1, z1) == Blocks.chest) x2 = x1 - 1;
				else if (player.getWorld().getBlock(x1, y1, z1 + 1) == Blocks.chest) {x2 = x1; z2 = z1 + 1;}
				else if (player.getWorld().getBlock(x1, y1, z1 - 1) == Blocks.chest) {x2 = x1; z2 = z1 - 1;}
				else y2 = -1;
			}
			else {
				sender.sendLangfileMessageToPlayer("command.chest.noChest", new Object[0]);
				return;
			}
		}
		
		IInventory chest = null;
		
		if (y2 > -1) chest = new InventoryLargeChest("Large chest", (TileEntityChest) player.getWorld().getMinecraftWorld().getTileEntity(x1, y1, z1), (TileEntityChest) player.getWorld().getMinecraftWorld().getTileEntity(x2, y2, z2));
		else chest = (TileEntityChest) player.getWorld().getMinecraftWorld().getTileEntity(x1, y1, z1);
		
		if (params[0].equalsIgnoreCase("drop") || params[0].equalsIgnoreCase("fill")) {
			this.transferInventory(player.getMinecraftPlayer().inventory, chest);
		}
		else if (params[0].equalsIgnoreCase("get")) {
			this.transferInventory(chest, player.getMinecraftPlayer().inventory);
		}
		else if (params[0].equalsIgnoreCase("clear")) {
			this.transferInventory(chest, null);
		}
		else if (params[0].equalsIgnoreCase("swap")) {
            InventoryPlayer p = new InventoryPlayer(player.getMinecraftPlayer());
            for (int i = 0; i < p.getSizeInventory(); i++) {
               p.setInventorySlotContents(i, player.getMinecraftPlayer().inventory.getStackInSlot(i));
               player.getMinecraftPlayer().inventory.setInventorySlotContents(i, null);
            }
            this.transferInventory(chest, player.getMinecraftPlayer().inventory);
            this.transferInventory(p, chest);
		}
	}

	private void transferInventory(IInventory from, IInventory to) {
		int count = 0;
		
		if (from == null) return;
		
		try {
			for (int i = 0; i < from.getSizeInventory(); i++) {
				if (to == null) {
					from.setInventorySlotContents(i, null);
					continue;
				}
				
				try {
					while (to.getStackInSlot(count) != null) count++;
					
					if (count > to.getInventoryStackLimit()) break;
					
					to.setInventorySlotContents(count, from.getStackInSlot(i));
					from.setInventorySlotContents(i, null);
				} 
				catch (Exception e) {break;}
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	@Override
	public Requirement[] getRequirements() {
		return new Requirement[0];
	}
	
	@Override
	public void unregisterFromHandler() {}

	@Override
	public ServerType getAllowedServerType() {
		return ServerType.ALL;
	}
	
	@Override
	public int getPermissionLevel() {
		return 2;
	}
}