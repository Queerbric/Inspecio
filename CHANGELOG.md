# Inspecio Changelog

## 1.0.0 - Initial release

 - Added more tooltips!
   - Added armor tooltip.
   - Added banner pattern tooltip.
   - Added beehive tooltip.
   - Added fish bucket tooltip.
   - Added food tooltip (hunger and saturation).
   - Added chest/barrel/hopper/dispenser/dropper/shulker box inventory tooltips.
   - Added Jukebox tooltip.
   - Added filled map tooltip.
   - Added sign tooltip.
   - Added spawn egg entity tooltip.
   - Added status effect tooltips.
   - Added repair cost tooltip.
   - Added loot table tooltip.
 - Added configuration through JSON file.

### 1.0.1

 - Added config error logging.
 - Fixed map tooltip rendering.

### 1.0.2

 - Added the inspecio command to configure the mod.
 - Updated to Java 16

### 1.0.3

 - First stable release.
 - Fixed broken rotations in goat entity tooltip ([#5](https://github.com/Queerbric/Inspecio/issues/5)).

## 1.1.0

 - Added campfire tooltip ([#7](https://github.com/Queerbric/Inspecio/issues/7)).
 - Allow disabling motion for hidden status effect tooltips ([#10](https://github.com/Queerbric/Inspecio/pull/10)).
 - Added Lodestone Compass tooltip, requires advanced context to be enabled ([#15](https://github.com/Queerbric/Inspecio/issues/15)).
 - Added `EntityTag` id handling in spawn egg tooltips ([#17](https://github.com/Queerbric/Inspecio/pull/17)).
 - Added armor stand tooltip ([#19](https://github.com/Queerbric/Inspecio/pull/19)).
 - Fixed shulker box tooltips cannot be disabled ([#11](https://github.com/Queerbric/Inspecio/issues/11)).
 - Fixed sign tooltips not rendering the outlines of glowing text in fast mode ([#12](https://github.com/Queerbric/Inspecio/pull/12)).
 - Fixed tropical fish tooltip ([#14](https://github.com/Queerbric/Inspecio/issues/14)).
 - Fixed missing null-check on spawn egg tooltips ([#18](https://github.com/Queerbric/Inspecio/issues/18)).
 - Fixed crafted chests having NBT in singleplayer ([#20](https://github.com/Queerbric/Inspecio/issues/20)).

## 1.2.0

 - Added configuration settings to disable lodestone coordinates, and repair cost ([#26](https://github.com/Queerbric/Inspecio/issues/26)).
 - Added an interface for custom inventory sizes and deserialization for inventory tooltips ([#34](https://github.com/Queerbric/Inspecio/issues/34)).
 - Added Russian translations ([#45](https://github.com/Queerbric/Inspecio/pull/45)).
 - Fixed OutOfBoundsException in Potion/TippedArrowMixin ([#38](https://github.com/Queerbric/Inspecio/pull/38)).
 - Fixed food effects not displaying when hunger tooltip is disabled ([#42](https://github.com/Queerbric/Inspecio/issues/42)).
 - 1.18 specific changes:
   - Fixed broken tooltips in 1.18 ([#43](https://github.com/Queerbric/Inspecio/issues/43)).
   - Fixed broken loom patterns in 1.18 ([#49](https://github.com/Queerbric/Inspecio/issues/49)).
   - Fixed beacon crashing the game in 1.18 ([#50](https://github.com/Queerbric/Inspecio/issues/50)).

### 1.2.1

 - Added Turkish translations ([#55](https://github.com/Queerbric/Inspecio/pull/55)).
 - Fixed serialization of beacon effect config ([#54](https://github.com/Queerbric/Inspecio/issues/54)).
 - Fixed random crashes with other mods adding new tooltips due to a bad cast ([#56](https://github.com/Queerbric/Inspecio/issues/56)).

## 1.3.0

 - Updated to 1.18.2.
 - Use Quilt Standard Libraries as Fabric API does not offer some necessary APIs for the mod to fully work.

### 1.3.1

 - Updated the bundled Quilt stuff to fix some issues.
 - Last update for Fabric, future releases will require Quilt.

## 1.4.0

 - Updated to Quilt Loader.
   - Now only run with Quilt Loader, bundles the necessary QSL modules but to play with other mods you may want Quilted Fabric API.

### 1.4.1

 - Fix a minor issue in the Quilt metadata manifest JSON.

### 1.4.2

 - Updated Simplified Chinese translations ([#63](https://github.com/Queerbric/Inspecio/pull/63)).
 - Updated to Minecraft 1.19.

## 1.5.0

 - Added enchantment style effect for hidden effect ([#89](https://github.com/Queerbric/Inspecio/pull/89)).
 - Removed the need to have advanced tooltips to see Repair Cost ([#81](https://github.com/Queerbric/Inspecio/issues/81)).
 - Updated to Minecraft 1.19.1.
