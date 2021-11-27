package org.hypbase.ghast.socketing.common.socketing.packets;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SUpdateCarriedPacket {
	private final ItemStack item;
	
	public SUpdateCarriedPacket(ItemStack item) {
		this.item = item;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public static SUpdateCarriedPacket decode(PacketBuffer packetBuffer) {
		ItemStack item = packetBuffer.readItem();
		return new SUpdateCarriedPacket(item);
	}
	
	public static void encode(SUpdateCarriedPacket msg, PacketBuffer packetBuffer) {
		packetBuffer.writeItem(msg.getItem());
	}
	
	public static void updateCarriedItem(ItemStack item) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		player.inventory.setCarried(item);
	}
	
	public static void handlePacket(SUpdateCarriedPacket msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> 
		updateCarriedItem(msg.getItem())
				);
		supplier.get().setPacketHandled(true);
	}
}
