# Claiming 9 To 5
## It's all taking and no giving, but at least your base won't be missing!

Claiming 9-5 is a FTB Chunks addon that ensures that your claims are protected while offline, but disables claims when online.
This is ideal for servers where base griefing is encouraged, but offline players should be able to sleep well knowing they won't wake up to a crater as a base.

## You think you deserve a fair promotion?
I know how it feels when you want to move ahead, but the boss won't seem to let you. But with Claiming 9 To 5, there's no need to worry!
You can simply allow players to gain extra chunk claims every X seconds by setting the respective gamerules mentioned in the next sections!

## Your friends want to play, but you're still working 9 To 5?
Hey, what a way to make a living! But rest assured that your base can still be protected even if some of your team is online with our gamerules:

| GameRule                                         | Default Value | Description                                                                                                             |
|--------------------------------------------------|---------------|-------------------------------------------------------------------------------------------------------------------------|
| claiming9to5:chunks_per_rewarding_period         | 1             | The number of additional chunks rewarded to a player for playing for a specific period of time                          |
| claiming9to5:chunks_reward_period_seconds        | 3600          | The specific period of time mentioned above in seconds                                                                  |
| claiming9to5:claim_disable_logon_delay           | 60            | The delay before disabling claims when a player joins                                                                   |
| claiming9to5:claim_enable_logoff_delay           | 60            | The delay before enabling claims when a player leaves                                                                   |
| claiming9to5:disable_online_player_claims        | true          | Whether to disable claims when players are online                                                                       |
| claiming9to5:max_chunk_claims                    | 10            | The maximum number of chunk claims a player can obtain. Set to -1 to disable                                            |
| claiming9to5:team_percent_online_disables_claims | 1             | The percentage of players that have to be online to disable claims. Setting this to 0 will make claims always disabled! |

## Supported Versions
Currently, the mod is only supported on NeoForge 1.21.1, but it likely works on other MC versions.
I will port it to Fabric if there is a significant demand for that. Just make or find a GH issue and say you're interested!