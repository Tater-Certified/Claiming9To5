package com.github.tatercertified.claiming9to5;

import net.minecraft.world.level.GameRules;

public class ClaimingGamerules {
    public static GameRules.Key<GameRules.IntegerValue> CHUNKS_REWARDED_PER_TIME_PERIOD;
    public static GameRules.Key<GameRules.IntegerValue> CHUNK_REWARDING_PERIOD_SECONDS;
    public static GameRules.Key<GameRules.BooleanValue> DISABLE_ONLINE_PLAYER_CLAIMS;
    public static GameRules.Key<GameRules.IntegerValue> CLAIM_ENABLE_AFTER_SECONDS_OFFLINE;
    public static GameRules.Key<GameRules.IntegerValue> CLAIM_DISABLE_AFTER_SECONDS_ONLINE;
    public static GameRules.Key<GameRules.IntegerValue> TEAM_PERCENT_ONLINE_DISABLES_CLAIMS;


    public static void registerGameRules() {
        CHUNKS_REWARDED_PER_TIME_PERIOD = GameRules.register(
                Claiming9to5.MODID + ":chunks_per_rewarding_period",
                GameRules.Category.MISC,
                GameRules.IntegerValue.create(1)
        );

        CHUNK_REWARDING_PERIOD_SECONDS = GameRules.register(
                Claiming9to5.MODID + ":chunks_reward_period_seconds",
                GameRules.Category.MISC,
                GameRules.IntegerValue.create(3600)
        );

        DISABLE_ONLINE_PLAYER_CLAIMS = GameRules.register(
                Claiming9to5.MODID + ":disable_online_player_claims",
                GameRules.Category.MISC,
                GameRules.BooleanValue.create(true)
        );

        CLAIM_ENABLE_AFTER_SECONDS_OFFLINE = GameRules.register(
                Claiming9to5.MODID + ":claim_enable_logoff_delay",
                GameRules.Category.MISC,
                GameRules.IntegerValue.create(60)
        );

        CLAIM_DISABLE_AFTER_SECONDS_ONLINE = GameRules.register(
                Claiming9to5.MODID + ":claim_disable_logon_delay",
                GameRules.Category.MISC,
                GameRules.IntegerValue.create(60)
        );

        TEAM_PERCENT_ONLINE_DISABLES_CLAIMS = GameRules.register(
                Claiming9to5.MODID + ":team_percent_online_disables_claims",
                GameRules.Category.MISC,
                GameRules.IntegerValue.create(1, (server, value) -> {
                    int clampedValue = Math.clamp(value.get(), 0, 100);
                    value.set(clampedValue, server);
                })
        );
    }
}
