package com.mrnobody.morecommands.command.server;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.CommandRequirement;
import com.mrnobody.morecommands.command.ServerCommandProperties;
import com.mrnobody.morecommands.command.StandardCommand;
import com.mrnobody.morecommands.core.MoreCommands.ServerType;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Coordinate;
import com.mrnobody.morecommands.wrapper.Player;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntityChest;

@Command(
		name = "dropstore",
		description = "command.dropstore.description",
		example = "command.dropstore.example",
		syntax = "command.dropstore.syntax",
		videoURL = "command.dropstore.videoURL"
		)
public class CommandDropstore extends StandardCommand implements ServerCommandProperties {

	@Override
	public String getCommandName() {
		return "dropstore";
	}

	@Override
	public String getUsage() {
		return "command.dropstore.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params)throws CommandException {
		Player player = new Player(getSenderAsEntity(sender.getMinecraftISender(), EntityPlayerMP.class));
		Coordinate coord1 = new Coordinate(player.getPosition().getX() + 1, player.getPosition().getY(), player.getPosition().getZ());
		Coordinate coord2 = new Coordinate(player.getPosition().getX() + 1, player.getPosition().getY(), player.getPosition().getZ() + 1);
		
		player.getWorld().setBlock(coord1, Blocks.chest);
		player.getWorld().setBlock(coord2, Blocks.chest);
		
		InventoryLargeChest chestInv = new InventoryLargeChest("Large chest", (TileEntityChest) player.getWorld().getTileEntity(coord1), (TileEntityChest) player.getWorld().getTileEntity(coord2));
	
        int count = 0;
        for (int i = 0; i < player.getMinecraftPlayer().inventory.mainInventory.length; i++) {
        	chestInv.setInventorySlotContents(count++, player.getMinecraftPlayer().inventory.mainInventory[i]);
        	player.getMinecraftPlayer().inventory.mainInventory[i] = null;
        }
        
        for (int i = 0; i < player.getMinecraftPlayer().inventory.armorInventory.length; i++) {
        	chestInv.setInventorySlotContents(count++, player.getMinecraftPlayer().inventory.armorInventory[i]);
        	player.getMinecraftPlayer().inventory.armorInventory[i] = null;
        }
        
        sender.sendLangfileMessage("command.dropstore.stored");
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
	public int getDefaultPermissionLevel() {
		return 2;
	}
	
	@Override
	public boolean canSenderUse(String commandName, ICommandSender sender, String[] params) {
		return isSenderOfEntityType(sender, EntityPlayerMP.class);
	}
}
