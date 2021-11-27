package org.hypbase.ghast.socketing.common.socketing;

import org.hypbase.ghast.socketing.Socketing;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.loot.LootTypesManager.ISerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SocketingRecipe implements ISocketingRecipe {
	private ItemStack result;
	private SocketingIngredient gem;
	private SocketingIngredient base;
	
	private ResourceLocation registryName;
	
	public SocketingRecipe(ResourceLocation id, SocketingIngredient gem, SocketingIngredient base, ItemStack result) {
		this.registryName = id;
		this.gem = gem;
		this.base = base;
		this.result = result;
	}
	
	public SocketingRecipe(ResourceLocation id, JsonObject recipeJson) {
		this.registryName = id;
		this.gem = SocketingIngredient.fromJson(recipeJson.get("gem"));
		this.base = SocketingIngredient.fromJson(recipeJson.get("base"));
		this.result = SocketingIngredient.fromJson(recipeJson.get("result")).ingredient.getItems()[0].copy();
		result.setCount(recipeJson.getAsJsonObject("result").get("count").getAsInt());
	}
	
	public SocketingRecipe(ResourceLocation id, PacketBuffer buffer) {
		this.registryName = id;
		this.gem  = new SocketingIngredient(Ingredient.fromNetwork(buffer), 1);
		this.base = new SocketingIngredient(Ingredient.fromNetwork(buffer), 1);
		this.result = buffer.readItem().copy();
	}
	
	@Override
	public ItemStack getResult() {
		 return result.copy();
	}

	@Override
	public SocketingIngredient getGem() {
		return gem;
	}

	@Override
	public SocketingIngredient getBase() {
		return base;
	}

	@Override
	public boolean isValid(ItemStack gem, ItemStack base) {
		boolean bool = (this.gem.test(gem) && this.base.test(base) && this.gem.count <= gem.getCount() && this.base.count <= base.getCount()) || (this.gem.test(base) && this.base.test(gem) && this.gem.count <= base.getCount() && this.base.count <= gem.getCount());
		return bool;
	}

	@Override
	public void craft(ItemStack gem, ItemStack base, PlayerEntity player, ItemStack hoverStack, int slot) {
		ItemStack newGem = sort(gem, base, true);
		ItemStack newBase = sort(gem, base, false);
		
		if (newGem != null && newBase != null && !newGem.isEmpty() && !newBase.isEmpty()) {
			getCraftingResult(newGem, newBase, player, SocketingRecipe.areStacksEqual(newGem, hoverStack) ? true : false, slot);
		
			addItem(player, getResult());
		}
	}

	@Override
	public void getCraftingResult(ItemStack gem, ItemStack base, PlayerEntity player, boolean isAHoverStack, int slot) {
		this.getGem().getChangeForStack(gem).changeStack(gem, player, isAHoverStack, slot);
		this.getBase().getChangeForStack(base).changeStack(base, player, isAHoverStack, slot);
	}

	
	public static void addItem(PlayerEntity player, ItemStack stack) {
		if (player != null && !player.getCommandSenderWorld().isClientSide) {
			if (!player.inventory.add(stack)) {
				player.drop(stack, false);
			}
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 73;
		int r = 1;
		r = prime * r + ((gem == null) ? 0 : gem.hashCode());
		r = prime * r + ((base == null) ? 0 : base.hashCode());
		r = prime * r + ((this.result == null) ? 0 : this.result.getItem().hashCode());
		return r;
	}
	
	public ItemStack sort(ItemStack gem, ItemStack base, boolean getGem) {
		if (this.getGem().test(gem) && this.getBase().test(base)) {
			if (getGem) {
				return gem;
			} else {
				return base;
			}
		}
		
		if (this.getGem().test(base) && this.getBase().test(gem)) {
			if(getGem) {
				return base;
			} else {
				return gem;
			}
		}
		
		return null;
	}
	
	public static int compareStacks(ItemStack gem, ItemStack base) {
		String aS = gem.getItem().getRegistryName().toString();
		String bS = base.getItem().getRegistryName().toString();
		
		int i = aS.compareTo(bS);
		
		if (i < 0) {
			return -1;
		} else if (i > 0) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof SocketingRecipe) {
			SocketingRecipe r = (SocketingRecipe) other;
			if (r.gem.equals(this.gem) && r.base.equals(this.base) && areStacksEqual(result, r.result) && r.registryName.equals(this.registryName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean areStacksEqual(ItemStack one, ItemStack two) {
		if (one.getItem() != two.getItem()) {
			return false;
		} else if (one.getTag() != two.getTag()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}
	
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SocketingRecipe> {

		@Override
		public SocketingRecipe fromJson(ResourceLocation p_199425_1_, JsonObject json) {
			return new SocketingRecipe(p_199425_1_, json);
		}

		@Override
		public SocketingRecipe fromNetwork(ResourceLocation id, PacketBuffer packetBuffer) {
			return new SocketingRecipe(id, packetBuffer);
		}

		@Override
		public void toNetwork(PacketBuffer packetBuffer, SocketingRecipe recipe) {
			recipe.getGem().ingredient.toNetwork(packetBuffer);
			recipe.getBase().ingredient.toNetwork(packetBuffer);
			packetBuffer.writeItem(recipe.getResultItem());
		}
		
	}

	//always returns false because there's no TileEntity related to it
	@Override
	public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
		return false;
	}
	
	@Override
	public boolean matches(ItemStack gem, ItemStack base) {
		return ((this.getGem().test(gem) && this.getBase().test(base)) || (this.getGem().test(base) && this.getBase().test(gem)));
	}

	
	//I don't know x2
	@Override
	public ItemStack assemble(IInventory p_77572_1_) {
		return null;
	}

	@Override
	public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		// TODO Auto-generated method stub
		return this.result;
	}

	@Override
	public ResourceLocation getId() {
		// TODO Auto-generated method stub
		return this.registryName;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		// TODO Auto-generated method stub
		return SocketingRegistry.SOCKETING_SERIALIZER;
	}

	
	//I don't even know.
	@Override
	public IRecipeType<?> getType() {
		return SocketingRegistry.SOCKETING_TYPE;
	}
	
}
