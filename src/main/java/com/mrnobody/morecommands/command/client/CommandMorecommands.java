package com.mrnobody.morecommands.command.client;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.mrnobody.morecommands.command.ClientCommand;
import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.CommandBase.Requirement;
import com.mrnobody.morecommands.command.CommandBase.ServerType;
import com.mrnobody.morecommands.core.MoreCommands;
import com.mrnobody.morecommands.util.Reference;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

import cpw.mods.fml.relauncher.Side;

@Command(
		name = "morecommands",
		description = "command.morecommands.description",
		example = "command.morecommands.example",
		syntax = "command.morecommands.syntax",
		videoURL = "command.morecommands.videoURL"
		)
public class CommandMorecommands extends ClientCommand {

	@Override
	public String getCommandName() {
		return "morecommands";
	}

	@Override
	public String getUsage() {
		return "command.morecommands.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		ChatComponentText textModid = new ChatComponentText("MODID:             "); textModid.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA));
		ChatComponentText modid = new ChatComponentText(Reference.MODID); modid.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY));
		
		ChatComponentText textVersion = new ChatComponentText("VERSION:          "); textVersion.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA));
		ChatComponentText version = new ChatComponentText(Reference.VERSION); version.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY));
	
		ChatComponentText textName = new ChatComponentText("NAME:              "); textName.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA));
		ChatComponentText name = new ChatComponentText(Reference.NAME); name.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY));
	
		ChatComponentText textModDir = new ChatComponentText("MOD_DIR:          "); textModDir.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA));
		ChatComponentText modDir = new ChatComponentText(Reference.getModDir().getPath()); modDir.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY));
	
		ChatComponentText textBuildDate = new ChatComponentText("BUILD_DATE:     "); textBuildDate.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA));
		ChatComponentText builDate = new ChatComponentText(df.format(Reference.BUILD)); builDate.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY));
	
		sender.sendChatComponentToPlayer(textModid.appendSibling(modid));
		sender.sendChatComponentToPlayer(textVersion.appendSibling(version));
		sender.sendChatComponentToPlayer(textName.appendSibling(name));
		sender.sendChatComponentToPlayer(textModDir.appendSibling(modDir));
		sender.sendChatComponentToPlayer(textBuildDate.appendSibling(builDate));
	}
	
	@Override
	public Requirement[] getRequirements() {
		return new Requirement[0];
	}

	@Override
	public ServerType getAllowedServerType() {
		return ServerType.ALL;
	}
	
	@Override
	public boolean registerIfServerModded() {
		return false;
	}
	
	@Override
	public int getPermissionLevel() {
		return 0;
	}
}
