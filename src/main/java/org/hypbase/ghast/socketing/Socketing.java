package org.hypbase.ghast.socketing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hypbase.ghast.socketing.client.RenderEvents;
import org.hypbase.ghast.socketing.client.SocketingKeyBindings;
import org.hypbase.ghast.socketing.common.socketing.SocketingPacketHandler;
import org.hypbase.ghast.socketing.common.socketing.SocketingRegistry;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("socketmod")
public class Socketing {
	public static final String MODID = "socketmod";
	public static Logger log = LogManager.getLogger();
	
	public Socketing() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		eventBus.addGenericListener(IRecipeSerializer.class, SocketingRegistry::register);
		eventBus.addListener(this::clientSetup);
		eventBus.addListener(this::commonSetup);
		
		if(FMLEnvironment.dist.equals(Dist.CLIENT)) {
			forgeBus.register(new RenderEvents());
		}
	}
	
	public void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			SocketingKeyBindings.init();
		});
	}
	
	public void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			//SocketingPacketHandler.registerPackets();
		});
		SocketingPacketHandler.registerPackets();
	}
}
