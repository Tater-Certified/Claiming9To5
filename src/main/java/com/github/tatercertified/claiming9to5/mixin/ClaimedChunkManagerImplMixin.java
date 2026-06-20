package com.github.tatercertified.claiming9to5.mixin;

import com.github.tatercertified.claiming9to5.TimeTracker;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbchunks.api.Protection;
import dev.ftb.mods.ftbchunks.data.ClaimedChunkImpl;
import dev.ftb.mods.ftbchunks.data.ClaimedChunkManagerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClaimedChunkManagerImpl.class)
public abstract class ClaimedChunkManagerImplMixin {
    @Shadow
    public abstract MinecraftServer getMinecraftServer();

    @Inject(method = "shouldPreventInteraction", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbchunks/api/Protection;getProtectionPolicy(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/InteractionHand;Ldev/ftb/mods/ftbchunks/api/ClaimedChunk;Lnet/minecraft/world/entity/Entity;)Ldev/ftb/mods/ftbchunks/api/ProtectionPolicy;", ordinal = 0), cancellable = true)
    private void claiming9To5$disableClaimsIfOnline(Entity actor, InteractionHand hand, BlockPos pos, Protection protection, Entity targetEntity, CallbackInfoReturnable<Boolean> cir, @Local(name = "chunk") ClaimedChunkImpl chunk) {
        if (((TimeTracker) chunk.getTeamData()).shouldClaimsBeDisabled(this.getMinecraftServer())) {
            cir.setReturnValue(false);
        }
    }
}
