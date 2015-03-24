package com.mrnobody.morecommands.command.server;

import net.minecraft.util.ChatComponentTranslation;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.util.ServerPlayerSettings;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;


//MAKE COMMAND CLIENT SIDE TOO

@Command(
		name = "output",
		description = "command.output.description",
		example = "command.output.example",
		syntax = "command.output.syntax",
		videoURL = "command.output.videoURL"
		)
public class CommandOutput extends ServerCommand {

	@Override
	public String getCommandName() {
		return "output";
	}

	@Override
	public String getUsage() {
		return "command.output.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		ServerPlayerSettings settings = ServerPlayerSettings.playerSettingsMapping.get(sender.getMinecraftISender());
    	boolean output = false;
    	boolean success = false;
    	
    	if (params.length >= 1) {
    		if (params[0].toLowerCase().equals("true")) {output = true; success = true;}
    		else if (params[0].toLowerCase().equals("false")) {output = false; success = true;}
    		else if (params[0].toLowerCase().equals("0")) {output = false; success = true;}
    		else if (params[0].toLowerCase().equals("1")) {output = true; success = true;}
    		else if (params[0].toLowerCase().equals("on")) {output = true; success = true;}
    		else if (params[0].toLowerCase().equals("off")) {output = false; success = true;}
    		else {success = false;}
    	}
    	else {output = !settings.output; success = true;}
    	
    	if (success) settings.output = output;
    	sender.getMinecraftISender().addChatMessage(new ChatComponentTranslation(success ? output ? "command.output.on" : "command.output.off"  : "command.output.failure", new Object[0]));
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
		return 0;
	}
}