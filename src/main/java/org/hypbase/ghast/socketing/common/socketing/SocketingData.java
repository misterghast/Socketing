package org.hypbase.ghast.socketing.common.socketing;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;


//I believe this class is deprecated.
public class SocketingData {
	private int amount = 0;
	
	private ItemStack newItem = ItemStack.EMPTY;
	
	public void changeStack(ItemStack toChange, @Nullable PlayerEntity player, boolean isHoverStack, int slot) {
		if(isHoverStack) {
			ItemStack i = newItem.copy();
			i.setCount(amount);
			player.inventory.setPickedItem(i);
		}
		else {
			player.inventory.removeItem(toChange);
		}
	}
	
	public static SocketingData getChangeData(Object... parameters) {
		SocketingData data = new SocketingData();
		return data;
	}
}
