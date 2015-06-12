package com.mrnobody.morecommands.command.server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.handler.EventHandler;
import com.mrnobody.morecommands.handler.Listeners.TwoEventListener;
import com.mrnobody.morecommands.util.ServerPlayerSettings;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

@Command(
		name = "infiniteitems",
		description = "command.infiniteitems.description",
		example = "command.infiniteitems.example",
		syntax = "command.infiniteitems.syntax",
		videoURL = "command.infiniteitems.videoURL"
		)
public class CommandInfiniteitems extends ServerCommand implements TwoEventListener<PlaceEvent, PlayerDestroyItemEvent>{
	public void onEvent1(PlaceEvent event) {
		this.onPlace(event);
	}
	  
	public void onEvent2(PlayerDestroyItemEvent event) {
		this.onDestroy(event);
	}
	
	private class Stack {
		private EntityPlayer player;
		private int stack;
		private boolean incr;
	    
		private Stack(EntityPlayer player, int stack) {
			this.player = player;
			this.stack = stack;
			this.incr = (player.inventory.mainInventory[stack].stackSize > 1);
		}
	}
	
	private class StackObserver extends Thread {
		private long lastTime = System.currentTimeMillis();
	    
		@Override
		public void run() {
			Stack stack;
			
			while (!this.isInterrupted()) {
				try {
					if (this.lastTime < System.currentTimeMillis()) {
						
						while ((stack = stacks.poll()) != null) {
							if (stack.player.inventory.mainInventory[stack.stack] != null && stack.incr)
								stack.player.inventory.mainInventory[stack.stack].stackSize += 1;
						}
						
						this.lastTime = System.currentTimeMillis();
					}
				}
				catch (Exception ex) {}
			}
		}
	}
	
	private Queue<Stack> stacks;
	private StackObserver observer;
	
	public CommandInfiniteitems() {
		EventHandler.PLACE.getHandler().register(this, true);
		EventHandler.DESTROY.getHandler().register(this, false);
		this.stacks = new ConcurrentLinkedQueue<Stack>();
		this.observer = new StackObserver();
		this.observer.start();
	}
	
	public void onPlace(PlaceEvent event) {
		if (ServerPlayerSettings.playerSettingsMapping.containsKey(event.player)
			&& ServerPlayerSettings.playerSettingsMapping.get(event.player).infiniteitems
			&& event.player.getCurrentEquippedItem() != null)
			this.stacks.offer(new Stack(event.player, event.player.inventory.currentItem));
	  }
	  
	public void onDestroy(PlayerDestroyItemEvent event) {
		if (ServerPlayerSettings.playerSettingsMapping.containsKey(event.entityPlayer)
			&& ServerPlayerSettings.playerSettingsMapping.get(event.entityPlayer).infiniteitems
			&& event.original != null && event.original.stackSize < 1) event.original.stackSize += 1;
	}
	
	@Override
	public String getName() {
		return "infiniteitems";
	}

	@Override
	public String getUsage() {
		return "command.infiniteitems.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		EntityPlayerMP player = (EntityPlayerMP) sender.getMinecraftISender();
		ServerPlayerSettings settings = ServerPlayerSettings.playerSettingsMapping.get(sender.getMinecraftISender());
    	
        if (params.length > 0) {
        	if (params[0].equalsIgnoreCase("enable") || params[0].equalsIgnoreCase("1")
            	|| params[0].equalsIgnoreCase("on") || params[0].equalsIgnoreCase("true")) {
        		settings.infiniteitems = true;
            	sender.sendLangfileMessage("command.infiniteitems.on");
            }
            else if (params[0].equalsIgnoreCase("disable") || params[0].equalsIgnoreCase("0")
            		|| params[0].equalsIgnoreCase("off") || params[0].equalsIgnoreCase("false")) {
            	settings.infiniteitems = false;
            	sender.sendLangfileMessage("command.infiniteitems.off");
            }
            else throw new CommandException("command.infiniteitems.failure", sender);
        }
        else {
        	settings.infiniteitems = !settings.infiniteitems;
        	sender.sendLangfileMessage(settings.infiniteitems ? "command.infiniteitems.on" : "command.infiniteitems.off");
        }
	}
	
	@Override
	public Requirement[] getRequirements() {
		return new Requirement[0];
	}
	
	@Override
	public void unregisterFromHandler() {
		EventHandler.PLACE.getHandler().unregister(this);
		EventHandler.DESTROY.getHandler().unregister(this);
		this.observer.interrupt();
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
	public boolean canSenderUse(ICommandSender sender) {
		return sender instanceof EntityPlayerMP;
	}
}
