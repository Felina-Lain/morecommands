package com.mrnobody.morecommands.command.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.CommandRequirement;
import com.mrnobody.morecommands.command.ServerCommandProperties;
import com.mrnobody.morecommands.command.StandardCommand;
import com.mrnobody.morecommands.core.MoreCommands.ServerType;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Coordinate;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

@Command(
		name = "confuse",
		description = "command.confuse.description",
		example = "command.confuse.example",
		syntax = "command.confuse.syntax",
		videoURL = "command.confuse.videoURL"
		)
public class CommandConfuse extends StandardCommand implements ServerCommandProperties {
	private static final double RADIUS_MAX = 50;
	
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
		params = reparseParamsWithNBTData(params);
		double radius = 10D;
		
		if (params.length > 0) {
			try {radius = Double.parseDouble(params[0]);}
			catch (NumberFormatException e) {throw new CommandException("command.confuse.NAN", sender);}
			if (radius > RADIUS_MAX) throw new CommandException("command.confuse.invalidRadius", sender);
		}
		
		List<? extends EntityCreature> entities = getEntitiesInRadius(sender.getPosition(), sender.getWorld().getMinecraftWorld(), EntityCreature.class, radius);
		
		for (int index = 1; index < entities.size(); index++)
			entities.get(index).setTarget(entities.get(index - 1));
        
		sender.sendLangfileMessage("command.confuse.confused", entities.size(), radius);
	}
	
	private <T extends Entity> List<? extends T> getEntitiesInRadius(final Coordinate coord, World world, Class<T> class1, double radius) {
		List<T> entities = new ArrayList<T>();
		
		for (int i = 0; i < world.loadedEntityList.size(); i++) {
			Entity found = (Entity) world.loadedEntityList.get(i);
			if (!(found instanceof EntityPlayer) && !found.isDead && class1.isInstance(found) && 
				(radius <= 0.0D || coord.getDistanceBetweenCoordinates(new Coordinate(found)) <= radius)) {
				entities.add((T) found);
			}
		}

		Collections.sort(entities, new Comparator<T>() {

			public int compare(T entity1, T entity2) {
				double d1 = coord.getDistanceBetweenCoordinates(new Coordinate(entity1)) - coord.getDistanceBetweenCoordinates(new Coordinate(entity2));
				return d1 >= 0.0D ? (d1 <= 0.0D ? 0 : 1) : -1;
			}
		});

		return entities;
	}
	
	@Override
	public CommandRequirement[] getRequirements() {
		return new CommandRequirement[0];
	}

	@Override
	public ServerType getAllowedServerType() {
		return ServerType.ALL;
	}
	
	@Override
	public int getDefaultPermissionLevel() {
		return 2;
	}
	
	@Override
	public boolean canSenderUse(String commandName, ICommandSender sender, String[] params) {
		return true;
	}
}
