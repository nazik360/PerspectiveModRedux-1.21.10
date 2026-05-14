package com.cynosphere.perspectivemodredux.mixin;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Camera.class)
public abstract class CameraAccessor {
    @Shadow protected abstract void setRotation(float yRot, float xRot);

    @Unique
    public void perspMod$setRotation(float yRot, float xRot) {
        setRotation(yRot, xRot);
    }
}
