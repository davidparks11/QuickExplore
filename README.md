# QuickExplore

This is a small Spigot Plugin that teleports players to a random location to defeat an enemy for a reward.
This doesn't use best practices and was my first attempt at a MC plugin.
Configuration options:

-   inform player (bool) - gives the player a little more information
-   max distance (int >= 0) - the maximum distance the player will be teleported in the x and y direction
-   min distance (int >= 0) - the minimum distance the player will be teleported in the x and y direction (setting both min and max to zero will result in no teleportation)
-   quests - strings that govern the available content to the users. They consist of 4 or 6 comma delimited parts.
    "_enemy, time_limit, reward_type, reward_amount, fee_type, fee_amount_". The _fee_type_ and _fee_amount_ options can both be omitted if the quest should require no payment. The plugin will attempt to parse all quest, but will self-disable if it is unable to parse any quests. Both amount values should be positive integers, and quest time should be at least 30 seconds.
-   refund time limit - the amount of time a player has to quit a quest and get a refund.

Unless people actually like this, I probably won't touch it or refactor with best practices.
