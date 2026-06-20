package com.github.tatercertified.claiming9to5;

import dev.ftb.mods.ftbchunks.api.ChunkTeamData;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@Mod(Claiming9to5.MODID)
public class Claiming9to5 {
    public static final String MODID = "claiming9to5";

    public Claiming9to5(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ClaimingGamerules.registerGameRules();
    }

    @SubscribeEvent
    public void onPlayerPostTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            int totalPlayTimeTicks = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
            if (totalPlayTimeTicks > 0 && totalPlayTimeTicks % (player.level().getGameRules().getInt(ClaimingGamerules.CHUNK_REWARDING_PERIOD_SECONDS) * 20) == 0) {
                grantChunks(player);
            }
        }
    }

    private static void grantChunks(ServerPlayer player) {
        ChunkTeamData data = FTBChunksAPI.api().getManager().getOrCreateData(player);
        int currentExtra = data.getExtraClaimChunks();
        data.setExtraClaimChunks(currentExtra + player.level().getGameRules().getInt(ClaimingGamerules.CHUNKS_REWARDED_PER_TIME_PERIOD));
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        TimeTracker.onPlayerConnectionEvent(event.getEntity(), true);
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        TimeTracker.onPlayerConnectionEvent(event.getEntity(), false);
    }
}