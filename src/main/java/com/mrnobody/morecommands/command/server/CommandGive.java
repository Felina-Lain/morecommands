package com.mrnobody.morecommands.command.server;

import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.patch.EntityPlayerMP;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Player;

@Command(
		name = "give",
		description = "command.give.description",
		example = "command.give.example",
		syntax = "command.give.syntax",
		videoURL = "command.give.videoURL"
		)
public class CommandGive extends ServerCommand {

	@Override
	public String getName() {
		return "give";
	}

	@Override
	public String getUsage() {
		return "command.give.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params)throws CommandException {
		if (params.length > 0) {
			Player player = new Player((EntityPlayerMP) sender.getMinecraftISender());
			Item item = (Item)Item.itemRegistry.getObject(params[0].toLowerCase().startsWith("minecraft:") ? params[0].toLowerCase() : "minecraft:" + params[0].toLowerCase());
			
			if (item == null) {
				try {item = Item.getItemById(Integer.parseInt(params[0]));}
				catch (NumberFormatException e) {}
			}
			
			if (item != null) {
				if (params.length > 1) {
					if (params.length > 2) {
						if (item.getHasSubtypes()) {
							try {player.givePlayerItem(item, Integer.parseInt(params[1]), Integer.parseInt(params[2]));}
							catch(NumberFormatException e) {throw new CommandException("command.give.notFound", sender);}
						}
						else throw new CommandException("command.give.noMeta", sender);
					}
					else {
						try {player.givePlayerItem(item, Integer.parseInt(params[1])); sender.sendLangfileMessage("command.give.success");}
						catch (NumberFormatException e) {throw new CommandException("command.give.notFound", sender);}
					}
				}
				else {player.givePlayerItem(item); sender.sendLangfileMessage("command.give.success");}
			}
			else throw new CommandException("command.give.notFound", sender);
		}
		else throw new CommandException("command.give.invalidUsage", sender);
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
	
	@Override
	public boolean canSenderUse(ICommandSender sender) {
		return sender instanceof EntityPlayerMP;
	}
}
