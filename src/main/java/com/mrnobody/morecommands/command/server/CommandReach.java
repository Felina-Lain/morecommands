package com.mrnobody.morecommands.command.server;

import net.minecraft.entity.player.EntityPlayerMP;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.core.MoreCommands;
import com.mrnobody.morecommands.packet.server.S08PacketReach;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Player;

@Command(
		name = "reach",
		description = "command.reach.description",
		example = "command.reach.example",
		syntax = "command.reach.syntax",
		videoURL = "command.reach.videoURL"
		)
public class CommandReach extends ServerCommand {
	@Override
	public String getCommandName() {
		return "reach";
	}

	@Override
	public String getUsage() {
		return "command.reach.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		EntityPlayerMP playerEntity = (EntityPlayerMP) sender.toPlayer().getMinecraftPlayer();
			
		if (params.length > 0) {
			try {
				float distance = Float.parseFloat(params[0]);
				
				S08PacketReach packet = new S08PacketReach();
				packet.reachDistance = distance;
				MoreCommands.getNetwork().sendTo(packet, playerEntity);
				
				playerEntity.theItemInWorldManager.setBlockReachDistance(distance);
				sender.sendLangfileMessageToPlayer("command.reach.set", new Object[] {params[0]});
			}
			catch (NumberFormatException e) {
				if (params[0].toLowerCase().equals("reset")) {
					S08PacketReach packet = new S08PacketReach();
					packet.reachDistance = 5.0F;
					MoreCommands.getNetwork().sendTo(packet, playerEntity);
					
					playerEntity.theItemInWorldManager.setBlockReachDistance(5.0F);
					sender.sendLangfileMessageToPlayer("command.reach.reset", new Object[0]);
				}
				else {sender.sendLangfileMessageToPlayer("command.reach.invalidArg", new Object[0]);}
			}
		}
	}
	
	@Override
	public Requirement[] getRequirements() {
		return new Requirement[] {Requirement.MODDED_CLIENT, Requirement.PATCH_ENTITYCLIENTPLAYERMP};
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