package com.mrnobody.morecommands.command.server;

import java.util.HashMap;
import java.util.Map;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;

@Command(
		name = "command",
		description = "command.command.description",
		example = "command.command.example",
		syntax = "command.command.syntax",
		videoURL = "command.command.videoURL"
		)
public class CommandCommand extends ServerCommand {
	private Map<String, ICommand> disabledCommands = new HashMap<String, ICommand>();
	
	@Override
    public String getName()
    {
        return "command";
    }

	@Override
    public String getUsage()
    {
        return "command.command.syntax";
    }

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		if (params.length > 1) {
			if (params[0].equalsIgnoreCase("enable")) {
				ICommand enable = this.disabledCommands.get(params[1]);
				
				if (enable != null) {
					((ServerCommandManager) MinecraftServer.getServer().getCommandManager()).registerCommand(enable);
					this.disabledCommands.remove(params[1]);
					sender.sendLangfileMessage("command.command.enabled");
				}
				else throw new CommandException("command.command.alreadyEnabled", sender);
			}
			else if (params[0].equalsIgnoreCase("disable")) {
				if (params[1].equals(this.getName())) throw new CommandException("command.command.wantedToDisable", sender);
				
				ICommand disable = (ICommand) ((ServerCommandManager) MinecraftServer.getServer().getCommandManager()).getCommands().get(params[1]);
				
				if (disable != null) {
					this.disabledCommands.put(disable.getName(), disable);
					((ServerCommandManager) MinecraftServer.getServer().getCommandManager()).getCommands().remove(params[1]);
					sender.sendLangfileMessage("command.command.disabled");
				}
				else throw new CommandException("command.command.alreadyDisabled", sender);
			}
			else throw new CommandException("command.command.invalidUsage", sender);
		}
		else throw new CommandException("command.command.invalidUsage", sender);
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
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public void unregisterFromHandler() {}

	@Override
	public boolean canSenderUse(ICommandSender sender) {
		return true;
	}
}
