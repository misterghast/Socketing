package org.hypbase.ghast.socketing.common.socketing.packets;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.hypbase.ghast.socketing.common.socketing.ISocketingRecipe;
import org.hypbase.ghast.socketing.common.socketing.SocketingPacketHandler;
import org.hypbase.ghast.socketing.common.socketing.SocketingRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class SocketingPacket {
	private final ItemStack held;
	private final ItemStack inv;
	private final int slot;
	
	public SocketingPacket(ItemStack held, ItemStack inv, int slot) {
		this.held = held;
		this.inv = inv;
		this.slot = slot;
	}

	public ItemStack getHeld() {
		return this.held;
	}
	
	public ItemStack getInv() {
		return this.inv;
	}
	
	public int getSlot() {
		return this.slot;
	}
	
	public static SocketingPacket decode(PacketBuffer packetBuffer) {
		ItemStack held = packetBuffer.readItem();
		System.out.println(held);
		ItemStack inv = packetBuffer.readItem();
		System.out.println(inv);
		int slot = packetBuffer.readInt();
		return new SocketingPacket(held, inv, slot);
	}
	
	public static void encode(SocketingPacket msg, PacketBuffer packetBuffer) {
		packetBuffer.writeItem(msg.getHeld());
		packetBuffer.writeItem(msg.getInv());
		packetBuffer.writeInt(msg.getSlot());
	}
	
	public static void replaceItems(ServerPlayerEntity player, ItemStack held, ItemStack inv, int slot, ISocketingRecipe recipe) {
		if(recipe != null) {
			ItemStack output = SocketingRegistry.getValidRecipeItem(player.getLevel(), recipe);
			//System.out.println(player.inventory.getCarried());
			if(output != null && (player.inventory.getCarried().getItem().equals(held.getItem()) || player.isCreative()) && player.inventory.contains(inv)) {
				//System.out.println(held);
				player.inventory.getCarried().shrink(1);
				player.inventory.getItem(slot).shrink(1);
				player.inventory.add(output);
				player.inventoryMenu.broadcastChanges();
				if(player.isCreative()) {
					//do nothing.
				} else {
					SocketingPacketHandler.INST.sendTo(new SUpdateCarriedPacket(player.inventory.getCarried()), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
					SocketingPacketHandler.INST.sendTo(new SUpdateInventoryItemPacket(slot, player.inventory.getItem(slot)), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
				}
								
				//player.inventory.add(output);
			}
		}
	}
	
	public static void handlePacket(SocketingPacket msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> 
			replaceItems(supplier.get().getSender(), msg.getHeld(), msg.getInv(), msg.getSlot(), SocketingRegistry.getRecipeFromIngredients(supplier.get().getSender().getLevel(), msg.getHeld(), msg.getInv()))
		);
		supplier.get().setPacketHandled(true);	
	}
}
