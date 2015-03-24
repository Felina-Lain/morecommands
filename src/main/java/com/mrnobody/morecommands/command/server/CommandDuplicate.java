package com.mrnobody.morecommands.command.server;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.command.CommandBase.Requirement;
import com.mrnobody.morecommands.command.CommandBase.ServerType;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

import cpw.mods.fml.relauncher.Side;

@Command(
		name = "duplicate",
		description = "command.duplicate.description",
		example = "command.duplicate.example",
		syntax = "command.duplicate.syntax",
		videoURL = "command.duplicate.videoURL"
		)
public class CommandDuplicate extends ServerCommand {

	@Override
	public String getCommandName() {
		return "duplicate";
	}

	@Override
	public String getUsage() {
		return "command.duplicate.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		EntityPlayer player = sender.toPlayer().getMinecraftPlayer();
		
		if (params.length > 0 && params[0].equalsIgnoreCase("all")) {
			for (int i = 0; i < player.inventory.mainInventory.length; i++) {
				if (player.inventory.mainInventory[i] == null) continue;
				
				ItemStack item = player.inventory.mainInventory[i];
				ItemStack duplicate = new ItemStack(item.getItem(), item.stackSize, item.getItemDamage());
				
				EntityItem itemEntity = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, duplicate);
				player.worldObj.spawnEntityInWorld(itemEntity);
			}
			
			for (int i = 0; i < player.inventory.armorInventory.length; i++) {
				if (player.inventory.armorInventory[i] == null) continue;
				
				ItemStack item = player.inventory.armorInventory[i];
				ItemStack duplicate = new ItemStack(item.getItem(), item.stackSize, item.getItemDamage());
				
				EntityItem itemEntity = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, duplicate);
				player.worldObj.spawnEntityInWorld(itemEntity);
			}
			
			
		}
		else {
			if (player.inventory.mainInventory[player.inventory.currentItem] == null) {
				sender.sendLangfileMessageToPlayer("command.duplicate.notSelected", new Object[0]);
				return;
			}
			
			ItemStack item = player.inventory.mainInventory[player.inventory.currentItem];
			ItemStack duplicate = new ItemStack(item.getItem(), item.stackSize, item.getItemDamage());
			
			EntityItem itemEntity = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, duplicate);
			player.worldObj.spawnEntityInWorld(itemEntity);
		}
		
		sender.sendLangfileMessageToPlayer("command.duplicate.duplicated", new Object[0]);
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