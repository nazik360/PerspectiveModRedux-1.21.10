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

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(method = "prepare", at = @At("TAIL"))
    private void perspMod$fixCameraYaw(Level level, Camera camera, Entity entity, CallbackInfo ci) {
        if (!PerspectiveModRedux.isActive) return;
        EntityRenderDispatcher dispatcher = (EntityRenderDispatcher)(Object)this;
        dispatcher.overrideCameraOrientation(PerspectiveModRedux.perspYaw, 0f);
    }
}
