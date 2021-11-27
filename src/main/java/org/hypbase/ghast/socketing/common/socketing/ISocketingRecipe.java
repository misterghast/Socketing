package org.hypbase.ghast.socketing.common.socketing;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

public interface ISocketingRecipe extends IRecipe<IInventory> {
	public ItemStack getResult();
	
	public SocketingIngredient getGem();
	public SocketingIngredient getBase();
	
	public boolean isValid(ItemStack gem, ItemStack base);
	
	public void craft(ItemStack gem, ItemStack base, PlayerEntity player, ItemStack hoverStack, int slot);
	
	public void getCraftingResult(ItemStack gem, ItemStack base, PlayerEntity player, boolean isAHoverStack, int slot);
	
	public ResourceLocation getRegistryName();
	
	public boolean matches(ItemStack gem, ItemStack base);
}
