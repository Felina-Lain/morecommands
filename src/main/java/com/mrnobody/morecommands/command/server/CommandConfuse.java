package com.mrnobody.morecommands.command.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

@Command(
		name = "confuse",
		description = "command.confuse.description",
		example = "command.confuse.example",
		syntax = "command.confuse.syntax",
		videoURL = "command.confuse.videoURL"
		)
public class CommandConfuse extends ServerCommand {
	private final double RADIUS_MAX = 50;
	
	@Override
	public String getCommandName() {
		return "confuse";
	}

	@Override
	public String getUsage() {
		return "command.confuse.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		double radius = 10D;
		
		if (params.length > 0) {
			try {radius = Double.parseDouble(params[0]);}
			catch (NumberFormatException nfe) {sender.sendLangfileMessageToPlayer("command.confuse.invalidArg", new Object[0]); return;}
			if (radius > this.RADIUS_MAX) {sender.sendLangfileMessageToPlayer("command.confuse.invalidRadius", new Object[0]); return;}
		}
		
		List<Entity> entities = new ArrayList<Entity>();
		EntityCreature creature;
		
		entities = this.getEntitiesInRadius(sender.toPlayer().getMinecraftPlayer(), sender.toPlayer().getWorld().getMinecraftWorld(), EntityCreature.class, radius * radius);
		
		for (int index = 1; index < entities.size(); index++) {
			((EntityCreature) entities.get(index)).setTarget(entities.get(index - 1));
		}
        
		sender.sendLangfileMessageToPlayer("command.confuse.confused", new Object[] {entities.size(), radius});
	}
	
	private List<Entity> getEntitiesInRadius(final EntityPlayer player, World world, Class<?> class1, double d) {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		
		for (int i = 0; i < world.loadedEntityList.size(); i++) {
			Entity entity = (Entity) world.loadedEntityList.get(i);
			if (entity != player && !entity.isDead && class1.isInstance(entity) && (d <= 0.0D || player.getDistanceSqToEntity(entity) <= d)) {
				entities.add(entity);
			}
		}

		Collections.sort(entities, new Comparator<Entity>() {

			public int compare(Entity entity1, Entity entity2) {
				double d1 = player.getDistanceSqToEntity(entity1) - player.getDistanceSqToEntity(entity2);
				return d1 >= 0.0D ? (d1 <= 0.0D ? 0 : 1) : -1;
			}
		});

		return entities;
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