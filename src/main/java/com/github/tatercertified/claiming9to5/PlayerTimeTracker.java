package com.github.tatercertified.claiming9to5;

public interface PlayerTimeTracker {
    void setNextRewardTime(long currentTime);
    boolean isRewardTime(long currentTime);
}
