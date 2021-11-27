package org.hypbase.ghast.socketing.common.socketing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hypbase.ghast.socketing.Socketing;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegistryEvent;

public class SocketingRegistry {
	public static final IRecipeType<ISocketingRecipe> SOCKETING_TYPE = IRecipeType.register(Socketing.MODID + ":socketing");
	public static final IRecipeSerializer<SocketingRecipe> SOCKETING_SERIALIZER = new SocketingRecipe.Serializer();
	
	public static void register(RegistryEvent.Register<IRecipeSerializer<?>> serializer) {
		ResourceLocation id = new ResourceLocation(Socketing.MODID, "socketing");
		Registry.register(Registry.RECIPE_TYPE, id, SOCKETING_TYPE);
		serializer.getRegistry().register(SOCKETING_SERIALIZER.setRegistryName(id));
	}
	
	private static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
		@Override
		public String toString() {
			return Registry.RECIPE_TYPE.getKey(this).toString();
		}
	}
	
	public static <C extends IInventory, T extends IRecipe<C>> List<T> getRecipes(World world, IRecipeType<T> type) {
		return world.getRecipeManager().getAllRecipesFor(type);
	}
	
	public static ItemStack getValidRecipeItem(World world, ISocketingRecipe recipe) {
		List<ISocketingRecipe> recipes = getRecipes(world, SocketingRegistry.SOCKETING_TYPE);
		
		for(ISocketingRecipe r : recipes) {
			if(r.getRegistryName().equals(recipe.getRegistryName())) {
				return r.getResult();
			}
		}
		return null;
	}
	
	public static boolean itemListContains(ItemStack item, ItemStack[] list) {
		for(ItemStack i : list) {
			if(i.getItem().equals(item.getItem())) {
				return true;
			}
		}
		return false;
	}
	
	public static ISocketingRecipe getRecipeFromIngredients(World world, ItemStack held, ItemStack base) {
		List<ISocketingRecipe> recipes = getRecipes(world, SocketingRegistry.SOCKETING_TYPE);
		for(ISocketingRecipe r : recipes) {
			/*System.out.println(r);
			System.out.println(r.getGem().ingredient.getItems()[0].toString());
			System.out.println(r.getBase().ingredient.getItems()[0].toString());*/
			if(itemListContains(held, r.getGem().ingredient.getItems()) && itemListContains(base, r.getBase().ingredient.getItems())) {
				//System.out.println("It works!");
				return r;
			}
		} 
		
		return null;
	}
	
	public static ISocketingRecipe getRecipeFromName(World world, ResourceLocation name) {
		List<ISocketingRecipe> recipes = getRecipes(world, SocketingRegistry.SOCKETING_TYPE);
		for(ISocketingRecipe r : recipes) {
			if(r.getRegistryName().equals(name)) {
				return r;
			}
		} 
		
		return null;
	}
}
