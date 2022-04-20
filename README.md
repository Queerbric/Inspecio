# Inspecio
<!-- modrinth_exclude.start -->
![Java 17](https://img.shields.io/badge/language-Java%2017-9B599A.svg?style=flat-square) <!-- modrinth_exclude.end -->
[![GitHub license](https://img.shields.io/github/license/Queerbric/Inspecio?style=flat-square)](https://raw.githubusercontent.com/Queerbric/Inspecio/1.17/LICENSE)
![Environment: Client](https://img.shields.io/badge/environment-client-1976d2?style=flat-square)
[![Mod loader: Quilt]][quilt] <!-- modrinth_exclude.start -->
![Version](https://img.shields.io/github/v/tag/Queerbric/Inspecio?label=version&style=flat-square) <!-- modrinth_exclude.end -->

Better and more tooltips on items!

## What's this mod?

Inspecio adds new tooltips to items like shulker boxes, filled map, fish bucket, armor, food, banner patterns, etc.

Some of those are just replacement to the vanilla "text" tooltip with a more fancy one, others are extra information for the user!

Most of the mod is configurable, some parts can be enabled/disabled to your heart's desire!

## Pictures

#### Armor tooltip

![armor](images/armor.png)

#### Food tooltip

![food](images/golden_carrot.png)

#### Effects tooltips

![potion](images/potion.png)

![suspicious stew](images/suspicious_stew.png)

![beacon](images/beacon.png)

#### Shulker Box tooltips (and other storage blocks)

Normal:

![normal](images/simple_shulker_box_tooltip.png)

Colored:

![colored](images/colored_shulker_box_tooltip.png)

Compact:

![compact](images/compact_shulker_box_tooltip.png)

#### Jukebox tooltip

![jukebox](images/jukebox.png)

#### Loot Table Tooltip

![loot_table](images/loot_table.png)

#### Bee Hive Tooltip

![bees](images/beehive.png)

#### Sign Tooltip

![sign](images/sign.png)

#### Banner Pattern

![banner_pattern](images/banner_pattern.png)

#### Campfire

![campfire](images/campfire.png)

#### Filled Map

![map](images/filled_map.png)

#### Entities

##### Armor Stand

![armor_stand](images/armor_stand.png)

##### Bucket of Fish

![tropical_fish_bucket](images/bucket_of_tropical_fish.png)

##### Bucket of Axolotl

![axolotl](images/bucket_of_axolotl.png)

##### Spawn Eggs

![fox_spawn_egg](images/fox_spawn_egg.png)

#### Lodestone Compass

Only shows up if Advanced Tooltip is enabled (`F3 + H`) and if the config option is enabled.
![lodestone_compass](images/lodestone_compass.png)

#### Repair Cost

Only shows up if Advanced Tooltip is enabled (`F3 + H`) and if the config option is enabled.
![repair_cost](images/repair_cost.png)

## Configuration

The configuration file of the mod is located in `<minecraft directory>/config/inspecio.json`.

You can use the command `/inspecio config` to manage configuration.

Here's the default configuration:

```json
{
  "jukebox": "fancy",
  "sign": "fancy",
  "advanced_tooltips": {
    "repair_cost": true,
    "lodestone_coords": false
  },
  "filled_map": {
    "enabled": true,
    "show_player_icon": false
  },
  "food": {
    "hunger": true,
    "saturation": "merged"
  },
  "containers": {
    "campfire": true,
    "storage": {
      "enabled": true,
      "compact": false,
      "loot_table": true
    },
    "shulker_box": {
      "enabled": true,
      "compact": false,
      "loot_table": true,
      "color": true
    }
  },
  "effects": {
    "food": true,
    "hidden_motion": true,
    "beacon": true,
    "potions": true,
    "tipped_arrows": true,
    "spectral_arrow": true
  },
  "entities": {
    "fish_bucket": {
      "enabled": true,
      "always_show_name": false,
      "spin": true
    },
    "spawn_egg": {
      "enabled": true,
      "always_show_name": false,
      "spin": true
    },
    "pufferfish_puff_state": 2,
    "armor_stand": {
      "enabled": true,
      "always_show_name": false,
      "spin": true
    },
    "bee": {
      "enabled": true,
      "always_show_name": false,
      "spin": true
    }
  },
  "armor": true,
  "banner_pattern": true
}
```

Here's a list of each configuration entries and what they do:

 - `armor` (`bool`) - if `true` then displays the armor bar on armor items, else `false`.
 - `banner_pattern` (`bool`) - if `true` then displays the pattern in the tooltip of banner patterns, else `false`.
 - `advanced_tooltips`
   - `repair_cost` (`bool`) - if `true`, will display the repair cost value if the game has advanced tooltips enabled (F3 + H).
   - `lodestone_coords` (`bool`) - if `true`, will display the lodestone coordinates on lodestone compass if the game has advanced tooltips enabled (F3 + H).
 - `containers`
   - `campfire` (`bool`) - if `true` then displays a special tooltip on campfires which hold custom NBT.
   - `storage`
     - `enabled` (`bool`) - `true` if the inventory of storage items like chests, barrels, etc. should be shown in the tooltip, else `false`.
     - `compact` (`bool`) - `true` if the inventory should be compacted to take as little space as possible, else `false`.
     - `loot_table` (`bool`) - `true` if the loot table identifier should be displayed in the tooltip if specified, else `false`.
   - `shulker_box`
     - `enabled` (`bool`) - `true` if the inventory of shulker boxes should be shown in the tooltip, else `false`.
     - `compact` (`bool`) - `true` if the inventory should be compacted to take as little space as possible, else `false`.
     - `loot_table` (`bool`) - `true` if the loot table identifier should be displayed in the tooltip if specified, else `false`.
     - `color` (`bool`) - `true` if the inventory tooltip should be colored the same as the shulker box, else `false`.
 - `effects`
   - `potions` (`bool`) - if `true` then replaces the effect tooltips with a fancy one on potion items, else `false`.
   - `tipped_arrows` (`bool`) - if `true` then replaces the effect tooltips with a fancy one on tipped arrows, else `false`.
   - `spectral_arrow` (`bool`) - if `true` then replaces the effect tooltips with a fancy one on spectral arrow item, else `false`.
   - `food` (`bool`) - if `true` then adds effect tooltips on food items, else `false`.
   - `hidden_motion` (`bool`) - if `true` then uses obfuscated text for hidden effect tooltips, else `false`.
   - `beacon` (`bool`) - if `true` then add a tooltip with the primary and secondary effects (if they exist), otherwise it does nothing.
 - `entities`
   - `armor_stand`
      - `enabled` (`bool`) - if `true` if armor stand tooltip should be displayed, else `false`.
      - `always_show_name` (`bool`) - if `true` then always show name of armor stand, else `false` and use the CTRL key instead.
      - `spin` (`bool`) - `true` if the armor stand spin in the tooltip, else `false`
   - `bee`
     - `enabled` (`bool`) - if `true` then displays bees if present in the beehive tooltip, else `false`.
     - `always_show_name` (`bool`) - if `true` then always show name of bees, else `false` and use the CTRL key instead.
     - `spin` (`bool`) - `true` if the bees spin in the tooltip, else `false`
   - `fish_bucket`
     - `enabled` (`bool`) - `true` if fish bucket tooltips should display the entity they hold, else `false`.
     - `spin` (`bool`) - `true` if the entity spins in the tooltip, else `false`.
   - `spawn_egg`
     - `enabled` (`bool`) - `true` if spawn egg tooltips should display the entity they hold, else `false`.
     - `always_show_name` (`bool`) - `true` if the name of the hold entity should always be shown, else `false`.
     - `spin` (`bool`) - `true` if the entity spins in the tooltip, else `false`.
   - `pufferfish_puff_state` (`int`) - the pufferfish puff state, between 0 and 2 inclusive.
 - `filled_map`
   - `enabled` (`bool`) - `true` if filled map tooltips should display the map, else `false`.
   - `show_player_icon` (`bool`) - `true` if show the player icon on filled map tooltips, else `false`.
 - `food`
   - `hunger` (`bool`) - `true` if hunger bar should be displayed on food items, else `false`.
   - `saturation` (`string`) - `"disabled"` does nothing, `"merged"` adds the saturation bar as an outline to the hunger bar, `"separated"` adds its own saturation bar.
 - `jukebox` (`string`) - `"disabled"` does nothing, `"fast"` will add the inserted disc name if possible in the tooltip of jukeboxes, `"fancy"` will display the disc item as well.
 - `sign` (`string`) - `"disabled"` does nothing, `"fast"` will add the sign content as text tooltip if possible, `"fancy"` will add a fancy sign tooltip if possible.

[quilt]: https://quiltmc.org
[Mod loader: Quilt]: https://img.shields.io/badge/modloader-Quilt-1976d2?style=flat-square
