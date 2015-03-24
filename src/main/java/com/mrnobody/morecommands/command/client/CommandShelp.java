package com.mrnobody.morecommands.command.client;

import net.minecraft.client.Minecraft;

import com.mrnobody.morecommands.command.ClientCommand;
import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

@Command(
		name = "shelp",
		description = "command.shelp.description",
		example = "command.shelp.example",
		syntax = "command.shelp.syntax",
		videoURL = "command.shelp.videoURL"
		)
public class CommandShelp extends ClientCommand {
	@Override
	public String getCommandName() {
		return "shelp";
	}

	@Override
	public String getUsage() {
		return "command.shelp.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		String args = "";
		for (String param : params) args += " " + param;
		Minecraft.getMinecraft().thePlayer.sendChatMessage("/help" + args);
	}
	
	@Override
	public Requirement[] getRequirements() {
		return new Requirement[0];
	}

	@Override
	public ServerType getAllowedServerType() {
		return ServerType.ALL;
	}
	
	@Override
	public boolean registerIfServerModded() {
		return true;
	}
	
	@Override
	public int getPermissionLevel() {
		return 0;
	}
}