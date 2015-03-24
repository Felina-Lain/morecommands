package com.mrnobody.morecommands.command.server;

import net.minecraft.entity.player.EntityPlayer;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.command.CommandBase.Requirement;
import com.mrnobody.morecommands.command.CommandBase.ServerType;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

import cpw.mods.fml.relauncher.Side;

@Command(
		name = "refill",
		description = "command.refill.description",
		example = "command.refill.example",
		syntax = "command.refill.syntax",
		videoURL = "command.refill.videoURL"
		)
public class CommandRefill extends ServerCommand {

	@Override
	public String getCommandName() {
		return "refill";
	}

	@Override
	public String getUsage() {
		return "command.refill.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params)throws CommandException {
		EntityPlayer player = sender.toPlayer().getMinecraftPlayer();
		
		if (params.length > 0 && params[0].equalsIgnoreCase("all")) {
			for (int i = 0; i < player.inventory.mainInventory.length; i++) {
				if (player.inventory.mainInventory[i] != null) 
					player.inventory.mainInventory[i].stackSize = player.inventory.mainInventory[i].getMaxStackSize();
			}
		}
		else {
			if (player.inventory.mainInventory[player.inventory.currentItem] != null) 
				player.inventory.mainInventory[player.inventory.currentItem].stackSize = player.inventory.mainInventory[player.inventory.currentItem].getMaxStackSize();
			else
				{sender.sendLangfileMessageToPlayer("command.refill.noSelection", new Object[0]); return;}
		}
		
		sender.sendLangfileMessageToPlayer("command.refill.refilled", new Object[0]);
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