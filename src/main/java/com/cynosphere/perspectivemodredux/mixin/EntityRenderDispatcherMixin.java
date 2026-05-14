package com.cynosphere.perspectivemodredux.mixin;

import com.cynosphere.perspectivemodredux.PerspectiveModRedux;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Исправляет cameraYaw в EntityRenderDispatcher, чтобы теги имён и тени
 * ориентировались по камере перспективы, а не по игроку.
 *
 * Mojang mapping 1.21.10:
 *   EntityRenderDispatcher#prepare(Level, Camera, Entity)
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Inject(
        method = "prepare",
        at = @At("TAIL")
    )
    private void perspMod$fixCameraYaw(
            Level level,
            Camera camera,
            Entity highlightedEntity,
            CallbackInfo ci) {

        if (!PerspectiveModRedux.isActive) return;

        // Перезаписываем cameraYaw: теги имён будут смотреть в сторону камеры
        ((EntityRenderDispatcher)(Object)this).cameraOrientation.yRot =
            PerspectiveModRedux.perspYaw;
    }
}
