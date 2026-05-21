package io.github.proxima812.proximahammers.fabric.mixin;

import io.github.proxima812.proximahammers.HammerPowers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixinFabric {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void ProximaHammers$blockThorImpactDamage(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerPlayer player && HammerPowers.blocksThorImpactDamage(player, damageSource)) {
            player.resetFallDistance();
            cir.setReturnValue(false);
        }
    }
}
