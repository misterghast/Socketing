package org.hypbase.ghast.socketing.client;

import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.hypbase.ghast.socketing.Socketing;
import org.hypbase.ghast.socketing.common.socketing.SocketingPacketHandler;
import org.hypbase.ghast.socketing.common.socketing.SocketingRegistry;
import org.hypbase.ghast.socketing.common.socketing.packets.CCheckValidRecipePacket;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;
public class RenderEvents {
	
	private static final String validRecipeString = "       =   ";
	private static final String invalidRecipeString = "Not a socketing recipe.";
	private static final int verticalOffset = 0;
	private static final double itemSizeMultiplier = 1;
	
	private static SocketingAction socketingAction;
	private static boolean crafted = false;
	private static boolean held = false;
	private static boolean displayed = false;
	private static boolean isValidRecipe = false;
	private static ItemStack result = null;
	private static Slot lastSlot = null;
	private static boolean justPressed = false;
	private static StringTextComponent toDisplay = null;
	private static int validToolTipScale = 1;
	
	public static void setValidRecipe(boolean isValid) {
		isValidRecipe = isValid;
	}
	
	public static void setResultItem(ItemStack item) {
		result = item;
	}
	
	public void drawGradientTooltip(Matrix4f mat, int zLevel, int tooltipX, int tooltipY, int tooltipHeight, int tooltipWidth, int backgroundColor, int borderColorStart, int borderColorEnd, int borderSize) {
		GuiUtils.drawGradientRect(mat, zLevel, tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 1, tooltipY + tooltipHeight, tooltipX + tooltipWidth + 1, tooltipY + tooltipHeight + borderSize, borderColorStart, borderColorStart);
		GuiUtils.drawGradientRect(mat, zLevel, tooltipX - borderSize, tooltipY - 1, tooltipX, tooltipY + tooltipHeight + 1, borderColorEnd, borderColorStart);
		GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipWidth, tooltipY - 1, tooltipX + tooltipWidth + borderSize, tooltipY + tooltipHeight + 1, borderColorEnd, borderColorStart);
		GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 1, tooltipY - borderSize, tooltipX + tooltipWidth + 1, tooltipY, borderColorEnd, borderColorEnd);
		GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 1, tooltipY + tooltipHeight + borderSize, tooltipX + tooltipWidth + 1, tooltipY + tooltipHeight + borderSize + 1, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(mat, zLevel, tooltipX - borderSize - 1, tooltipY - 1, tooltipX - borderSize, tooltipY + tooltipHeight + 1, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipWidth + borderSize, tooltipY - 1, tooltipX + tooltipWidth + borderSize + 1, tooltipY + tooltipHeight + 1, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 1, tooltipY - borderSize - 1, tooltipX + tooltipWidth + 1, tooltipY - borderSize, backgroundColor, backgroundColor);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onGuiClick(GuiScreenEvent.MouseClickedEvent event) {
		Screen gui = event.getGui();
		if(gui instanceof ContainerScreen && SocketingKeyBindings.socket.isDown()) {
			ContainerScreen container = (ContainerScreen) gui;
			Slot slotBelow = container.getSlotUnderMouse();
			ClientPlayerEntity player = Minecraft.getInstance().player;
			ItemStack held = player.inventory.getCarried();
			
			if(slotBelow != null && slotBelow.hasItem() && !slotBelow.getItem().isEmpty() && slotBelow.container == player.inventory && held != null && !held.isEmpty()) {
				//System.out.println(held);
				SocketingAction.craft(held, slotBelow.getItem(), slotBelow.getSlotIndex(), player.level);
				//player.inventory.setCarried(ItemStack.EMPTY);
				crafted = true;
			}
		} else {
			System.out.println("bruh");
		}
		
		if(crafted) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onGuiTap(GuiScreenEvent.KeyboardKeyPressedEvent event) {
		Screen gui = event.getGui();
		if(gui instanceof ContainerScreen && (event.getKeyCode() == SocketingKeyBindings.socket.getKey().getValue())) {
			held = true;
		}
		
		if(gui instanceof ContainerScreen && event.getKeyCode() == 256) {
			held = false;
			displayed = false;
			result = null;
			toDisplay = null;
		}
	}
	
	
	@SubscribeEvent
	public void onGuiDoneTap(GuiScreenEvent.KeyboardKeyReleasedEvent event) {
		Screen gui = event.getGui();
		if(gui instanceof ContainerScreen && event.getKeyCode() == SocketingKeyBindings.socket.getKey().getValue()) {
			held = false;
			displayed = false;
			result = null;
			toDisplay = null;
			isValidRecipe = false;
			lastSlot = null;
		}
	}
	
	@SubscribeEvent
	public void onGuiMouseRelease(GuiScreenEvent.MouseReleasedEvent event) {
		if(crafted) {
			event.setCanceled(true);
			crafted = false;
		}
	}
	
	@SubscribeEvent
	public void renderGUI(GuiScreenEvent.DrawScreenEvent.Post event) {
		Screen gui = event.getGui();
		if(gui instanceof ContainerScreen && held && !displayed) {
			displayed = true;
		} 
		if(displayed && gui instanceof ContainerScreen) {
			PlayerInventory i = Minecraft.getInstance().player.inventory;
			ContainerScreen is = (ContainerScreen) event.getGui();
			if(is.getSlotUnderMouse() != null && (is.getSlotUnderMouse() != lastSlot || justPressed) && i.getCarried() != null) {
				SocketingPacketHandler.INST.sendToServer(new CCheckValidRecipePacket(i.getCarried(), is.getSlotUnderMouse().getItem()));
				lastSlot = is.getSlotUnderMouse();
			}	
		}		
		
		if(displayed) {
			
			MatrixStack s = event.getMatrixStack();
			Matrix4f mat = s.last().pose();
			FontRenderer f = Minecraft.getInstance().font;
			List<ITextProperties> l = new ArrayList<ITextProperties>();
			if(isValidRecipe) {
				l.add(ITextProperties.of(validRecipeString));
				
			} else {
			    l.add(ITextProperties.of(invalidRecipeString));
			}
			//GuiUtils.drawHoveringText(s, l, event.getMouseX(), event.getMouseY(), event.getGui().height, event.getGui().width, f.width(toDisplay) + 20, Color.black.getRGB(), Color.green.getRGB(), Color.CYAN.getRGB(), f);
			int zLevel = 400;
			int tooltipX = event.getMouseX();
			int tooltipY = event.getMouseY();
			int tooltipHeight = 24;
			int tooltipTextWidth = f.width(l.get(0));
			int backgroundColor = Color.black.getRGB();
			int borderColorStart = Color.green.getRGB();
			int borderColorEnd = Color.cyan.getRGB();
			s.pushPose();
			if(isValidRecipe) {
				tooltipTextWidth = 75;
				drawGradientTooltip(mat, zLevel, tooltipX - tooltipTextWidth - 24, tooltipY - 4, tooltipHeight, tooltipTextWidth, backgroundColor, borderColorStart, borderColorEnd, 1);
				s.translate(0, 0, 401);
				s.scale(validToolTipScale, validToolTipScale, 1.0f);
				f.draw(s, "+", (float)(event.getMouseX() - tooltipTextWidth - 24 + ((21 * itemSizeMultiplier))) / validToolTipScale, (float)(event.getMouseY() + 4 * itemSizeMultiplier) / validToolTipScale, Color.white.getRGB());
				f.draw(s, "=", (float)(event.getMouseX() - tooltipTextWidth - 24 + ((49.5 * itemSizeMultiplier))) / validToolTipScale, (float)(event.getMouseY() + 4 * itemSizeMultiplier) / validToolTipScale, Color.WHITE.getRGB());
				s.popPose();
			} else {
				drawGradientTooltip(mat, zLevel, tooltipX - tooltipTextWidth - 24, tooltipY, f.lineHeight, tooltipTextWidth, backgroundColor, borderColorStart, borderColorEnd, 1);
				s.popPose();
				s.pushPose();
				s.translate(0, 0, 400);
				f.draw(s, invalidRecipeString, event.getMouseX() - f.width(invalidRecipeString) - 23, event.getMouseY(), Color.white.getRGB());			
				s.popPose();
			}
			if(isValidRecipe && gui instanceof ContainerScreen) {
				ContainerScreen cgui = (ContainerScreen) gui;
				if(cgui.getSlotUnderMouse() != null) {
					String p1 = cgui.getSlotUnderMouse().getItem().getItem().getRegistryName().getPath();
					System.out.println(p1);
					ItemRenderer r = Minecraft.getInstance().getItemRenderer();
					RenderSystem.pushMatrix();
					//Deprecated but Mojang still uses it so.
					RenderSystem.translatef(0.0f, 0.0f, 402.0f);
					RenderSystem.scalef((float)itemSizeMultiplier, (float)itemSizeMultiplier, 1.0f);
					r.renderGuiItem(cgui.getSlotUnderMouse().getItem(), (int)((event.getMouseX() - tooltipTextWidth - 24 + (2 * itemSizeMultiplier)) / itemSizeMultiplier), (int)((event.getMouseY() - verticalOffset) / itemSizeMultiplier));
					r.renderGuiItem(Minecraft.getInstance().player.inventory.getCarried(), (int)((event.getMouseX() - tooltipTextWidth - 24 + (29 * itemSizeMultiplier)) / itemSizeMultiplier), (int)((event.getMouseY() - verticalOffset) / itemSizeMultiplier));
					r.renderGuiItem(result, (int)((event.getMouseX() - tooltipTextWidth - 24 + (57 * itemSizeMultiplier)) / itemSizeMultiplier), (int)((event.getMouseY() - verticalOffset) / itemSizeMultiplier));
					//s.translate(0, 0, 401);	
					RenderSystem.popMatrix();
					
					if(cgui.getSlotUnderMouse().getItem().getCount() - 1 <= 0 || Minecraft.getInstance().player.inventory.getCarried().getCount() - 1 <= 0) {
						SocketingPacketHandler.INST.sendTo(new CCheckValidRecipePacket(Minecraft.getInstance().player.inventory.getCarried(), cgui.getSlotUnderMouse().getItem()), Minecraft.getInstance().getConnection().getConnection(), NetworkDirection.PLAY_TO_SERVER);
						lastSlot = cgui.getSlotUnderMouse();
					}
				}
			}
			if(justPressed) {
				justPressed = false;
			}
			//ContainerScreen.blit(s, 10, 10, 10, 10, 10, 10, 10, 10, 10);
		}
	}
	
	
	/*@SubscribeEvent
	public void renderGui(DrawScreenEvent.Post event) {
		Screen gui = event.getGui();
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayerEntity player = minecraft.player;
		
		ItemStack held = player.inventory.getCarried();
		
		if (gui instanceof ContainerScreen && SocketingKeyBindings.showSocket.isDown()) {
			ContainerScreen container = (ContainerScreen) gui;
			ItemStack inv = container.getSlotUnderMouse().getItem();
			ItemStack validItem = SocketingRegistry.getRecipeFromIngredients(minecraft.level, held, inv).getResult();
			
			if(validItem != null && inv != null && held != null) {
				System.out.println("Debug worked.");
			} else {
				System.out.println("Debug failed because of null. validItem: " + validItem.toString() + " inv: " + inv.toString() + " held: " + held.toString());
			}
		} else {
			System.out.println("Debug failed.");
		}
	}*/
}
