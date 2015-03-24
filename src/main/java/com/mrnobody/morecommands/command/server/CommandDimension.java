package com.mrnobody.morecommands.command.server;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.command.CommandBase.Requirement;
import com.mrnobody.morecommands.command.CommandBase.ServerType;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Player;

import cpw.mods.fml.relauncher.Side;

@Command(
		name = "dimension",
		description = "command.dimension.description",
		example = "command.dimension.example",
		syntax = "command.dimension.syntax",
		videoURL = "command.dimension.videoURL"
		)
public class CommandDimension extends ServerCommand {
	private final int DIMENSION_SURFACE = 0;
	private final int DIMENSION_NETHER = -1;
	private final int DIMENSION_END = 1;

	@Override
	public String getCommandName() {
		return "dimension";
	}

	@Override
	public String getUsage() {
		return "command.dimension.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params)throws CommandException {
		if (params.length > 0) {
			Player player = sender.toPlayer();
			boolean error = false;
		
			if (params[0].equalsIgnoreCase("normal") || params[0].equalsIgnoreCase("surface") || params[0].equalsIgnoreCase(String.valueOf(this.DIMENSION_SURFACE))) player.changeDimension(this.DIMENSION_SURFACE);
			else if (params[0].equalsIgnoreCase("nether") || params[0].equalsIgnoreCase(String.valueOf(this.DIMENSION_NETHER))) player.changeDimension(this.DIMENSION_NETHER);
			else if (params[0].equalsIgnoreCase("end") || params[0].equalsIgnoreCase(String.valueOf(this.DIMENSION_END))) player.changeDimension(this.DIMENSION_END);
			else error = true;
			
			if (!error) sender.sendLangfileMessageToPlayer("command.dimension.changed", new Object[0]);
			else sender.sendLangfileMessageToPlayer("command.dimension.unknown", new Object[0]);
		} else sender.sendLangfileMessageToPlayer("command.dimension.notSpecified", new Object[0]);
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