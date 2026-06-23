package com.github.tatercertified.claiming9to5.mixin;

import com.github.tatercertified.claiming9to5.TimeTracker;
import dev.ftb.mods.ftbchunks.data.ChunkTeamDataImpl;
import dev.ftb.mods.ftbteams.api.Team;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkTeamDataImpl.class)
public class ChunkTeamDataImplMixin implements TimeTracker {
    @Shadow
    @Final
    private Team team;
    private long lastLoginTime;
    private long lastLogoffTime;
    private boolean claimDisabledCache;

    @Override
    public long getLastLogin() {
        return this.lastLoginTime;
    }

    @Override
    public long getLastLogoff() {
        return this.lastLogoffTime;
    }

    @Override
    public void setLastLogin(long currenTime) {
        this.lastLoginTime = currenTime;
    }

    @Override
    public void setLastLogoff(long currentTime) {
        this.lastLogoffTime = currentTime;
    }

    @Override
    public boolean claimDisabledCache() {
        return this.claimDisabledCache;
    }

    @Override
    public void setClaimDisabledCache(boolean enable) {
        this.claimDisabledCache = enable;
    }

    @Override
    public Team team() {
        return this.team;
    }
}
