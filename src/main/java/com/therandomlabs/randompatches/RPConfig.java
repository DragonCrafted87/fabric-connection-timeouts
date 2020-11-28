package com.therandomlabs.randompatches;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.therandomlabs.randompatches.client.WindowIconHandler;
import com.therandomlabs.utils.config.Config;
import com.therandomlabs.utils.forge.ForgeUtils;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

@Config(id = RandomPatches.MOD_ID, comment = "RandomPatches configuration")
public final class RPConfig {
	public static final class Boats {
		@Config.Property(
				"Prevents underwater boat passengers from being ejected after 60 ticks (3 seconds)."
		)
		public static boolean preventUnderwaterBoatPassengerEjection = ForgeUtils.IS_DEOBFUSCATED;

		@Config.Property({
				"The buoyancy of boats when they are under flowing water.",
				"The vanilla default is -0.0007."
		})
		public static double underwaterBoatBuoyancy = ForgeUtils.IS_DEOBFUSCATED ? 5.0 : 0.023;
	}

	public static final class Client {
		@Config.Category("Options related to the Minecraft window.")
		public static final Window window = null;

		@Config.Property(
				"Forces Minecraft to show the title screen after disconnecting rather than " +
						"the Multiplayer or Realms menu."
		)
		public static boolean forceTitleScreenOnDisconnect = ForgeUtils.IS_DEOBFUSCATED;

		@Config.RequiresRestart
		@Config.RangeDouble(min = Double.MIN_VALUE, max = 260.0)
		@Config.Property({
				"The framerate limit slider step size.",
				"If this is set to 10.0, vanilla behavior is not changed."
		})
		public static float framerateLimitSliderStepSize = 1.0F;

		@Config.Property("Whether to remove the glowing effect from potions.")
		public static boolean removePotionGlint = ForgeUtils.IS_DEOBFUSCATED;
	}

	public static final class Misc {
		@Config.Property("Enables the portal bucket replacement fix for Nether portals.")
		public static boolean portalBucketReplacementFixForNetherPortals;

		@Config.RequiresReload
		@Config.Property("Enables the /rpreload command.")
		public static boolean rpreload = true;

		@Config.RangeInt(min = 257)
		@Config.Property({
				"The packet size limit.",
				"The vanilla limit is " + 0x200000 + "."
		})
		public static int packetSizeLimit = 0x1000000;

		@Config.Property(
				"Whether skull stacking requires the same textures or just the same player profile."
		)
		public static boolean skullStackingRequiresSameTextures = true;

		public static long packetSizeLimitLong;

		public static void onReload() {
			packetSizeLimitLong = packetSizeLimit;
		}
	}

	public static final class SpeedLimits {
		@Config.RangeDouble(min = 1.0)
		@Config.Property({
				"The maximum player speed.",
				"The vanilla default is 100.0."
		})
		public static float maxPlayerSpeed = 1000000.0F;

		@Config.RangeDouble(min = 1.0)
		@Config.Property({
				"The maximum player elytra speed.",
				"The vanilla default is 300.0."
		})
		public static float maxPlayerElytraSpeed = 1000000.0F;

		@Config.RangeDouble(min = 1.0)
		@Config.Property({
				"The maximum player vehicle speed.",
				"The vanilla default is 100.0."
		})
		public static double maxPlayerVehicleSpeed = 1000000.0;
	}

	public static final class Timeouts {
		@Config.RangeInt(min = 1)
		@Config.Property("The interval at which the server sends the KeepAlive packet.")
		public static int keepAlivePacketInterval = 15;

		@Config.RangeInt(min = 1)
		@Config.Property("The login timeout in ticks.")
		public static int loginTimeout = 1800;

		@Config.RangeInt(min = 1)
		@Config.Property({
				"The read timeout in seconds.",
				"This is the time it takes for a player to be disconnected after not " +
						"responding to a KeepAlive packet.",
				"This value is automatically rounded up to a product of keepAlivePacketInterval."
		})
		public static int readTimeout = 90;

		public static long keepAlivePacketIntervalMillis;
		public static long keepAlivePacketIntervalLong;
		public static long readTimeoutMillis;

		public static void onReload() {
			if (readTimeout < keepAlivePacketInterval) {
				readTimeout = keepAlivePacketInterval * 2;
			} else if (readTimeout % keepAlivePacketInterval != 0) {
				readTimeout = keepAlivePacketInterval * (readTimeout / keepAlivePacketInterval + 1);
			}

			keepAlivePacketIntervalMillis = keepAlivePacketInterval * 1000L;
			keepAlivePacketIntervalLong = keepAlivePacketInterval;
			readTimeoutMillis = readTimeout * 1000L;

			System.setProperty("forge.readTimeout", Integer.toString(readTimeout));
		}
	}

	public static final class Window {
		public static final Path DEFAULT_ICON = Paths.get(
				ForgeUtils.IS_DEOBFUSCATED ? "../src/main/resources/logo.png" : ""
		);

		@Config.Property({
				"The path to the 16x16 Minecraft window icon.",
				"Leave this and the 32x32 icon blank to use the default icon."
		})
		public static Path icon16 = DEFAULT_ICON;

		@Config.Property({
				"The path to the 32x32 Minecraft window icon.",
				"Leave this and the 16x16 icon blank to use the default icon."
		})
		public static Path icon32 = DEFAULT_ICON;

		@Config.Property({
				"The path to the 256x256 window icon which is used on Mac OS X.",
				"Leave this, the 16x16 icon and the 32x32 icon blank to use the default icon."
		})
		public static Path icon256 = DEFAULT_ICON;

		@Config.Property({
				"The Minecraft window title.",
				"The Minecraft version is provided as an argument."
		})
		public static String title = ForgeUtils.IS_DEOBFUSCATED ? "RandomPatches" : "Minecraft* %s";

		@Config.Property({
				"The Minecraft window title.",
				"The Minecraft version and current activity are provided as arguments.",
				"For example: \"RandomPatches - %2$s\""
		})
		public static String titleWithActivity =
				ForgeUtils.IS_DEOBFUSCATED ? "RandomPatches - %2$s" : "Minecraft* %s - %s";

		public static String icon16String;
		public static String icon32String;
		public static String icon256String;

		public static boolean setWindowSettings = true;

		public static void onReload() {
			onReload(true);
		}

		public static void onReload(boolean applySettings) {
			icon16String = icon16.toString();
			icon32String = icon32.toString();
			icon256String = icon256.toString();

			if (icon16String.isEmpty()) {
				if (!icon256String.isEmpty()) {
					icon16 = icon256;
					icon16String = icon256String;
				} else if (!icon32String.isEmpty()) {
					icon16 = icon32;
					icon16String = icon32String;
				}
			}

			if (icon32String.isEmpty()) {
				if (!icon256String.isEmpty()) {
					icon32 = icon256;
					icon32String = icon256String;
				} else if (!icon16String.isEmpty()) {
					icon32 = icon16;
					icon32String = icon16String;
				}
			}

			if (icon256String.isEmpty()) {
				if (!icon32String.isEmpty()) {
					icon256 = icon32;
					icon256String = icon32String;
				} else if (!icon16String.isEmpty()) {
					icon256 = icon16;
					icon256String = icon16String;
				}
			}

			if (ForgeUtils.IS_CLIENT && setWindowSettings && applySettings) {
				Minecraft.getInstance().execute(Window::setWindowSettings);
			}
		}

		private static void setWindowSettings() {
			final MainWindow mainWindow = Minecraft.getInstance().getMainWindow();

			if (mainWindow == null) {
				return;
			}

			final long handle = mainWindow.getHandle();

			if (!icon16String.isEmpty()) {
				WindowIconHandler.setWindowIcon(handle);
			}

			GLFW.glfwSetWindowTitle(handle, title);
		}
	}

	@Config.Category("Options related to boats.")
	public static final Boats boats = null;

	@Config.Category("Options related to client-sided features.")
	public static final Client client = null;

	@Config.Category("Options that don't fit into any other categories.")
	public static final Misc misc = null;

	@Config.Category("Options related to the movement speed limits.")
	public static final SpeedLimits speedLimits = null;

	@Config.Category("Options related to the disconnect timeouts.")
	public static final Timeouts timeouts = null;
}
