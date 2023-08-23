package me.jellysquid.mods.sodium.client.util.workarounds;

import me.jellysquid.mods.sodium.client.gui.console.Console;
import me.jellysquid.mods.sodium.client.gui.console.message.MessageLevel;
import me.jellysquid.mods.sodium.client.util.workarounds.driver.nvidia.NvidiaGLContextInfo;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostLaunchChecks {
    private static final Logger LOGGER = LoggerFactory.getLogger("Sodium-PostlaunchChecks");

    public static void checkContext() {
        checkContextImplementation();
    }

    private static void checkContextImplementation() {
        GLContextInfo driver = getGraphicsContextInfo();

        if (driver == null) {
            LOGGER.warn("Could not retrieve identifying strings for OpenGL implementation");
            return;
        }

        LOGGER.info("OpenGL Vendor: {}", driver.vendor());
        LOGGER.info("OpenGL Renderer: {}", driver.renderer());
        LOGGER.info("OpenGL Version: {}", driver.version());

        if (isBrokenNvidiaDriverInstalled(driver)) {
            showConsoleMessage(Text.of("sodium.console.broken_nvidia_driver"));
            logMessage("The NVIDIA graphics driver appears to be out of date. This will likely cause severe " +
                    "performance issues and crashes when used with Sodium. The graphics driver should be updated to " +
                    "the latest version (version 536.23 or newer).");
        }
    }

    @Nullable
    private static GLContextInfo getGraphicsContextInfo() {
        String vendor = GL11C.glGetString(GL11C.GL_VENDOR);
        String renderer = GL11C.glGetString(GL11C.GL_RENDERER);
        String version = GL11C.glGetString(GL11C.GL_VERSION);

        if (vendor == null || renderer == null || version == null) {
            return null;
        }

        return new GLContextInfo(vendor, renderer, version);
    }

    private static void showConsoleMessage(MutableText message) {
        Console.instance().logMessage(MessageLevel.SEVERE, message, 30.0);
    }

    private static void logMessage(String message, Object... args) {
        LOGGER.error(message, args);
    }

    private static boolean isBrokenNvidiaDriverInstalled(GLContextInfo driver) {
        if (Util.getOperatingSystem() != Util.OperatingSystem.WINDOWS) {
            return false;
        }

        var version = NvidiaGLContextInfo.tryParse(driver);

        if (version != null) {
            return version.isWithinRange(
                    new NvidiaGLContextInfo(526, 47), // Broken in 526.47
                    new NvidiaGLContextInfo(536, 23) // Fixed in 536.23
            );
        }

        return false;
    }
}
