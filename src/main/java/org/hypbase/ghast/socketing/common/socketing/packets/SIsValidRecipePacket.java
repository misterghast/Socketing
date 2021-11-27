package org.hypbase.ghast.socketing.common.socketing.packets;

import java.util.function.Supplier;

import org.hypbase.ghast.socketing.client.RenderEvents;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SIsValidRecipePacket {
	private boolean isValidRecipe;
	private ItemStack result;
	
	public SIsValidRecipePacket(boolean isValidRecipe, ItemStack result) {
		this.isValidRecipe = isValidRecipe;
		this.result = result;
	}
	
	public static SIsValidRecipePacket decode(PacketBuffer buffer) {
		boolean isValidRecipe = buffer.readBoolean();
		ItemStack result = buffer.readItem();
		return new SIsValidRecipePacket(isValidRecipe, result);
	}
	
	public static void encode(SIsValidRecipePacket msg, PacketBuffer buffer) {
		buffer.writeBoolean(msg.getIsValidRecipe());
		buffer.writeItem(msg.getResult());
	}
	
	public boolean getIsValidRecipe() {
		return this.isValidRecipe;
	}
	
	public ItemStack getResult() {
		return this.result;
	}
	
	public static void setValid(boolean isValidRecipe, ItemStack result) {
		//System.out.println("It's being set, not sure what the issue is.");
		RenderEvents.setValidRecipe(isValidRecipe);
		RenderEvents.setResultItem(result);
	}
	
	public static void handlePacket(SIsValidRecipePacket msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			setValid(msg.getIsValidRecipe(), msg.getResult());
		});
		supplier.get().setPacketHandled(true);
	}
}
