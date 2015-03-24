package com.mrnobody.morecommands.command.server;

import net.minecraft.entity.player.EntityPlayerMP;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.command.CommandBase.Requirement;
import com.mrnobody.morecommands.command.CommandBase.ServerType;
import com.mrnobody.morecommands.core.MoreCommands;
import com.mrnobody.morecommands.core.Patcher;
import com.mrnobody.morecommands.packet.server.S05PacketXray;
import com.mrnobody.morecommands.util.ServerPlayerSettings;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

import cpw.mods.fml.relauncher.Side;

@Command(
		name = "xray",
		description = "command.xray.description",
		example = "command.xray.example",
		syntax = "command.xray.syntax",
		videoURL = "command.xray.videoURL"
		)
public class CommandXray extends ServerCommand {

	@Override
	public String getCommandName() {
		return "xray";
	}

	@Override
	public String getUsage() {
		return "command.xray.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		EntityPlayerMP player = (EntityPlayerMP) sender.toPlayer().getMinecraftPlayer();
		ServerPlayerSettings ability = ServerPlayerSettings.playerSettingsMapping.get(sender.getMinecraftISender());
		
		boolean showGUI = false;
		int blockRadius = ability.xrayBlockRadius;
		boolean enable = ability.xrayEnabled;
		
		if (params.length > 0) {
			if (params[0].equalsIgnoreCase("config")) {showGUI = true;}
			else if (params[0].equalsIgnoreCase("radius") && params.length > 1) {
				try {blockRadius = Integer.parseInt(params[1]);}
				catch (NumberFormatException nfe) {sender.sendLangfileMessageToPlayer("command.xray.invalidUsage", new Object[0]);}
			}
			else if (params[0].equalsIgnoreCase("enable") || params[0].equalsIgnoreCase("on") || params[0].equalsIgnoreCase("1")) {
				enable = true; 
				sender.sendLangfileMessageToPlayer("command.xray.enabled", new Object[0]);
			}
			else if (params[0].equalsIgnoreCase("disable") || params[0].equalsIgnoreCase("off") || params[0].equalsIgnoreCase("0")) {
				enable = false; 
				sender.sendLangfileMessageToPlayer("command.xray.disabled", new Object[0]);
			}
			else {sender.sendLangfileMessageToPlayer("command.xray.invalidUsage", new Object[0]);}
		}
		else {
			enable = !enable; 
			sender.sendLangfileMessageToPlayer(enable ? "command.xray.enabled" : "command.xray.disabled", new Object[0]);
		}
		
		ability.xrayBlockRadius = blockRadius;
		ability.xrayEnabled = enable;
		
		S05PacketXray packet = new S05PacketXray();
		
		packet.showConfig = showGUI;
		packet.blockRadius = blockRadius;
		packet.xrayEnabled = enable;
		
		MoreCommands.getNetwork().sendTo(packet, player);
	}
	
	@Override
	public Requirement[] getRequirements() {
		return new Requirement[] {Requirement.MODDED_CLIENT};
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