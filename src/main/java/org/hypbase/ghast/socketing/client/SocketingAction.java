package org.hypbase.ghast.socketing.client;

import java.util.List;

import javax.annotation.Nullable;

import org.hypbase.ghast.socketing.common.socketing.ISocketingRecipe;
import org.hypbase.ghast.socketing.common.socketing.SocketingPacketHandler;
import org.hypbase.ghast.socketing.common.socketing.SocketingRegistry;
import org.hypbase.ghast.socketing.common.socketing.packets.SocketingPacket;

import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

//Basically stolen from PrimitiveCrafting :o
public class SocketingAction {
	private List<ISocketingRecipe> validRecipes;
	
	private ISocketingRecipe currentRecipe;
	
	private ItemStack held;
	private ItemStack inv;
	
	private Slot inventorySlot;
	
	public SocketingAction(ItemStack held, ItemStack inv, Slot slot) {
		this.held = held;
		this.inv = inv;
		this.inventorySlot = slot;
	}
	
	public static void craft(ItemStack held, ItemStack inv, int slot, World world) {
		if(SocketingRegistry.getRecipeFromIngredients(world, held, inv) != null) {
			SocketingPacketHandler.INST.sendToServer(new SocketingPacket(held, inv, slot));
		}
	}
	
}
