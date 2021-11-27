package org.hypbase.ghast.socketing.client;

import org.lwjgl.glfw.GLFWKeyCallback;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class SocketingKeyBindings {
	public static KeyBinding socket;
	public static KeyBinding showSocket;
	
	public static void init() {
		socket = new KeyBinding("key.cookiesandcream.socket.desc", 83, "cookiesandcream.title");
		showSocket = new KeyBinding("key.cookiesandcream.showSocketable.desc", 78, "cookiesandcream.title");
		
		socket.setKeyConflictContext(KeyConflictContext.GUI);
		showSocket.setKeyConflictContext(KeyConflictContext.GUI);
		
		ClientRegistry.registerKeyBinding(socket);
		ClientRegistry.registerKeyBinding(showSocket);
	}
}
