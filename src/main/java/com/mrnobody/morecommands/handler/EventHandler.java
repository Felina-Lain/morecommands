package com.mrnobody.morecommands.handler;

import java.lang.reflect.Method;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * An enumeration of handlers used for sending events if a forge event is received
 * 
 * @author MrNobody98
 */
public enum EventHandler {
	ATTACK(EnumBus.MinecraftForge, new Handler<LivingAttackEvent>(LivingAttackEvent.class)),
	BLOCK_PLACEMENT(EnumBus.MinecraftForge, new Handler<PlaceEvent>(PlaceEvent.class)),
	BREAKSPEED(EnumBus.MinecraftForge, new Handler<BreakSpeed>(BreakSpeed.class)),
	COMMAND(EnumBus.MinecraftForge, new Handler<CommandEvent>(CommandEvent.class)),
	ENTITYJOIN(EnumBus.MinecraftForge, new Handler<EntityJoinWorldEvent>(EntityJoinWorldEvent.class)),
	EXPLOSION(EnumBus.MinecraftForge, new Handler<ExplosionEvent>(ExplosionEvent.class)),
	FALL(EnumBus.MinecraftForge, new Handler<LivingFallEvent>(LivingFallEvent.class)),
	DROPS(EnumBus.MinecraftForge, new Handler<HarvestDropsEvent>(HarvestDropsEvent.class)),
	HURT(EnumBus.MinecraftForge, new Handler<LivingHurtEvent>(LivingHurtEvent.class)),
	ITEM_DESTROY(EnumBus.MinecraftForge, new Handler<PlayerDestroyItemEvent>(PlayerDestroyItemEvent.class)),
	KEYINPUT(EnumBus.FML, new Handler<KeyInputEvent>(KeyInputEvent.class)),
	PLAYER_ATTACK(EnumBus.MinecraftForge, new Handler<AttackEntityEvent>(AttackEntityEvent.class)),
	INTERACT(EnumBus.MinecraftForge, new Handler<PlayerInteractEvent>(PlayerInteractEvent.class)),
	RENDERWORLD(EnumBus.MinecraftForge, new Handler<RenderWorldLastEvent>(RenderWorldLastEvent.class)),
	TICK(EnumBus.FML, new Handler<TickEvent>(TickEvent.class));
	
	private EnumBus bus;
	private Handler handler;
	
	EventHandler(EnumBus bus, Handler handler) {
		this.bus = bus;
		this.handler = handler;
	}
	
	/**
	 * @return The bus on which the handler shall be registered
	 */
	public EnumBus getBus() {
		return this.bus;
	}
	
	/**
	 * @return The handler handling the received event
	 */
	public Handler getHandler() {
		return this.handler;
	}
	
	/**
	 * An enumeration of the forge buses
	 * 
	 * @author MrNobody98
	 */
	public static enum EnumBus {MinecraftForge, FML}
	
	/**
	 * Registers a handler to a {@link cpw.mods.fml.common.eventhandler.EventBus}. <br>
	 * The regular {@link cpw.mods.fml.common.eventhandler.EventBus#register(Object)} method can't
	 * be used, because it doesn't work with generic types. <br> Instead, reflection is used to pass
	 * a private method of {@link cpw.mods.fml.common.eventhandler.EventBus} as parameter, which can
	 * be used to register the handler class with the event class.
	 * 
	 * @author MrNobody98
	 */
	public static void register(EnumBus bus, Handler handler, Method register, ModContainer container) {
		if (handler.getEventClass() != null) {
			Method onEvent = null;
			for (Method m : handler.getClass().getMethods()) {
				if (m.getName().equals("onEvent") && m.isAnnotationPresent(SubscribeEvent.class)) {onEvent = m; break;}
			}
			
			if (onEvent != null && register != null && container != null) {
				try {register.invoke(bus == EnumBus.MinecraftForge ? MinecraftForge.EVENT_BUS : FMLCommonHandler.instance().bus(), handler.getEventClass(), handler, onEvent, container);}
				catch (Exception ex) {ex.printStackTrace();}
			}
		}
	}
	
	/**
	 * @return whether this handler is intended to be used only on client side
	 */
	public static boolean isClientOnly(EventHandler handler) {
		if (handler == EventHandler.RENDERWORLD) return true;
		if (handler == EventHandler.KEYINPUT) return true;
		return false;
	}
}