package com.github.tatercertified.claiming9to5;

import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.data.ChunkTeamDataImpl;
import dev.ftb.mods.ftbchunks.net.SendGeneralDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
            if (((PlayerTimeTracker) player).isRewardTime(System.currentTimeMillis())) {
                grantChunks(player);
            }
        }
    }

    private static void grantChunks(ServerPlayer player) {
        int maxClaimChunks = player.getServer().getGameRules().getInt(ClaimingGamerules.MAX_PLAYER_CHUNK_CLAIMS);
        // Update player data
        ChunkTeamDataImpl data = (ChunkTeamDataImpl) FTBChunksAPI.api().getManager().getPersonalData(player.getUUID());
        int currentExtra = data.getExtraClaimChunks();

        if (maxClaimChunks == -1 || maxClaimChunks > currentExtra) {
            data.setExtraClaimChunks(Math.min(maxClaimChunks, currentExtra + player.level().getGameRules().getInt(ClaimingGamerules.CHUNKS_REWARDED_PER_TIME_PERIOD)));
            data.markDirty();

            // Update team data
            ChunkTeamDataImpl teamData = (ChunkTeamDataImpl) FTBChunksAPI.api().getManager().getOrCreateData(player);
            teamData.updateLimits();
            SendGeneralDataPacket.send(teamData, player);
            player.sendSystemMessage(Component.literal("You now have " + data.getExtraClaimChunks() + " claim chunks"), true);
        }
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