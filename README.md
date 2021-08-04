# Inspecio
<!-- modrinth_exclude.start -->
![Java 16](https://img.shields.io/badge/language-Java%2016-9B599A.svg?style=flat-square) <!-- modrinth_exclude.end -->
[![GitHub license](https://img.shields.io/github/license/Queerbric/Inspecio?style=flat-square)](https://raw.githubusercontent.com/Queerbric/Inspecio/1.17/LICENSE)
![Environment: Client](https://img.shields.io/badge/environment-client-1976d2?style=flat-square)
[![Mod loader: Fabric]][fabric] <!-- modrinth_exclude.start -->
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

#### Potion tooltips

![potion](images/potion.png)
![suspicious stew](images/suspicious_stew.png)

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

Only shows up if Advanced Tooltip is enabled (`F3 + H`).
![lodestone_compass](images/lodestone_compass.png)

#### Repair Cost

Only shows up if Advanced Tooltip is enabled (`F3 + H`).
![repair_cost](images/repair_cost.png)

## Configuration

The configuration file of the mod is located in `<minecraft directory>/config/inspecio.json`.

You can use the command `/inspecio config` to manage configuration.

Here's the default configuration:

```json
{
  "food": {
    "hunger": true,
    "saturation": "merged"
  },
  "jukebox": "fancy",
  "sign": "fancy",
  "entities": {
    "armor_stand": {
      "enabled": true,
      "always_show_name": false,
      "spin": true
    },
    "bee": {
      "enabled": true,
      "always_show_name": false,
      "spin": true
    },
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
    "pufferfish_puff_state": 2
  },
  "filled_map": {
    "enabled": true,
    "show_player_icon": false
  },
  "armor": true,
  "banner_pattern": true,
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
    "potions": true,
    "tipped_arrows": true,
    "spectral_arrow": true,
    "food": true,
    "hidden_motion": true
  }
}
```

Here's a list of each configuration entries and what they do:

 - `armor` (`bool`) - if `true` then displays the armor bar on armor items, else `false`.
 - `banner_pattern` (`bool`) - if `true` then displays the pattern in the tooltip of banner patterns, else `false`.
 - `containers`
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

[fabric]: https://fabricmc.net
[Mod loader: Fabric]: https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAAsTAAALEwEAmpwYAAAFHGlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4gPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgNS42LWMxNDIgNzkuMTYwOTI0LCAyMDE3LzA3LzEzLTAxOjA2OjM5ICAgICAgICAiPiA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPiA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyIgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RFdnQ9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZUV2ZW50IyIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgQ0MgMjAxOCAoV2luZG93cykiIHhtcDpDcmVhdGVEYXRlPSIyMDE4LTEyLTE2VDE2OjU0OjE3LTA4OjAwIiB4bXA6TW9kaWZ5RGF0ZT0iMjAxOS0wNy0yOFQyMToxNzo0OC0wNzowMCIgeG1wOk1ldGFkYXRhRGF0ZT0iMjAxOS0wNy0yOFQyMToxNzo0OC0wNzowMCIgZGM6Zm9ybWF0PSJpbWFnZS9wbmciIHBob3Rvc2hvcDpDb2xvck1vZGU9IjMiIHBob3Rvc2hvcDpJQ0NQcm9maWxlPSJzUkdCIElFQzYxOTY2LTIuMSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDowZWRiMWMyYy1mZjhjLWU0NDEtOTMxZi00OTVkNGYxNGM3NjAiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6MGVkYjFjMmMtZmY4Yy1lNDQxLTkzMWYtNDk1ZDRmMTRjNzYwIiB4bXBNTTpPcmlnaW5hbERvY3VtZW50SUQ9InhtcC5kaWQ6MGVkYjFjMmMtZmY4Yy1lNDQxLTkzMWYtNDk1ZDRmMTRjNzYwIj4gPHhtcE1NOkhpc3Rvcnk+IDxyZGY6U2VxPiA8cmRmOmxpIHN0RXZ0OmFjdGlvbj0iY3JlYXRlZCIgc3RFdnQ6aW5zdGFuY2VJRD0ieG1wLmlpZDowZWRiMWMyYy1mZjhjLWU0NDEtOTMxZi00OTVkNGYxNGM3NjAiIHN0RXZ0OndoZW49IjIwMTgtMTItMTZUMTY6NTQ6MTctMDg6MDAiIHN0RXZ0OnNvZnR3YXJlQWdlbnQ9IkFkb2JlIFBob3Rvc2hvcCBDQyAyMDE4IChXaW5kb3dzKSIvPiA8L3JkZjpTZXE+IDwveG1wTU06SGlzdG9yeT4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz4/HiGMAAAAtUlEQVRYw+XXrQqAMBQF4D2P2eBL+QIG8RnEJFaNBjEum+0+zMQLtwwv+wV3ZzhhMDgfJ0wUSinxZUQWgKos1JP/AbD4OneIDyQPwCFniA+EJ4CaXm4TxAXCC0BNHgLhAdAnx9hC8PwGSRtAFVMQjF7cNTWED8B1cgwW20yfJgAvrssAsZ1cB3g/xckAxr6FmCDU5N6f488BrpCQ4rQBJkiMYh4ACmLzwOQF0CExinkCsvw7vgGikl+OotaKRwAAAABJRU5ErkJggg==
