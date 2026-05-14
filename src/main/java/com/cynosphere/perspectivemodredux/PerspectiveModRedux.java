package com.cynosphere.perspectivemodredux;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class PerspectiveModRedux implements ClientModInitializer {
    public static boolean isActive = false;
    public static float savedYaw = 0f;
    public static float savedPitch = 0f;
    public static float perspYaw = 0f;
    public static float perspPitch = 0f;
    private static KeyMapping perspectiveKey;
    private static boolean prevKeyDown = false;

    @Override
    public void onInitializeClient() {
        perspectiveKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.perspectivemodredux.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F4,
                KeyBindingHelper.registerKeyCategory("key.categories.perspectivemodredux")
            )
        );
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(Minecraft mc) {
        if (mc.player == null) return;
        boolean keyDown = perspectiveKey.isDown();
        if (keyDown && !prevKeyDown) {
            if (isActive) deactivate(mc); else activate(mc);
        }
        prevKeyDown = keyDown;
        if (isActive) freezePlayer(mc.player);
    }

    public static void activate(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null) return;
        isActive = true;
        savedYaw = player.getYRot();
        savedPitch = player.getXRot();
        perspYaw = savedYaw;
        perspPitch = savedPitch;
        if (mc.options.getCameraType().isFirstPerson()) {
            mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);
        }
    }

    public static void deactivate(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null) return;
        isActive = false;
        player.setYRot(savedYaw);
        player.setXRot(savedPitch);
        player.yHeadRot = savedYaw;
        player.yHeadRotO = savedYaw;
        player.yBodyRot = savedYaw;
        player.yBodyRotO = savedYaw;
        mc.options.setCameraType(net.minecraft.client.CameraType.FIRST_PERSON);
    }

    public static void freezePlayer(LocalPlayer player) {
        player.setYRot(savedYaw);
        player.setXRot(savedPitch);
        player.yHeadRot = savedYaw;
        player.yHeadRotO = savedYaw;
        player.yBodyRot = savedYaw;
        player.yBodyRotO = savedYaw;
    }
}
