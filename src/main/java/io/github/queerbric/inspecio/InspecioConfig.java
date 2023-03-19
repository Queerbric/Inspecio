/*
 * Copyright (c) 2020 - 2022 LambdAurora <email@lambdaurora.dev>, Emi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.queerbric.inspecio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents the Inspecio configuration.
 * <p>
 * Uses Codec for serialization/deserialization.
 *
 * @author LambdAurora
 * @version 1.8.0
 * @since 1.0.0
 */
// @TODO rework this to be more expandable?
public class InspecioConfig {
	public static final Path CONFIG_PATH = FileSystems.getDefault().getPath("config", "inspecio.json");
	public static final Path CONFIG_BACKUP_PATH = FileSystems.getDefault().getPath("config/backup", "inspecio.json");

	public static final boolean DEFAULT_ARMOR = true;
	public static final boolean DEFAULT_BANNER_PATTERN = true;
	public static final boolean DEFAULT_PAINTING = true;
	public static final JukeboxTooltipMode DEFAULT_JUKEBOX_TOOLTIP_MODE = JukeboxTooltipMode.FANCY;
	public static final SignTooltipMode DEFAULT_SIGN_TOOLTIP_MODE = SignTooltipMode.FANCY;

	public static final Codec<InspecioConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			configEntry("armor", DEFAULT_ARMOR, InspecioConfig::hasArmor),
			configEntry("banner_pattern", DEFAULT_BANNER_PATTERN, InspecioConfig::hasBannerPattern),
			configEntry("painting", DEFAULT_PAINTING, InspecioConfig::hasPainting),
			configEntry(ContainersConfig.CODEC, "containers", ContainersConfig::defaultConfig, InspecioConfig::getContainersConfig),
			configEntry(EffectsConfig.CODEC, "effects", EffectsConfig::defaultConfig, InspecioConfig::getEffectsConfig),
			configEntry(EntitiesConfig.CODEC, "entities", EntitiesConfig::defaultConfig, InspecioConfig::getEntitiesConfig),
			configEntry(FilledMapConfig.CODEC, "filled_map", FilledMapConfig::defaultConfig, InspecioConfig::getFilledMapConfig),
			configEntry(FoodConfig.CODEC, "food", FoodConfig::defaultConfig, InspecioConfig::getFoodConfig),
			configEntry(JukeboxTooltipMode.CODEC, "jukebox", () -> DEFAULT_JUKEBOX_TOOLTIP_MODE, InspecioConfig::getJukeboxTooltipMode),
			configEntry(SignTooltipMode.CODEC, "sign", () -> DEFAULT_SIGN_TOOLTIP_MODE, InspecioConfig::getSignTooltipMode),
			configEntry(AdvancedTooltipsConfig.CODEC, "advanced_tooltips", AdvancedTooltipsConfig::defaultConfig, InspecioConfig::getAdvancedTooltipsConfig)
	).apply(instance, InspecioConfig::new));

	static boolean shouldSaveConfigAfterLoad = false;

	private static <C> RecordCodecBuilder<C, Boolean> configEntry(String path, boolean defaultValue, Function<C, Boolean> getter) {
		String[] parts = path.split("/");
		return Codec.BOOL.fieldOf(parts[parts.length - 1]).orElse(Inspecio.onConfigError(path), defaultValue).forGetter(getter);
	}

	private static <C, E> RecordCodecBuilder<C, E> configEntry(Codec<E> codec, String path, Supplier<E> defaultGetter, Function<C, E> getter) {
		String[] parts = path.split("/");
		return codec.fieldOf(parts[parts.length - 1]).orElseGet(Inspecio.onConfigError(path), defaultGetter).forGetter(getter);
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private boolean armor;
	private boolean bannerPattern;
	private boolean painting;
	private final ContainersConfig containersConfig;
	private final EffectsConfig effectsConfig;
	private final EntitiesConfig entitiesConfig;
	private final FilledMapConfig filledMapConfig;
	private final FoodConfig foodConfig;
	private JukeboxTooltipMode jukeboxTooltipMode;
	private SignTooltipMode signTooltipMode;
	private final AdvancedTooltipsConfig advancedTooltipsConfig;

	public InspecioConfig(boolean armor, boolean bannerPattern, boolean painting,
			ContainersConfig containersConfig,
			EffectsConfig effectsConfig,
			EntitiesConfig entitiesConfig,
			FilledMapConfig filledMapConfig,
			FoodConfig foodConfig,
			JukeboxTooltipMode jukeboxTooltipMode,
			SignTooltipMode signTooltipMode,
			AdvancedTooltipsConfig advancedTooltipsConfig) {
		this.armor = armor;
		this.bannerPattern = bannerPattern;
		this.painting = painting;
		this.containersConfig = containersConfig;
		this.effectsConfig = effectsConfig;
		this.entitiesConfig = entitiesConfig;
		this.filledMapConfig = filledMapConfig;
		this.foodConfig = foodConfig;
		this.jukeboxTooltipMode = jukeboxTooltipMode;
		this.signTooltipMode = signTooltipMode;
		this.advancedTooltipsConfig = advancedTooltipsConfig;
	}

	public boolean hasArmor() {
		return this.armor;
	}

	public void setArmor(boolean armor) {
		this.armor = armor;
	}

	public boolean hasBannerPattern() {
		return this.bannerPattern;
	}

	public void setBannerPattern(boolean bannerPattern) {
		this.bannerPattern = bannerPattern;
	}

	public boolean hasPainting() {
		return this.painting;
	}

	public void setPainting(boolean painting) {
		this.painting = painting;
	}

	public ContainersConfig getContainersConfig() {
		return this.containersConfig;
	}

	public EffectsConfig getEffectsConfig() {
		return this.effectsConfig;
	}

	public EntitiesConfig getEntitiesConfig() {
		return this.entitiesConfig;
	}

	public FilledMapConfig getFilledMapConfig() {
		return this.filledMapConfig;
	}

	public FoodConfig getFoodConfig() {
		return this.foodConfig;
	}

	public JukeboxTooltipMode getJukeboxTooltipMode() {
		return this.jukeboxTooltipMode;
	}

	public void setJukeboxTooltipMode(JukeboxTooltipMode jukeboxTooltipMode) {
		this.jukeboxTooltipMode = jukeboxTooltipMode;
	}

	public SignTooltipMode getSignTooltipMode() {
		return this.signTooltipMode;
	}

	public void setSignTooltipMode(SignTooltipMode signTooltipMode) {
		this.signTooltipMode = signTooltipMode;
	}

	public AdvancedTooltipsConfig getAdvancedTooltipsConfig() {
		return this.advancedTooltipsConfig;
	}

	/**
	 * Saves the configuration to file.
	 *
	 * @return the current configuration
	 */
	public InspecioConfig save() {
		Inspecio.log("Saving configuration...");
		if (!createConfigDirectoryIfNeeded())
			return this;

		var config = CODEC.encode(this, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).result();
		if (config.isEmpty()) {
			Inspecio.warn("Failed to serialize configuration.");
			return this;
		}
		try (var writer = Files.newBufferedWriter(CONFIG_PATH, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			var jsonWriter = GSON.newJsonWriter(writer);
			GSON.toJson(config.get().getAsJsonObject(), jsonWriter);
		} catch (IOException e) {
			Inspecio.warn("Failed to save configuration.", e);
		}
		return this;
	}

	public static class ContainersConfig {
		public static final boolean DEFAULT_CAMPFIRE = true;

		public static final Codec<ContainersConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				configEntry("containers/campfire", DEFAULT_CAMPFIRE, ContainersConfig::isCampfireEnabled),
				configEntry(StorageContainerConfig.CODEC, "containers/storage", StorageContainerConfig::defaultConfig, ContainersConfig::getStorageConfig),
				configEntry(ShulkerBoxConfig.CODEC, "containers/shulker_box", ShulkerBoxConfig::defaultConfig, ContainersConfig::getShulkerBoxConfig),
				configEntry(ChiseledBookshelfConfig.CODEC, "containers/chiseled_bookshelf",
						ChiseledBookshelfConfig::defaultConfig, ContainersConfig::getChiseledBookshelfConfig
				)
		).apply(instance, ContainersConfig::new));

		private boolean campfire;
		private final StorageContainerConfig storageContainerConfig;
		private final ShulkerBoxConfig shulkerBoxConfig;
		private final ChiseledBookshelfConfig chiseledBookshelfConfig;

		public ContainersConfig(boolean campfire, StorageContainerConfig storageContainerConfig,
				ShulkerBoxConfig shulkerBoxConfig, ChiseledBookshelfConfig chiseledBookshelfConfig) {
			this.campfire = campfire;
			this.storageContainerConfig = storageContainerConfig;
			this.shulkerBoxConfig = shulkerBoxConfig;
			this.chiseledBookshelfConfig = chiseledBookshelfConfig;
		}

		public boolean isCampfireEnabled() {
			return this.campfire;
		}

		public void setCampfire(boolean enabled) {
			this.campfire = enabled;
		}

		public StorageContainerConfig getStorageConfig() {
			return this.storageContainerConfig;
		}

		public ShulkerBoxConfig getShulkerBoxConfig() {
			return this.shulkerBoxConfig;
		}

		public ChiseledBookshelfConfig getChiseledBookshelfConfig() {
			return this.chiseledBookshelfConfig;
		}

		public @Nullable StorageContainerConfig forBlock(Block block) {
			InspecioConfig.StorageContainerConfig config = null;
			if (block instanceof ChestBlock
					|| block instanceof BarrelBlock
					|| block instanceof DispenserBlock
					|| block instanceof HopperBlock) config = this.getStorageConfig();
			else if (block instanceof ShulkerBoxBlock) config = this.getShulkerBoxConfig();
			return config;
		}

		public static ContainersConfig defaultConfig() {
			return new ContainersConfig(DEFAULT_CAMPFIRE, StorageContainerConfig.defaultConfig(),
					ShulkerBoxConfig.defaultConfig(), ChiseledBookshelfConfig.defaultConfig());
		}
	}

	public static class StorageContainerConfig {
		public static final boolean DEFAULT_ENABLED = true;
		public static final boolean DEFAULT_COMPACT = false;
		public static final boolean DEFAULT_LOOT_TABLE = true;

		public static final Codec<StorageContainerConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				configEntry("containers/storage/enabled", DEFAULT_ENABLED, StorageContainerConfig::isEnabled),
				configEntry("containers/storage/compact", DEFAULT_COMPACT, StorageContainerConfig::isCompact),
				configEntry("containers/storage/loot_table", DEFAULT_LOOT_TABLE, StorageContainerConfig::hasLootTable)
		).apply(instance, StorageContainerConfig::new));

		private boolean enabled;
		private boolean compact;
		private boolean lootTable;

		public StorageContainerConfig(boolean enabled, boolean compact, boolean lootTable) {
			this.enabled = enabled;
			this.compact = compact;
			this.lootTable = lootTable;
		}

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isCompact() {
			return this.compact;
		}

		public void setCompact(boolean compact) {
			this.compact = compact;
		}

		public boolean hasLootTable() {
			return this.lootTable;
		}

		public void setLootTable(boolean lootTable) {
			this.lootTable = lootTable;
		}

		public static StorageContainerConfig defaultConfig() {
			return new StorageContainerConfig(DEFAULT_ENABLED, DEFAULT_COMPACT, DEFAULT_LOOT_TABLE);
		}
	}

	public static class ShulkerBoxConfig extends StorageContainerConfig {
		public static final boolean DEFAULT_COLOR = true;

		public static final Codec<ShulkerBoxConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				configEntry("containers/shulker_box/enabled", DEFAULT_ENABLED, StorageContainerConfig::isEnabled),
				configEntry("containers/shulker_box/compact", DEFAULT_COMPACT, StorageContainerConfig::isCompact),
				configEntry("containers/shulker_box/loot_table", DEFAULT_LOOT_TABLE, StorageContainerConfig::hasLootTable),
				configEntry("containers/shulker_box/color", DEFAULT_COLOR, ShulkerBoxConfig::hasColor)
		).apply(instance, ShulkerBoxConfig::new));

		private boolean color;

		public ShulkerBoxConfig(boolean enabled, boolean compact, boolean lootTable, boolean color) {
			super(enabled, compact, lootTable);
			this.color = color;
		}

		public boolean hasColor() {
			return this.color;
		}

		public void setColor(boolean color) {
			this.color = color;
		}

		public static ShulkerBoxConfig defaultConfig() {
			return new ShulkerBoxConfig(DEFAULT_ENABLED, DEFAULT_COMPACT, DEFAULT_LOOT_TABLE, DEFAULT_COLOR);
		}
	}

	public static class ChiseledBookshelfConfig extends StorageContainerConfig {
		public static final boolean DEFAULT_BLOCK_RENDER = true;

		public static final Codec<ChiseledBookshelfConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				configEntry("containers/chiseled_bookshelf/enabled", DEFAULT_ENABLED, StorageContainerConfig::isEnabled),
				configEntry("containers/chiseled_bookshelf/compact", DEFAULT_COMPACT, StorageContainerConfig::isCompact),
				configEntry("containers/chiseled_bookshelf/loot_table", DEFAULT_LOOT_TABLE, StorageContainerConfig::hasLootTable),
				configEntry("containers/chiseled_bookshelf/block_render", DEFAULT_BLOCK_RENDER, ChiseledBookshelfConfig::hasBlockRender)
		).apply(instance, ChiseledBookshelfConfig::new));

		private boolean blockRender;

		public ChiseledBookshelfConfig(boolean enabled, boolean compact, boolean lootTable, boolean blockRender) {
			super(enabled, compact, lootTable);
			this.blockRender = blockRender;
		}

		public boolean hasBlockRender() {
			return this.blockRender;
		}

		public void setBlockRender(boolean blockRender) {
			this.blockRender = blockRender;
		}

		public static ChiseledBookshelfConfig defaultConfig() {
			return new ChiseledBookshelfConfig(DEFAULT_ENABLED, DEFAULT_COMPACT, DEFAULT_LOOT_TABLE, DEFAULT_BLOCK_RENDER);
		}
	}

	/**
	 * Represents effects configuration.
	 *
	 * @version 1.1.0
	 * @since 1.0.0
	 */
	public static class EffectsConfig {
		public static boolean DEFAULT_POTIONS = true;
		public static boolean DEFAULT_TIPPED_ARROWS = true;
		public static boolean DEFAULT_SPECTRAL_ARROW = true;
		public static boolean DEFAULT_FOOD = true;
		public static boolean DEFAULT_HIDDEN_MOTION = true;
		public static boolean DEFAULT_BEACON = true;
		public static HiddenEffectMode DEFAULT_HIDDEN_EFFECTS_MODE = HiddenEffectMode.ENCHANTMENT;

		public static final Codec<EffectsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				configEntry("effects/potions", DEFAULT_POTIONS, EffectsConfig::hasPotions),
				configEntry("effects/tipped_arrows", DEFAULT_TIPPED_ARROWS, EffectsConfig::hasTippedArrows),
				configEntry("effects/spectral_arrow", DEFAULT_SPECTRAL_ARROW, EffectsConfig::hasSpectralArrow),
				configEntry("effects/food", DEFAULT_FOOD, EffectsConfig::hasFood),
				configEntry("effects/hidden_motion", DEFAULT_HIDDEN_MOTION, EffectsConfig::hasHiddenMotion),
				configEntry(HiddenEffectMode.CODEC, "effects/hidden_effect_mode", () -> DEFAULT_HIDDEN_EFFECTS_MODE, EffectsConfig::getHiddenEffectMode),
				configEntry("effects/beacon", DEFAULT_BEACON, EffectsConfig::hasBeacon)
		).apply(instance, EffectsConfig::new));

		private boolean potions;
		private boolean tippedArrows;
		private boolean spectralArrow;
		private boolean food;
		private boolean hiddenMotion;
		private HiddenEffectMode hiddenEffectMode;
		private boolean beacon;

		public EffectsConfig(boolean potions, boolean tippedArrows, boolean spectralArrow, boolean food, boolean hiddenMotion, HiddenEffectMode hiddenEffectMode, boolean beacon) {
			this.potions = potions;
			this.tippedArrows = tippedArrows;
			this.spectralArrow = spectralArrow;
			this.food = food;
			this.hiddenMotion = hiddenMotion;
			this.hiddenEffectMode = hiddenEffectMode;
			this.beacon = beacon;
		}

		public boolean hasPotions() {
			return this.potions;
		}

		public void setPotions(boolean potions) {
			this.potions = potions;
		}

		public boolean hasTippedArrows() {
			return this.tippedArrows;
		}

		public void setTippedArrows(boolean tippedArrows) {
			this.tippedArrows = tippedArrows;
		}

		public boolean hasSpectralArrow() {
			return this.spectralArrow;
		}

		public void setSpectralArrow(boolean spectralArrow) {
			this.spectralArrow = spectralArrow;
		}

		public boolean hasFood() {
			return this.food;
		}

		public void setFood(boolean food) {
			this.food = food;
		}

		public boolean hasHiddenMotion() {
			return this.hiddenMotion;
		}

		public void setHiddenMotion(boolean hiddenMotion) {
			this.hiddenMotion = hiddenMotion;
		}

		public HiddenEffectMode getHiddenEffectMode() {
			return this.hiddenEffectMode;
		}

		public void setHiddenEffectMode(HiddenEffectMode hiddenEffectMode) {
			this.hiddenEffectMode = hiddenEffectMode;
		}

		public boolean hasBeacon() {
			return this.beacon;
		}

		public void setBeacon(boolean beacon) {
			this.beacon = beacon;
		}

		public static EffectsConfig defaultConfig() {
			return new EffectsConfig(DEFAULT_POTIONS, DEFAULT_TIPPED_ARROWS, DEFAULT_SPECTRAL_ARROW, DEFAULT_FOOD, DEFAULT_HIDDEN_MOTION, DEFAULT_HIDDEN_EFFECTS_MODE, DEFAULT_BEACON);
		}
	}

	/**
	 * Represents entities configuration.
	 *
	 * @version 1.6.0
	 * @since 1.0.0
	 */
	public static class EntitiesConfig {
		public static final int DEFAULT_PUFF_STATE = 2;

		public static final Codec<EntitiesConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				configEntry(EntityConfig.CODEC, "entities/armor_stand", EntityConfig::defaultConfig, EntitiesConfig::getArmorStandConfig),
				configEntry(BeeEntityConfig.CODEC, "entities/bee", BeeEntityConfig::defaultConfig, EntitiesConfig::getBeeConfig),
				configEntry(EntityConfig.CODEC, "entities/fish_bucket", EntityConfig::defaultConfig, EntitiesConfig::getFishBucketConfig),
				configEntry(EntityConfig.CODEC, "entities/spawn_egg", EntityConfig::defaultConfig, EntitiesConfig::getSpawnEggConfig),
				configEntry(EntityConfig.CODEC, "entities/mob_spawner", EntityConfig::defaultConfig, EntitiesConfig::getMobSpawnerConfig),
				Codec.INT.fieldOf("pufferfish_puff_state").orElse(DEFAULT_PUFF_STATE)
						.forGetter(EntitiesConfig::getPufferFishPuffState)
		).apply(instance, EntitiesConfig::new));

		private final EntityConfig armorStandConfig;
		private final BeeEntityConfig beeConfig;
		private final EntityConfig fishBucketConfig;
		private final EntityConfig spawnEggConfig;
		private final EntityConfig mobSpawnerConfig;
		private int pufferFishPuffState;

		public EntitiesConfig(EntityConfig armorStandConfig, BeeEntityConfig beeConfig, EntityConfig fishBucketConfig, EntityConfig spawnEggConfig,
				EntityConfig mobSpawnerConfig, int pufferFishPuffState) {
			this.armorStandConfig = armorStandConfig;
			this.beeConfig = beeConfig;
			this.fishBucketConfig = fishBucketConfig;
			this.spawnEggConfig = spawnEggConfig;
			this.mobSpawnerConfig = mobSpawnerConfig;
			this.setPufferFishPuffState(pufferFishPuffState);
		}

		public EntityConfig getArmorStandConfig() {
			return this.armorStandConfig;
		}

		public BeeEntityConfig getBeeConfig() {
			return this.beeConfig;
		}

		public EntityConfig getFishBucketConfig() {
			return this.fishBucketConfig;
		}

		public EntityConfig getSpawnEggConfig() {
			return this.spawnEggConfig;
		}

		public EntityConfig getMobSpawnerConfig() {
			return this.mobSpawnerConfig;
		}

		public int getPufferFishPuffState() {
			return this.pufferFishPuffState;
		}

		public void setPufferFishPuffState(int pufferFishPuffState) {
			this.pufferFishPuffState = MathHelper.clamp(pufferFishPuffState, 0, 2);
		}

		public static EntitiesConfig defaultConfig() {
			return new EntitiesConfig(EntityConfig.defaultConfig(), BeeEntityConfig.defaultConfig(), EntityConfig.defaultConfig(), EntityConfig.defaultConfig(),
					EntityConfig.defaultConfig(), DEFAULT_PUFF_STATE);
		}
	}

	/**
	 * Represents entity configuration.
	 *
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static class EntityConfig {
		public static final boolean DEFAULT_ENABLED = true;
		public static final boolean DEFAULT_ALWAYS_SHOW_NAME = false;
		public static final boolean DEFAULT_SPIN = true;

		public static final Codec<EntityConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.fieldOf("enabled").orElse(DEFAULT_ENABLED).forGetter(EntityConfig::isEnabled),
				Codec.BOOL.fieldOf("always_show_name").orElse(DEFAULT_ALWAYS_SHOW_NAME)
						.forGetter(EntityConfig::shouldAlwaysShowName),
				Codec.BOOL.fieldOf("spin").orElse(DEFAULT_SPIN).forGetter(EntityConfig::shouldSpin)
		).apply(instance, EntityConfig::new));

		private boolean enabled;
		private boolean alwaysShowName;
		private boolean spin;

		public EntityConfig(boolean enabled, boolean alwaysShowName, boolean spin) {
			this.enabled = enabled;
			this.alwaysShowName = alwaysShowName;
			this.spin = spin;
		}

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean shouldAlwaysShowName() {
			return this.alwaysShowName;
		}

		public void setAlwaysShowName(boolean alwaysShowName) {
			this.alwaysShowName = alwaysShowName;
		}

		public boolean shouldSpin() {
			return this.spin;
		}

		public void setSpin(boolean spin) {
			this.spin = spin;
		}

		public static EntityConfig defaultConfig() {
			return new EntityConfig(DEFAULT_ENABLED, DEFAULT_ALWAYS_SHOW_NAME, DEFAULT_SPIN);
		}
	}

	/**
	 * Represents the configuration of tooltips relating to bee hives and bee nests.
	 *
	 * @version 1.6.0
	 * @since 1.6.0
	 */
	public static class BeeEntityConfig extends EntityConfig {
		public static final boolean DEFAULT_SHOW_HONEY_LEVEL = true;

		public static final Codec<BeeEntityConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.fieldOf("enabled").orElse(DEFAULT_ENABLED).forGetter(EntityConfig::isEnabled),
				Codec.BOOL.fieldOf("always_show_name").orElse(DEFAULT_ALWAYS_SHOW_NAME)
						.forGetter(EntityConfig::shouldAlwaysShowName),
				Codec.BOOL.fieldOf("spin").orElse(DEFAULT_SPIN).forGetter(EntityConfig::shouldSpin),
				Codec.BOOL.fieldOf("show_honey_level").orElse(DEFAULT_SHOW_HONEY_LEVEL).forGetter(BeeEntityConfig::shouldShowHoney)
		).apply(instance, BeeEntityConfig::new));

		private boolean showHoneyLevel;

		public BeeEntityConfig(boolean enabled, boolean alwaysShowName, boolean spin, boolean showHoneyLevel) {
			super(enabled, alwaysShowName, spin);
			this.showHoneyLevel = showHoneyLevel;
		}

		public boolean shouldShowHoney() {
			return this.showHoneyLevel;
		}

		public void setShowHoneyLevel(boolean showHoneyLevel) {
			this.showHoneyLevel = showHoneyLevel;
		}

		public static BeeEntityConfig defaultConfig() {
			return new BeeEntityConfig(DEFAULT_ENABLED, DEFAULT_ALWAYS_SHOW_NAME, DEFAULT_SPIN, DEFAULT_SHOW_HONEY_LEVEL);
		}
	}

	/**
	 * Represents filled map configuration.
	 *
	 * @version 1.0.1
	 * @since 1.0.1
	 */
	public static class FilledMapConfig {
		public static final boolean DEFAULT_ENABLED = true;
		public static final boolean DEFAULT_SHOW_PLAYER_ICON = false;

		public static final Codec<FilledMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				configEntry("filled_map/enabled", DEFAULT_ENABLED, FilledMapConfig::isEnabled),
				configEntry("filled_map/show_player_icon", DEFAULT_SHOW_PLAYER_ICON, FilledMapConfig::shouldShowPlayerIcon)
		).apply(instance, FilledMapConfig::new));

		private boolean enabled;
		private boolean showPlayerIcon;

		public FilledMapConfig(boolean enabled, boolean showPlayerIcon) {
			this.enabled = enabled;
			this.showPlayerIcon = showPlayerIcon;
		}

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean shouldShowPlayerIcon() {
			return this.showPlayerIcon;
		}

		public void setShowPlayerIcon(boolean showPlayerIcon) {
			this.showPlayerIcon = showPlayerIcon;
		}

		public static FilledMapConfig defaultConfig() {
			return new FilledMapConfig(DEFAULT_ENABLED, DEFAULT_SHOW_PLAYER_ICON);
		}
	}

	/**
	 * Represents food configuration.
	 *
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static class FoodConfig {
		public static final boolean DEFAULT_HUNGER = true;
		public static final SaturationTooltipMode DEFAULT_SATURATION_TOOLTIP_MODE = SaturationTooltipMode.MERGED;

		public static final Codec<FoodConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				configEntry("food/hunger", DEFAULT_HUNGER, FoodConfig::hasHunger),
				configEntry(SaturationTooltipMode.CODEC, "food/saturation", () -> DEFAULT_SATURATION_TOOLTIP_MODE,
						FoodConfig::getSaturationMode)
		).apply(instance, FoodConfig::new));

		private boolean hunger;
		private SaturationTooltipMode saturationMode;

		public FoodConfig(boolean hunger, SaturationTooltipMode saturationMode) {
			this.hunger = hunger;
			this.saturationMode = saturationMode;
		}

		public boolean hasHunger() {
			return this.hunger;
		}

		public void setHunger(boolean hunger) {
			this.hunger = hunger;
		}

		public SaturationTooltipMode getSaturationMode() {
			return this.saturationMode;
		}

		public void setSaturationMode(SaturationTooltipMode saturationMode) {
			this.saturationMode = saturationMode;
		}

		public boolean isEnabled() {
			return this.hunger || this.saturationMode.isEnabled();
		}

		public static FoodConfig defaultConfig() {
			return new FoodConfig(DEFAULT_HUNGER, DEFAULT_SATURATION_TOOLTIP_MODE);
		}
	}

	public static class AdvancedTooltipsConfig {
		public static boolean DEFAULT_REPAIR_COST = true;
		public static boolean DEFAULT_LODESTONE_COORDS = false;

		public static final Codec<AdvancedTooltipsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				configEntry("advanced_tooltips/repair_cost", DEFAULT_REPAIR_COST, AdvancedTooltipsConfig::hasRepairCost),
				configEntry("advanced_tooltips/lodestone_coords", DEFAULT_LODESTONE_COORDS, AdvancedTooltipsConfig::hasLodestoneCoords)
		).apply(instance, AdvancedTooltipsConfig::new));

		private boolean repairCost;
		private boolean lodestoneCoords;

		public AdvancedTooltipsConfig(boolean repairCost, boolean lodestoneCoords) {
			this.repairCost = repairCost;
			this.lodestoneCoords = lodestoneCoords;
		}

		public boolean hasRepairCost() {
			return this.repairCost;
		}

		public void setRepairCost(boolean repairCost) {
			this.repairCost = repairCost;
		}

		public boolean hasLodestoneCoords() {
			return this.lodestoneCoords;
		}

		public void setLodestoneCoords(boolean lodestoneCoords) {
			this.lodestoneCoords = lodestoneCoords;
		}

		public static AdvancedTooltipsConfig defaultConfig() {
			return new AdvancedTooltipsConfig(DEFAULT_REPAIR_COST, DEFAULT_LODESTONE_COORDS);
		}
	}

	private static boolean createConfigDirectoryIfNeeded() {
		try {
			if (!Files.exists(CONFIG_PATH.getParent()))
				Files.createDirectory(CONFIG_PATH.getParent());
			return true;
		} catch (IOException e) {
			Inspecio.warn("Could not create missing \"config\" directory.", e);
			return false;
		}
	}

	private static boolean createConfigBackupDirectoryIfNeeded() {
		try {
			if (!Files.exists(CONFIG_BACKUP_PATH.getParent()))
				Files.createDirectory(CONFIG_BACKUP_PATH.getParent());
			return true;
		} catch (IOException e) {
			Inspecio.warn("Could not create missing \"config/backup\" directory.", e);
			return false;
		}
	}

	private static InspecioConfig backupAndRestore(InspecioConfig config) {
		try {
			if (createConfigBackupDirectoryIfNeeded())
				Files.copy(CONFIG_PATH, CONFIG_BACKUP_PATH, StandardCopyOption.REPLACE_EXISTING);

			config.save();
		} catch (IOException e) {
			Inspecio.warn("Could not backup existing configuration.", e);
		}

		return config;
	}

	public static InspecioConfig load() {
		Inspecio.log("Loading configuration...");

		if (!Files.exists(CONFIG_PATH)) {
			if (!createConfigDirectoryIfNeeded())
				return defaultConfig();

			return defaultConfig().save();
		}

		try (var reader = Files.newBufferedReader(CONFIG_PATH)) {
			var result = CODEC.decode(JsonOps.INSTANCE, JsonParser.parseReader(reader)).map(Pair::getFirst);

			var config = result.result().orElseGet(() -> {
				Inspecio.warn("Could not load configuration, using default configuration instead.");
				shouldSaveConfigAfterLoad = false;
				return backupAndRestore(defaultConfig());
			});

			if (shouldSaveConfigAfterLoad) {
				backupAndRestore(config);

				shouldSaveConfigAfterLoad = false;
			}

			return config;
		} catch (IOException e) {
			Inspecio.warn("Could not load configuration file.", e);
			return backupAndRestore(defaultConfig());
		}
	}

	/**
	 * Returns the default Inspecio configuration.
	 *
	 * @return the default configuration
	 */
	public static InspecioConfig defaultConfig() {
		return new InspecioConfig(
				DEFAULT_ARMOR, DEFAULT_BANNER_PATTERN, DEFAULT_PAINTING,
				ContainersConfig.defaultConfig(),
				EffectsConfig.defaultConfig(),
				EntitiesConfig.defaultConfig(),
				FilledMapConfig.defaultConfig(),
				FoodConfig.defaultConfig(),
				DEFAULT_JUKEBOX_TOOLTIP_MODE,
				DEFAULT_SIGN_TOOLTIP_MODE,
				AdvancedTooltipsConfig.defaultConfig()
		);
	}
}
