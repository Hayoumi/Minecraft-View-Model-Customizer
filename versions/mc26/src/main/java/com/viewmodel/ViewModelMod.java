package com.viewmodel;

import com.mojang.blaze3d.platform.InputConstants;
import com.viewmodel.client.gui.ViewmodelConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ViewModelMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("viewmodel");
    private static KeyMapping openScreenKey;

    @Override
    public void onInitializeClient() {
        ViewModelConfig.load();

        openScreenKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.viewmodel.open_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("viewmodel", "controls"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ViewModelConfig.tick();
            while (openScreenKey.consumeClick()) {
                openConfigScreen(client);
            }
        });
    }

    private static void openConfigScreen(Minecraft client) {
        if (client == null || client.player == null) {
            return;
        }
        client.setScreen(new ViewmodelConfigScreen(client.screen));
    }
}
