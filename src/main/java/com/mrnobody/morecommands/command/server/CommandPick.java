package com.mrnobody.morecommands.command.server;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Player;

@Command(
		name = "pick",
		description = "command.pick.description",
		example = "command.pick.example",
		syntax = "command.pick.syntax",
		videoURL = "command.pick.videoURL"
		)
public class CommandPick extends ServerCommand {
	@Override
	public String getCommandName() {
		return "pick";
	}

	@Override
	public String getUsage() {
		return "command.pick.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		Player player = sender.toPlayer();
		MovingObjectPosition pick = player.tracePath(128.0D, 0.0D, 1.0F);
		int amount = 64;
		
		if (params.length > 0) {
			try {amount = Integer.parseInt(params[0]);}
			catch (NumberFormatException nfe) {sender.sendLangfileMessageToPlayer("command.pick.NAN", new Object[0]); return;}
		}
		
		if (pick != null) {
			if (!this.onPickBlock(pick, player.getMinecraftPlayer(), player.getMinecraftPlayer().worldObj, amount))
				sender.sendLangfileMessageToPlayer("command.pick.cantgive", new Object[0]);
		}
		else sender.sendLangfileMessageToPlayer("command.pick.notInSight", new Object[0]);
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
	
    private boolean onPickBlock(MovingObjectPosition target, EntityPlayer player, World world, int amount)
    {
        ItemStack result = null;

        if (target.typeOfHit == MovingObjectType.BLOCK)
        {
            Block block = world.getBlock(target.blockX, target.blockY, target.blockZ);

            if (block.isAir(world, target.blockX, target.blockY, target.blockZ))
            {
                return false;
            }

            //result = block.getPickBlock(target, world, target.blockX, target.blockY, target.blockZ); //does not work on servers because methods used by this method are client side only
            result = this.getPickBlock(block, target, world, target.blockX, target.blockY, target.blockZ);
        }
        else
        {
            if (target.typeOfHit != MovingObjectType.ENTITY || target.entityHit == null)
            {
                return false;
            }

            result = target.entityHit.getPickedResult(target);
        }

        if (result == null)
        {
            return false;
        }
        
        result.stackSize = amount;

        for (int x = 0; x < 9; x++)
        {
            ItemStack stack = player.inventory.getStackInSlot(x);
            if (stack != null && stack.isItemEqual(result) && ItemStack.areItemStackTagsEqual(stack, result))
            {
                player.inventory.currentItem = x;
                stack.stackSize += amount;
                return true;
            }
        }

        int slot = player.inventory.getFirstEmptyStack();
        if (slot < 0 || slot >= 9)
        {
            slot = player.inventory.currentItem;
        }

        player.inventory.setInventorySlotContents(slot, result);
        player.inventory.currentItem = slot;
        return true;
    }
    
    private ItemStack getPickBlock(Block block, MovingObjectPosition target, World world, int x, int y, int z) {
    	Item item = Item.getItemFromBlock(block);

    	if (item == null) {
    		String unlocalized = block.getUnlocalizedName();
    		if (unlocalized.startsWith("tile.")) unlocalized = block.getUnlocalizedName().substring(5);
    		item = (Item) Item.itemRegistry.getObject("minecraft:" + unlocalized);
    	}
        
    	if (item == null) return null;

    	Block result = item instanceof ItemBlock && !(block instanceof BlockFlowerPot) ? Block.getBlockFromItem(item) : block;
    	return new ItemStack(item, 1, result.getDamageValue(world, x, y, z));
	}
}