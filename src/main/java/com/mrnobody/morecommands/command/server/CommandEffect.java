package com.mrnobody.morecommands.command.server;

import net.minecraft.potion.Potion;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.command.CommandBase.Requirement;
import com.mrnobody.morecommands.command.CommandBase.ServerType;
import com.mrnobody.morecommands.wrapper.Achievements;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Player;

import cpw.mods.fml.relauncher.Side;

@Command(
		name = "effect",
		description = "command.effect.description",
		example = "command.effect.example",
		syntax = "command.effect.syntax",
		videoURL = "command.effect.videoURL"
		)
public class CommandEffect extends ServerCommand {

	@Override
	public String getCommandName() {
		return "effect";
	}

	@Override
	public String getUsage() {
		return "command.effect.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		Player player = sender.toPlayer();
		
		if (params.length > 0) {
    		if(params[0].equals("list")) {
    			int page = 1;
    			int PAGE_MAX = 15;
    			boolean validParam = true;
    			
    			if (params.length > 1) {
    				try {page = Integer.parseInt(params[1]);} 
    				catch (NumberFormatException e) {validParam = false;}
    			}
    			
    			if (validParam) {
    				int to = PAGE_MAX * page <= Potion.potionTypes.length ? PAGE_MAX * page : Potion.potionTypes.length;
    				int from = to - PAGE_MAX;
    				
    				for (int index = from; index < to; index++) {
    					if (Potion.potionTypes[index] != null) sender.sendStringMessageToPlayer(" - '" + Potion.potionTypes[index].getName().substring(7) + "' (" + String.valueOf(Potion.potionTypes[index].getId()) + ")");
    				}
    				sender.sendLangfileMessageToPlayer("command.effect.more", new Object[0]);
    			}
    			else {sender.sendLangfileMessageToPlayer("command.effect.invalidUsage", new Object[0]);}
    		}
    		
    		else if (params[0].equals("remove")) {
    			if (params.length > 1) {
    				boolean broken = false;
    				
    				for (Potion p : Potion.potionTypes) {
    					if (p != null) {
    						if (params[1].toLowerCase().equals(p.getName().substring(7).toLowerCase()) || String.valueOf(p.getId()).equals(params[1])) {
    							player.removePotionEffect(p.getId()); sender.sendLangfileMessageToPlayer("command.effect.removeSuccess", new Object[0]); broken = true; break;
    						}
    					}
    				}
    				if (!broken) sender.sendLangfileMessageToPlayer("command.effect.removeFailure", new Object[0]);
    			}
    			else {sender.sendLangfileMessageToPlayer("command.effect.invalidUsage", new Object[0]);}
    		}
    		
    		else if (params[0].equals("removeAll")) {player.removeAllPotionEffects(); sender.sendLangfileMessageToPlayer("command.effect.removeAllSuccess", new Object[0]);}
    		
    		else if (params[0].equals("add")) {
    			if (params.length > 3) {
    				boolean broken = false;
    				
    				for (Potion p : Potion.potionTypes) {
    					if (p != null) {
    						if (params[1].toLowerCase().equals(p.getName().substring(7).toLowerCase()) || String.valueOf(p.getId()).equals(params[1])) {
    							try {player.addPotionEffect(p.getId(), Integer.parseInt(params[2]), Integer.parseInt(params[3])); broken = true; sender.sendLangfileMessageToPlayer("command.effect.addSuccess", new Object[0]);}
    							catch (NumberFormatException e) {sender.sendLangfileMessageToPlayer("command.effect.NAN", new Object[0]); broken = true;}
    							break;
    						}
    					}
    				}
    				if (!broken) sender.sendLangfileMessageToPlayer("command.effect.notFound", new Object[0]);
    			}
    			else {sender.sendLangfileMessageToPlayer("command.effect.invalidUsage", new Object[0]);}
    		}
    		else {sender.sendLangfileMessageToPlayer("command.effect.invalidUsage", new Object[0]);}
		}
		else {sender.sendLangfileMessageToPlayer("command.effect.invalidUsage", new Object[0]);}
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