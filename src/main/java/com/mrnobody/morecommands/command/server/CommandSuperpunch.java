package com.mrnobody.morecommands.command.server;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.CommandException;
import com.mrnobody.morecommands.command.CommandRequirement;
import com.mrnobody.morecommands.command.CommandSender;
import com.mrnobody.morecommands.command.ServerCommandProperties;
import com.mrnobody.morecommands.command.StandardCommand;
import com.mrnobody.morecommands.core.MoreCommands.ServerType;
import com.mrnobody.morecommands.event.EventHandler;
import com.mrnobody.morecommands.event.Listeners.EventListener;
import com.mrnobody.morecommands.settings.ServerPlayerSettings;

import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

@Command(
	name = "superpunch",
	description = "command.superpunch.description",
	example = "command.superpunch.example",
	syntax = "command.superpunch.syntax",
	videoURL = "command.superpunch.videoURL"
	)
public class CommandSuperpunch extends StandardCommand implements ServerCommandProperties, EventListener<AttackEntityEvent> {
	public CommandSuperpunch() {
		EventHandler.PLAYER_ATTACK.register(this);
	}
	
	@Override
	public void onEvent(AttackEntityEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			ServerPlayerSettings settings = getPlayerSettings((EntityPlayerMP) event.entity);
			
			if (settings.superpunch > 0) {
				event.setCanceled(true);
				this.attackWithSuperpunch((EntityPlayer) event.entity, event.target, settings.superpunch);
			}
		}
	}

	@Override
	public String getCommandName() {
		return "superpunch";
	}

	@Override
	public String getCommandUsage() {
		return "command.superpunch.syntax";
	}
	
	@Override
	public String execute(CommandSender sender, String[] params) throws CommandException {
		ServerPlayerSettings settings = getPlayerSettings(getSenderAsEntity(sender.getMinecraftISender(), EntityPlayerMP.class));
        	
        if (params.length > 0) {
        	if (params[0].equalsIgnoreCase("reset")) {
        		settings.superpunch = -1;
        		sender.sendLangfileMessage("command.superpunch.reset");
        	}
        	else {
        		try {
        			settings.superpunch = Integer.parseInt(params[0]);
        			sender.sendLangfileMessage("command.superpunch.success");
        		}
        		catch (NumberFormatException nfe) {
        			throw new CommandException("command.superpunch.NAN", sender);
        		}
        	}
        }
        else throw new CommandException("command.generic.invalidUsage", sender, this.getCommandName());
        
        return null;
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
	public int getDefaultPermissionLevel(String[] args) {
		return 2;
	}
	
	@Override
	public boolean canSenderUse(String commandName, ICommandSender sender, String[] params) {
		return isSenderOfEntityType(sender, EntityPlayerMP.class);
	}
	
    private void attackWithSuperpunch(EntityPlayer player, Entity target, int factor)
    {
        //if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(player, target)) return;
        if (target.canAttackWithItem())
        {
            if (!target.hitByEntity(player))
            {
                float f = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                byte b0 = 0;
                float f1 = 0.0F;

                if (target instanceof EntityLivingBase)
                {
                    f1 = EnchantmentHelper.func_152377_a(player.getHeldItem(), ((EntityLivingBase)target).getCreatureAttribute());
                }
                else
                {
                    f1 = EnchantmentHelper.func_152377_a(player.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
                }

                int j = b0 + EnchantmentHelper.getKnockbackModifier(player);

                if (player.isSprinting())
                {
                    ++j;
                }
                
                //SUPERPUNCH :D
                j *= factor;

                if (f > 0.0F || f1 > 0.0F)
                {
                    boolean flag = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && target instanceof EntityLivingBase;

                    if (flag && f > 0.0F)
                    {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag1 = false;
                    int i = EnchantmentHelper.getFireAspectModifier(player);

                    if (target instanceof EntityLivingBase && i > 0 && !target.isBurning())
                    {
                        flag1 = true;
                        target.setFire(1);
                    }

                    double d0 = target.motionX;
                    double d1 = target.motionY;
                    double d2 = target.motionZ;
                    boolean flag2 = target.attackEntityFrom(DamageSource.causePlayerDamage(player), f);

                    if (flag2)
                    {
                        if (j > 0)
                        {
                        	target.addVelocity((double)(-MathHelper.sin(player.rotationYaw * (float)Math.PI / 180.0F) * (float)j * 0.5F), 0.1D, (double)(MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F) * (float)j * 0.5F));
                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            player.setSprinting(false);
                        }

                        if (target instanceof EntityPlayerMP && target.velocityChanged)
                        {
                            ((EntityPlayerMP)target).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(target));
                            target.velocityChanged = false;
                            target.motionX = d0;
                            target.motionY = d1;
                            target.motionZ = d2;
                        }

                        if (flag)
                        {
                            player.onCriticalHit(target);
                        }

                        if (f1 > 0.0F)
                        {
                            player.onEnchantmentCritical(target);
                        }

                        if (f >= 18.0F)
                        {
                            player.triggerAchievement(AchievementList.overkill);
                        }

                        player.setLastAttacker(target);

                        if (target instanceof EntityLivingBase)
                        {
                            EnchantmentHelper.func_151384_a((EntityLivingBase)target, player);
                        }

                        EnchantmentHelper.func_151385_b(player, target);
                        ItemStack itemstack = player.getCurrentEquippedItem();
                        Object object = target;

                        if (target instanceof EntityDragonPart)
                        {
                            IEntityMultiPart ientitymultipart = ((EntityDragonPart)target).entityDragonObj;

                            if (ientitymultipart instanceof EntityLivingBase)
                            {
                                object = (EntityLivingBase)ientitymultipart;
                            }
                        }

                        if (itemstack != null && object instanceof EntityLivingBase)
                        {
                            itemstack.hitEntity((EntityLivingBase)object, player);

                            if (itemstack.stackSize <= 0)
                            {
                                player.destroyCurrentEquippedItem();
                            }
                        }

                        if (target instanceof EntityLivingBase)
                        {
                            player.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));

                            if (i > 0)
                            {
                            	target.setFire(i * 4);
                            }
                        }

                        player.addExhaustion(0.3F);
                    }
                    else if (flag1)
                    {
                    	target.extinguish();
                    }
                }
            }
        }
    }
}
