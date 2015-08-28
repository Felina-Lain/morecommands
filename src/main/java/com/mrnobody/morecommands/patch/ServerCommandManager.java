package com.mrnobody.morecommands.patch;

import static net.minecraft.util.EnumChatFormatting.RED;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.mrnobody.morecommands.command.CommandBase;
import com.mrnobody.morecommands.core.MoreCommands;
import com.mrnobody.morecommands.util.DummyCommand;
import com.mrnobody.morecommands.util.GlobalSettings;
import com.mrnobody.morecommands.util.LanguageManager;
import com.mrnobody.morecommands.util.ServerPlayerSettings;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

/**
 * The patched class of {@link net.minecraft.command.ServerCommandManager} <br>
 * Patching this class is needed to use commands e.g. with a command block <br>
 * The vanilla command manager passes e.g. the command block as command sender,
 * this modified version will use players selected by a target selector
 * Another aspect why this patch is needed is to use variables.
 * 
 * @author MrNobody98
 *
 */
public class ServerCommandManager extends net.minecraft.command.ServerCommandManager {	
	private static class VarCouldNotBeResolvedException extends Exception {
		private String var;
		
		public VarCouldNotBeResolvedException(String var) {
			this.var = var;
		}
	}
	
	public ServerCommandManager(net.minecraft.command.ICommandManager parent) {
		super();
		for (Object command : parent.getCommands().values()) this.registerCommand((ICommand) command);
	}
	
	private static String replaceVars(String string, ServerPlayerSettings settings) throws VarCouldNotBeResolvedException {
		String varIdentifier = "";
		String newString = "";
		boolean isReadingVarIdentifier = false;
		
		for (char ch : string.toCharArray()) {
			if (ch == '%') {
				if (isReadingVarIdentifier) {
					isReadingVarIdentifier = false;
					
					if (varIdentifier.isEmpty()) newString += "%";
					else {
						if (!settings.varMapping.containsKey(varIdentifier))
							throw new VarCouldNotBeResolvedException(varIdentifier);
						newString += settings.varMapping.get(varIdentifier);
					}
					
					varIdentifier = "";
				}
				else isReadingVarIdentifier = true;
			}
			else {
				if (isReadingVarIdentifier) varIdentifier += ch;
				else newString += ch;
			}
		}
		
		return newString;
	}
	
	@Override
    public int executeCommand(ICommandSender sender, String rawCommand)
    {
        rawCommand = rawCommand.trim();

        if (rawCommand.startsWith("/"))
        {
            rawCommand = rawCommand.substring(1);
        }
        
        if (GlobalSettings.enableVars && sender instanceof EntityPlayerMP && ServerPlayerSettings.containsSettingsForPlayer((EntityPlayerMP) sender)) {
        	try {rawCommand = replaceVars(rawCommand, ServerPlayerSettings.getPlayerSettings((EntityPlayerMP) sender));}
            catch (VarCouldNotBeResolvedException vcnbre) {
            	ChatComponentText text = new ChatComponentText(LanguageManager.getTranslation(MoreCommands.getMoreCommands().getCurrentLang(sender), "command.var.cantBeResolved", vcnbre.var));
            	text.getChatStyle().setColor(RED); sender.addChatMessage(text);
            }
        }

        String[] astring = rawCommand.split(" ");
        String s1 = astring[0];
        astring = dropFirstString(astring);
        ICommand icommand = (ICommand)this.getCommands().get(s1);
        int i = this.getUsernameIndex(icommand, astring);
        int j = 0;
        ChatComponentTranslation chatcomponenttranslation;
        
        if (icommand == null || icommand instanceof CommandBase || icommand instanceof DummyCommand) {
            if (astring.length > 0 && astring[astring.length - 1].startsWith("@"))
            {
                List list = PlayerSelector.matchEntities(sender, astring[astring.length - 1], Entity.class);
                Iterator iterator = list.iterator();
                astring = (String[]) Arrays.copyOfRange(astring, 0, astring.length - 1);
                int showError = -1;
                
                while (iterator.hasNext()) {
                	Entity entity = (Entity) iterator.next();
                	
                	if (entity instanceof EntityPlayerMP && ServerPlayerSettings.containsSettingsForPlayer((EntityPlayerMP) entity)) {
                		ServerPlayerSettings settings = ServerPlayerSettings.getPlayerSettings((EntityPlayerMP) entity);
                		
                		if (settings.clientCommands.contains(s1)) {
                			MoreCommands.getMoreCommands().getPacketDispatcher().sendS10ExecuteClientCommand((EntityPlayerMP) entity, s1 + " " + String.join(" ", astring));
                			++j;
                			continue;
                		}
                	}
                	
                	if (icommand == null) showError = 0;
                	else if (icommand != null && icommand.canCommandSenderUse(sender)) {
                        net.minecraftforge.event.CommandEvent event = new net.minecraftforge.event.CommandEvent(icommand, entity, astring);
                        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        {
                            if (event.exception != null)
                            {
                                com.google.common.base.Throwables.propagateIfPossible(event.exception);
                            }
                            continue;
                        }
                		
                		if (this.tryExecute(entity, astring, icommand, rawCommand)) ++j;
                	}
                	else showError = 1;
                }
                
                if (showError == 0) {
                    chatcomponenttranslation = new ChatComponentTranslation("commands.generic.notFound", new Object[0]);
                    chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
                    sender.addChatMessage(chatcomponenttranslation);
                }
                else if (showError == 1) {
                    chatcomponenttranslation = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
                    chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
                    sender.addChatMessage(chatcomponenttranslation);
                }
                
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, j);
            }
            else
            {
            	if (icommand == null) {
                    chatcomponenttranslation = new ChatComponentTranslation("commands.generic.notFound", new Object[0]);
                    chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
                    sender.addChatMessage(chatcomponenttranslation);
            	}
            	else if (icommand.canCommandSenderUse(sender)) {
                    net.minecraftforge.event.CommandEvent event = new net.minecraftforge.event.CommandEvent(icommand, sender, astring);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                    {
                        if (event.exception != null)
                        {
                            com.google.common.base.Throwables.propagateIfPossible(event.exception);
                        }
                        return 1;
                    }
            		
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);

                    if (this.tryExecute(sender, astring, icommand, rawCommand))
                    {
                        ++j;
                    }
            	}
            	else {
                    chatcomponenttranslation = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
                    chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
                    sender.addChatMessage(chatcomponenttranslation);
            	}
            }
        }
        else {
            if (icommand.canCommandSenderUse(sender))
            {
                net.minecraftforge.event.CommandEvent event = new net.minecraftforge.event.CommandEvent(icommand, sender, astring);
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                {
                    if (event.exception != null)
                    {
                        com.google.common.base.Throwables.propagateIfPossible(event.exception);
                    }
                    return 1;
                }

                if (i > -1)
                {
                    List list = PlayerSelector.matchEntities(sender, astring[i], Entity.class);
                    String s2 = astring[i];
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        Entity entity = (Entity)iterator.next();
                        astring[i] = entity.getUniqueID().toString();

                        if (this.tryExecute(sender, astring, icommand, rawCommand))
                        {
                            ++j;
                        }
                    }

                    astring[i] = s2;
                }
                else
                {
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);

                    if (this.tryExecute(sender, astring, icommand, rawCommand))
                    {
                        ++j;
                    }
                }
            }
            else
            {
                chatcomponenttranslation = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
                chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
                sender.addChatMessage(chatcomponenttranslation);
            }
        }

        sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, j);
        return j;
    }
    
    private static String[] dropFirstString(String[] input)
    {
        String[] astring1 = new String[input.length - 1];
        System.arraycopy(input, 1, astring1, 0, input.length - 1);
        return astring1;
    }
    
    private int getUsernameIndex(ICommand command, String[] args)
    {
        if (command == null)
        {
            return -1;
        }
        else
        {
            for (int i = 0; i < args.length; ++i)
            {
                if (command.isUsernameIndex(args, i) && PlayerSelector.matchesMultiplePlayers(args[i]))
                {
                    return i;
                }
            }

            return -1;
        }
    }
}
