package com.cynosphere.perspectivemodredux.mixin;

import com.cynosphere.perspectivemodredux.PerspectiveModRedux;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
            shift = At.Shift.AFTER
        )
    )
    private void perspMod$patchCamera(net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!PerspectiveModRedux.isActive) return;
        if (minecraft.player == null) return;
        Camera camera = minecraft.gameRenderer.getMainCamera();
        if (camera == null) return;
        ((CameraAccessor)(Object)camera).perspMod$setRotation(
            PerspectiveModRedux.perspYaw,
            PerspectiveModRedux.perspPitch
        );
    }
}
