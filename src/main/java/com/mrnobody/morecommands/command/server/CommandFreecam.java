package com.mrnobody.morecommands.command.server;

import net.minecraft.entity.player.EntityPlayerMP;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.command.CommandBase.Requirement;
import com.mrnobody.morecommands.command.CommandBase.ServerType;
import com.mrnobody.morecommands.core.MoreCommands;
import com.mrnobody.morecommands.core.Patcher;
import com.mrnobody.morecommands.packet.server.S03PacketFreecam;
import com.mrnobody.morecommands.util.ServerPlayerSettings;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

import cpw.mods.fml.relauncher.Side;

@Command(
		name = "freecam",
		description = "command.freecam.description",
		example = "command.freecam.example",
		syntax = "command.freecam.syntax",
		videoURL = "command.freecam.videoURL"
		)
public class CommandFreecam extends ServerCommand {
	@Override
	public String getCommandName() {
		return "freecam";
	}

	@Override
	public String getUsage() {
		return "command.freecam.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		ServerPlayerSettings ability = ServerPlayerSettings.playerSettingsMapping.get(sender.getMinecraftISender());
		
		if (ability.freecam) {
    		S03PacketFreecam packet = new S03PacketFreecam();
    		MoreCommands.getNetwork().sendTo(packet, (EntityPlayerMP) sender.toPlayer().getMinecraftPlayer());
			
			ability.freecam = false;
			sender.sendLangfileMessageToPlayer("command.freecam.off", new Object[0]);
		}
		else {
    		S03PacketFreecam packet = new S03PacketFreecam();
    		MoreCommands.getNetwork().sendTo(packet, (EntityPlayerMP) sender.toPlayer().getMinecraftPlayer());
			
			ability.freecam = true;
            sender.sendLangfileMessageToPlayer("command.freecam.on", new Object[0]);
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