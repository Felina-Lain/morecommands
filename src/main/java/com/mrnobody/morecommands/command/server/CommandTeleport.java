package com.mrnobody.morecommands.command.server;

import java.text.DecimalFormat;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.util.ServerPlayerSettings;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Coordinate;

@Command(
		name = "teleport",
		description = "command.teleport.description",
		example = "command.teleport.example",
		syntax = "command.teleport.syntax",
		videoURL = "command.teleport.videoURL"
		)
public class CommandTeleport extends ServerCommand {
	@Override
	public String getCommandName() {
		return "teleport";
	}

	@Override
	public String getUsage() {
		return "command.teleport.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		ServerPlayerSettings settings = ServerPlayerSettings.playerSettingsMapping.get(sender.getMinecraftISender());
		
		if (params.length > 2) {
			try {
				Coordinate coord = new Coordinate(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
				settings.lastPos = sender.toPlayer().getPosition();
				sender.toPlayer().setPosition(coord);
				DecimalFormat f = new DecimalFormat("#.##");
				
				sender.sendStringMessageToPlayer("Successfully teleported to:"
						+ " X = " + f.format(coord.getX())
						+ "; Y = " + f.format(coord.getY())
						+ "; Z = " + f.format(coord.getZ()));
			}
			catch (NumberFormatException nfe) {sender.sendLangfileMessageToPlayer("command.teleport.NAN", new Object[0]);}
		}
		else {sender.sendLangfileMessageToPlayer("command.teleport.invalidParams", new Object[0]);}
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