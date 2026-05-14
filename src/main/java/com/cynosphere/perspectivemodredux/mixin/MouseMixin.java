package com.cynosphere.perspectivemodredux.mixin;

import com.cynosphere.perspectivemodredux.PerspectiveModRedux;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Перехватывает MouseHandler#turnPlayer (Mojang mapping) — метод,
 * который в vanilla переводит движение мыши в поворот игрока.
 *
 * В 1.21.10 метод называется turnPlayer(double timeDelta).
 * Если Mojang переименовали — проверьте через Loom decompile.
 *
 * Когда перспектива активна:
 *  1. Отменяем стандартный поворот игрока (ci.cancel())
 *  2. Применяем дельту мыши к perspYaw / perspPitch
 *  3. Принудительно восстанавливаем углы игрока
 */
@Mixin(MouseHandler.class)
public abstract class MouseMixin {

    @Shadow @Final private Minecraft minecraft;

    // Накопленные дельты курсора (имена по Mojang mapping)
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    @Inject(
        method = "turnPlayer",
        at = @At("HEAD"),
        cancellable = true
    )
    private void perspMod$turnPlayer(double timeDelta, CallbackInfo ci) {
        if (!PerspectiveModRedux.isActive) return;

        LocalPlayer player = minecraft.player;
        if (player == null) return;
        if (minecraft.screen != null) return;  // меню открыто — не крутим

        // Отменяем стандартную обработку (иначе игрок будет поворачиваться)
        ci.cancel();

        // Читаем накопленные дельты
        double rawDX = accumulatedDX;
        double rawDY = accumulatedDY;

        // Обнуляем — vanilla тоже делает это после обработки
        accumulatedDX = 0;
        accumulatedDY = 0;

        if (rawDX == 0 && rawDY == 0) {
            PerspectiveModRedux.freezePlayer(player);
            return;
        }

        // Масштабирование чувствительности (идентично vanilla)
        double sens = minecraft.options.sensitivity().get() * 0.6 + 0.2;
        double scale = sens * sens * sens * 8.0;

        PerspectiveModRedux.perspYaw   += (float) (rawDX * scale);
        PerspectiveModRedux.perspPitch += (float) (rawDY * scale);

        // Ограничиваем тангаж ±90°
        PerspectiveModRedux.perspPitch =
            Math.max(-90f, Math.min(90f, PerspectiveModRedux.perspPitch));

        // Держим игрока замороженным
        PerspectiveModRedux.freezePlayer(player);
    }
}
