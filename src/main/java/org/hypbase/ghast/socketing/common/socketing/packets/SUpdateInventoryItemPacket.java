package org.hypbase.ghast.socketing.common.socketing.packets;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SUpdateInventoryItemPacket {
	private final ItemStack item;
	private final int slot;
	
	public SUpdateInventoryItemPacket(int slot, ItemStack item) {
		this.item = item;
		this.slot = slot;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public int getSlot() {
		return this.slot;
	}
	
	public static SUpdateInventoryItemPacket decode(PacketBuffer buffer) {
		ItemStack item = buffer.readItem();
		int slot = buffer.readInt();
		
		return new SUpdateInventoryItemPacket(slot, item);
	}
	
	public static void encode(SUpdateInventoryItemPacket msg, PacketBuffer buffer) {
		buffer.writeItem(msg.getItem());
		buffer.writeInt(msg.getSlot());
	}
	
	public static void updateInventoryItem(ItemStack item, int slot) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		player.inventory.setItem(slot, item);
	}
	
	public static void handlePacket(SUpdateInventoryItemPacket msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			updateInventoryItem(msg.getItem(), msg.getSlot());
		});
		supplier.get().setPacketHandled(true);
	}
}
