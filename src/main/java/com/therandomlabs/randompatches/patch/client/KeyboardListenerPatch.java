package com.therandomlabs.randompatches.patch.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.ChatOptionsScreen;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public final class KeyboardListenerPatch {
	public static final class ToggleNarratorKeybind {
		public static final Minecraft mc = Minecraft.getInstance();
		public static KeyBinding keybind;

		private ToggleNarratorKeybind() {}

		public static void register() {
			keybind = new KeyBinding(
					"key.narrator",
					new IKeyConflictContext() {
						@Override
						public boolean isActive() {
							return !(mc.currentScreen instanceof ControlsScreen);
						}

						@Override
						public boolean conflicts(IKeyConflictContext other) {
							return true;
						}
					},
					KeyModifier.CONTROL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_B,
					"key.categories.misc"
			);

			ClientRegistry.registerKeyBinding(keybind);
		}
	}

	private KeyboardListenerPatch() {}

	public static void handleKeypress(int key) {
		if(ToggleNarratorKeybind.keybind == null) {
			return;
		}

		if(!ToggleNarratorKeybind.keybind.isActiveAndMatches(
				InputMappings.Type.KEYSYM.getOrMakeInput(key)
		)) {
			return;
		}

		final Minecraft mc = ToggleNarratorKeybind.mc;

		AbstractOption.NARRATOR.func_216722_a(mc.gameSettings, 1);

		if(mc.currentScreen instanceof ChatOptionsScreen) {
			((ChatOptionsScreen) mc.currentScreen).updateNarratorButton();
		} else if(mc.currentScreen instanceof AccessibilityScreen) {
			((AccessibilityScreen) mc.currentScreen).func_212985_a();
		}
	}
}
