package org.hypbase.ghast.socketing.common.socketing;

import javax.annotation.Nullable;

import org.hypbase.ghast.socketing.Socketing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class SocketingIngredient {
	public Ingredient ingredient;
	protected SocketingData[] changeData;
	public int count;
	
	protected SocketingIngredient() {
		
	}

	public SocketingIngredient(Ingredient ingredient, int count) {
		this(ingredient, count, true);
	
	}
	
	public static SocketingIngredient fromJson(JsonElement jsonObject) {
		return new SocketingIngredient(Ingredient.fromJson(jsonObject), 1);
	}
	
	public SocketingIngredient(Ingredient ingredient, int count, boolean init) {
		this.ingredient = ingredient;
		
		ItemStack[] stacks = ingredient.getItems();
		
		this.count = count;
	}
	
	public void initTransformData() {
		ItemStack[] stacks = ingredient.getItems();
		
		for(int i = 0; i < stacks.length; i++) {
			ItemStack stack = stacks[i];
			
			SocketingData data;
			data = SocketingData.getChangeData();
			
			changeData[i] = data;
		}
	}
	
	public SocketingData getChangeForStack(ItemStack stack) {
		ItemStack[] matchingStacks = ingredient.getItems();
		for (int i = 0; i < matchingStacks.length; i++) {
			ItemStack itemstack = matchingStacks[i];
			
			boolean equal = SocketingRecipe.areStacksEqual(itemstack, stack);
			boolean matches = ingredient.test(stack);
			
			if (matches && equal) {
				return changeData[i];
			}
		}
		
		Socketing.log.error("Couldn't find a recipe.");
		Socketing.log.error("Input:" + stack.toString());
		return SocketingData.getChangeData(); // blank change data
	}
	
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) {
			return false;
		} else {
			for (ItemStack itemstack : ingredient.getItems()) {
				boolean matches = ingredient.test(stack);
				
				if(matches && stack.getCount() >= count) {
					return true;
				}
			}
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 73;
		int r = 1;
		r = prime * r + count;
		r = prime * r + ((ingredient == null) ? 0 : ingredient.hashCode());
		return r;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SocketingIngredient)) {
			return false;
		}
		
		SocketingIngredient oth = (SocketingIngredient) obj;
		
		if(oth.count != this.count) {
			return false;
		}
		
		ItemStack[] thisStack = this.ingredient.getItems();
		ItemStack[] othStack = oth.ingredient.getItems();
		
		boolean equal = true;
		
		if (thisStack.length != othStack.length) {
			return false;
		}
		
		for(int i = 0; i < thisStack.length; i++) {
			if(!ItemStack.isSame(thisStack[i], othStack[i])) {
				equal = false;
			}
		}
		
		return equal;
	}
}
