package com.github.tatercertified.claiming9to5.mixin;

import com.github.tatercertified.claiming9to5.ClaimingGamerules;
import com.github.tatercertified.claiming9to5.PlayerTimeTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements PlayerTimeTracker {
    @Shadow
    public abstract ServerStatsCounter getStats();

    @Shadow
    public abstract ServerLevel serverLevel();

    private long nextTimeMs = -1;

    @Override
    public void setNextRewardTime(long currentTime) {
        int totalPlayTimeTicks = this.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
        int chunkRewardingTicks = this.serverLevel().getGameRules().getInt(ClaimingGamerules.CHUNK_REWARDING_PERIOD_SECONDS) * 20;
        int ticksTillNext = chunkRewardingTicks - totalPlayTimeTicks % chunkRewardingTicks;
        this.nextTimeMs = currentTime + (ticksTillNext * 50L); // Turn ticks into ms
    }

    @Override
    public boolean isRewardTime(long currentTime) {
        if (this.nextTimeMs < 0) {
            setNextRewardTime(currentTime);
            return false;
        }

        boolean result = currentTime >= nextTimeMs;
        if (result) {
            setNextRewardTime(currentTime);
        }
        return result;
    }
}
