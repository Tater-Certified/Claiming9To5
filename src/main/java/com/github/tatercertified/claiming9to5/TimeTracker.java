package com.github.tatercertified.claiming9to5;

import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface TimeTracker {
    long getLastLogin();
    long getLastLogoff();
    void setLastLogin(long currenTime);
    void setLastLogoff(long currentTime);
    boolean claimDisabledCache();
    void setClaimDisabledCache(boolean enable);
    Team team();

    default int relativeToThreshold(MinecraftServer server) {
        int teamSize = team().getMembers().size();
        int percentRequired = server.getGameRules().getInt(ClaimingGamerules.TEAM_PERCENT_ONLINE_DISABLES_CLAIMS);
        // If this threshold is reached, claims will be disabled after x seconds
        int playerOnlineThreshold = (int) Math.ceil((teamSize * percentRequired) / 100.0);
        // This should be < 0 to be ok
        return team().getOnlineMembers().size() - playerOnlineThreshold;
    }

    default boolean pastLogOffTimeBuffer(MinecraftServer server, long currentTime) {
        int msBuffer = server.getGameRules().getInt(ClaimingGamerules.CLAIM_ENABLE_AFTER_SECONDS_OFFLINE) * 1000;
        boolean result = getLastLogoff() + msBuffer < currentTime;
        if (result) {
            setClaimDisabledCache(true);
        }
        return result;
    }

    default boolean pastLoginTimeBuffer(MinecraftServer server, long currentTime) {
        int msBuffer = server.getGameRules().getInt(ClaimingGamerules.CLAIM_DISABLE_AFTER_SECONDS_ONLINE) * 1000;
        boolean result = getLastLogin() + msBuffer < currentTime;
        if (result) {
            setClaimDisabledCache(true);
        }
        return result;
    }

    default boolean shouldClaimsBeDisabled(MinecraftServer server) {
        if (!server.getGameRules().getBoolean(ClaimingGamerules.DISABLE_ONLINE_PLAYER_CLAIMS)) {
            return false;
        }

        // Quickly check if the claim has been disabled already
        if (claimDisabledCache()) {
            return true;
        }

        int relativeThreshold = relativeToThreshold(server);
        long currentTime = System.currentTimeMillis();
        // Test if the threshold is met and has been met for long enough. The threshold must be 0 or greater to be met
        if (relativeThreshold >= 0) {
            return pastLoginTimeBuffer(server, currentTime);
        }
        // Test if the threshold is not met but players have not been gone for long enough
        return !pastLogOffTimeBuffer(server, currentTime);
    }

    static void onPlayerConnectionEvent(Player undefinedPlayer, boolean playerJoined) {
        if (undefinedPlayer instanceof ServerPlayer player) {
            // Quickly ignore logic if the feature is disabled
            if (!player.getServer().getGameRules().getBoolean(ClaimingGamerules.DISABLE_ONLINE_PLAYER_CLAIMS)) {
                return;
            }

            Optional<Team> playerTeam = FTBTeamsAPI.api().getManager().getTeamForPlayer(player);
            if (playerTeam.isPresent()) {
                Team team = playerTeam.get();
                if (!team.isPlayerTeam()) {
                    TimeTracker timeTracker = (TimeTracker) FTBChunksAPI.api().getManager().getOrCreateData(team);
                    boolean playerOnTeam = team.getOnlineMembers().contains(player);
                    int postEventRelativeThreshold = timeTracker.relativeToThreshold(player.getServer());
                    if (playerOnTeam && !playerJoined) {
                        postEventRelativeThreshold--;
                    } else if (!playerOnTeam && playerJoined) {
                        postEventRelativeThreshold++;
                    }

                    if (playerJoined) {
                        if (postEventRelativeThreshold == 0) {
                            // Player triggered claims to be disabled after x seconds
                            timeTracker.setLastLogin(System.currentTimeMillis());
                            announceToTeam(team, false, player.getServer());
                        } else {
                            // Just announce the current claim status to the player
                            timeTracker.announceToPlayer(player);
                        }
                    } else if (postEventRelativeThreshold == -1) {
                        // Player left and claims will be enabled after x seconds
                        timeTracker.setLastLogoff(System.currentTimeMillis());
                        announceToTeam(team, true, player.getServer());
                    }
                }
            }
        }
    }

    private static void announceToTeam(Team team, boolean willEnable, MinecraftServer server) {
        int seconds = willEnable ? server.getGameRules().getInt(ClaimingGamerules.CLAIM_ENABLE_AFTER_SECONDS_OFFLINE) : server.getGameRules().getInt(ClaimingGamerules.CLAIM_DISABLE_AFTER_SECONDS_ONLINE);
        Component notification = Component.literal((willEnable ? "Team claims will be enabled after " : "Team claims will be disabled after ") + seconds + " seconds!");
        team.getOnlineMembers().forEach(onlinePlayer -> onlinePlayer.sendSystemMessage(notification));
    }

    default void announceToPlayer(ServerPlayer player) {
        boolean claimStatus = shouldClaimsBeDisabled(player.getServer());
        Component notification = Component.literal("Team claims are " + (claimStatus ? "enabled" : "disabled"));
        player.sendSystemMessage(notification);
    }
}
