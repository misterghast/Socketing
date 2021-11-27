package org.hypbase.ghast.socketing.common.socketing.packets;

import java.util.function.Supplier;

import org.hypbase.ghast.socketing.common.socketing.SocketingPacketHandler;
import org.hypbase.ghast.socketing.common.socketing.SocketingRecipe;
import org.hypbase.ghast.socketing.common.socketing.SocketingRegistry;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class CCheckValidRecipePacket {
	private ItemStack gem;
	private ItemStack base;
	
	public ItemStack getGem() {
		return this.gem;
	}
	
	public ItemStack getBase() {
		return this.base;
	}
	
	public CCheckValidRecipePacket(ItemStack gem, ItemStack base) {
		this.gem = gem;
		this.base = base;
	}
	
	public static CCheckValidRecipePacket decode(PacketBuffer buffer) {
		ItemStack gem = buffer.readItem();
		ItemStack base = buffer.readItem();
		return new CCheckValidRecipePacket(gem, base);
	}
	
	public static void encode(CCheckValidRecipePacket msg, PacketBuffer buffer) {
		buffer.writeItem(msg.getGem());
		buffer.writeItem(msg.getBase());
	}
	
	public static void sendIsValidToClient(World world, ItemStack gem, ItemStack base, ServerPlayerEntity sender) {
		SocketingRecipe r = (SocketingRecipe) SocketingRegistry.getRecipeFromIngredients(sender.getLevel(), gem, base);
		if(r != null && r.getResult() != null) {
			SocketingPacketHandler.INST.sendTo(new SIsValidRecipePacket(true, r.getResult()), sender.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
		} else {
			SocketingPacketHandler.INST.sendTo(new SIsValidRecipePacket(false, ItemStack.EMPTY), sender.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
		}
	}
	
	public static void handlePacket(CCheckValidRecipePacket msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			sendIsValidToClient(supplier.get().getSender().getLevel(), msg.getGem(), msg.getBase(), supplier.get().getSender());
		});
		supplier.get().setPacketHandled(true);
	}
}
