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

    private long nextTimeMs;

    @Override
    public void setNextRewardTime(long currentTime) {
        int totalPlayTimeTicks = this.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
        if (totalPlayTimeTicks == 0) {
            totalPlayTimeTicks++;
        }
        int ticksTillNext = totalPlayTimeTicks % (this.serverLevel().getGameRules().getInt(ClaimingGamerules.CHUNK_REWARDING_PERIOD_SECONDS) * 20);
        this.nextTimeMs = currentTime + ticksTillNext / 20;
    }

    @Override
    public boolean isRewardTime(long currentTime) {
        boolean result = currentTime >= nextTimeMs;
        if (result) {
            setNextRewardTime(currentTime);
        }
        return result;
    }
}
