package io.github.proxima812.proximahammers.fabric.mixin;

import io.github.proxima812.proximahammers.HammerPowers;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixinFabric {
    @Inject(method = "setPlayerInput", at = @At("HEAD"))
    private void ProximaHammers$setPlayerInput(float sidewaysSpeed, float forwardSpeed, boolean jumping, boolean sneaking, CallbackInfo ci) {
        HammerPowers.setJumping((ServerPlayer) (Object) this, jumping);
    }
}
