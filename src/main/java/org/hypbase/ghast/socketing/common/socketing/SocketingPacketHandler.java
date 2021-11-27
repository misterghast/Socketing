package org.hypbase.ghast.socketing.common.socketing;

import java.util.function.BiConsumer;

import org.hypbase.ghast.socketing.Socketing;
import org.hypbase.ghast.socketing.common.socketing.packets.CCheckValidRecipePacket;
import org.hypbase.ghast.socketing.common.socketing.packets.SIsValidRecipePacket;
import org.hypbase.ghast.socketing.common.socketing.packets.SUpdateCarriedPacket;
import org.hypbase.ghast.socketing.common.socketing.packets.SUpdateInventoryItemPacket;
import org.hypbase.ghast.socketing.common.socketing.packets.SocketingPacket;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SocketingPacketHandler {
	private static final String PROTOCOL_VERSION = "CAC1";
	private static int id = 0;
	public static SimpleChannel INST = NetworkRegistry.newSimpleChannel(new ResourceLocation(Socketing.MODID, "socketingpackets"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
;
	
	public static void registerPackets() {
		INST.registerMessage(id++, SocketingPacket.class, (msg, packetBuffer) -> SocketingPacket.encode(msg, packetBuffer), (packetBuffer) -> SocketingPacket.decode(packetBuffer), (msg, supplier) -> SocketingPacket.handlePacket(msg, supplier));		
		INST.registerMessage(id++, SUpdateCarriedPacket.class, (msg, packetBuffer) -> SUpdateCarriedPacket.encode(msg, packetBuffer), (packetBuffer) -> SUpdateCarriedPacket.decode(packetBuffer), (msg, supplier) -> SUpdateCarriedPacket.handlePacket(msg, supplier));
		INST.registerMessage(id++, SUpdateInventoryItemPacket.class, (msg, packetBuffer) -> SUpdateInventoryItemPacket.encode(msg, packetBuffer), (packetBuffer) -> SUpdateInventoryItemPacket.decode(packetBuffer), (msg, supplier) -> SUpdateInventoryItemPacket.handlePacket(msg, supplier));
		INST.registerMessage(id++, SIsValidRecipePacket.class, (msg, packetBuffer) -> SIsValidRecipePacket.encode(msg, packetBuffer), (packetBuffer) -> SIsValidRecipePacket.decode(packetBuffer), (msg, supplier) -> SIsValidRecipePacket.handlePacket(msg, supplier));
		INST.registerMessage(id++, CCheckValidRecipePacket.class, (msg, packetBuffer) -> CCheckValidRecipePacket.encode(msg, packetBuffer), (packetBuffer) -> CCheckValidRecipePacket.decode(packetBuffer), (msg, supplier) -> CCheckValidRecipePacket.handlePacket(msg, supplier));
	}
}
