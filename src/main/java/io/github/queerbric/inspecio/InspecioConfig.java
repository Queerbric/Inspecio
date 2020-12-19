/*
 * Copyright (c) 2020 LambdAurora <aurora42lambda@gmail.com>, Emi
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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.MathHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the Inspecio configuration.
 * <p>
 * Uses Codec for serialization/deserialization.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class InspecioConfig {
	public static final Path CONFIG_PATH = FileSystems.getDefault().getPath("config", "inspecio.json");

	public static final boolean DEFAULT_ARMOR = true;
	public static final boolean DEFAULT_BANNER_PATTERN = true;
	public static final boolean DEFAULT_FILLED_MAP = true;
	public static final JukeboxTooltipMode DEFAULT_JUKEBOX_TOOLTIP_MODE = JukeboxTooltipMode.FANCY;
	public static final SignTooltipMode DEFAULT_SIGN_TOOLTIP_MODE = SignTooltipMode.FANCY;

	public static final Codec<InspecioConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.list(ContainerConfig.CODEC).fieldOf("containers").orElseGet(ArrayList::new)
					.forGetter(InspecioConfig::getContainers),
			Codec.BOOL.fieldOf("armor").orElse(DEFAULT_ARMOR).forGetter(InspecioConfig::hasArmor),
			Codec.BOOL.fieldOf("banner_pattern").orElse(DEFAULT_BANNER_PATTERN).forGetter(InspecioConfig::hasBannerPattern),
			EffectsConfig.CODEC.fieldOf("effects").orElseGet(EffectsConfig::defaultConfig)
					.forGetter(InspecioConfig::getEffectsConfig),
			EntitiesConfig.CODEC.fieldOf("entities").orElseGet(EntitiesConfig::defaultConfig)
					.forGetter(InspecioConfig::getEntitiesConfig),
			Codec.BOOL.fieldOf("filled_map").orElse(DEFAULT_FILLED_MAP).forGetter(InspecioConfig::hasFilledMap),
			FoodConfig.CODEC.fieldOf("food").orElseGet(FoodConfig::defaultConfig)
					.forGetter(InspecioConfig::getFoodConfig),
			JukeboxTooltipMode.CODEC.fieldOf("jukebox").orElse(DEFAULT_JUKEBOX_TOOLTIP_MODE)
					.forGetter(InspecioConfig::getJukeboxTooltipMode),
			SignTooltipMode.CODEC.fieldOf("sign").orElse(DEFAULT_SIGN_TOOLTIP_MODE)
					.forGetter(InspecioConfig::getSignTooltipMode)
	).apply(instance, InspecioConfig::new));

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final JsonParser JSON_PARSER = new JsonParser();

	private final List<ContainerConfig> containers;
	private boolean armor;
	private boolean bannerPattern;
	private final EffectsConfig effectsConfig;
	private final EntitiesConfig entitiesConfig;
	private boolean filledMap;
	private final FoodConfig foodConfig;
	private JukeboxTooltipMode jukeboxTooltipMode;
	private SignTooltipMode signTooltipMode;

	public InspecioConfig(List<ContainerConfig> containers,
						  boolean armor, boolean bannerPattern,
						  EffectsConfig effectsConfig,
						  EntitiesConfig entitiesConfig,
						  boolean filledMap,
						  FoodConfig foodConfig,
						  JukeboxTooltipMode jukeboxTooltipMode,
						  SignTooltipMode signTooltipMode) {
		this.containers = containers;
		this.armor = armor;
		this.bannerPattern = bannerPattern;
		this.effectsConfig = effectsConfig;
		this.entitiesConfig = entitiesConfig;
		this.filledMap = filledMap;
		this.foodConfig = foodConfig;
		this.jukeboxTooltipMode = jukeboxTooltipMode;
		this.signTooltipMode = signTooltipMode;
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

	public List<ContainerConfig> getContainers() {
		return this.containers;
	}

	public Optional<ContainerConfig> getOptionalContainer(String id) {
		return this.containers.stream().filter(config -> config.getId().equals(id)).findFirst();
	}

	public ContainerConfig getContainer(String id) {
		return this.getOptionalContainer(id).orElseGet(() -> ContainerConfig.of(id));
	}

	public Optional<ContainerConfig> getContainerForItem(BlockItem item) {
		if (item.getBlock() instanceof ShulkerBoxBlock)
			return this.getOptionalContainer("shulker_box");
		else if (item.getBlock() instanceof ChestBlock)
			return this.getOptionalContainer("chest");
		else if (item.getBlock() instanceof BarrelBlock)
			return this.getOptionalContainer("barrel");
		return Optional.empty();
	}

	public EffectsConfig getEffectsConfig() {
		return this.effectsConfig;
	}

	public EntitiesConfig getEntitiesConfig() {
		return this.entitiesConfig;
	}

	public boolean hasFilledMap() {
		return this.filledMap;
	}

	public void setFilledMap(boolean filledMap) {
		this.filledMap = filledMap;
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

	/**
	 * Saves the configuration to file.
	 *
	 * @return the current configuration
	 */
	public InspecioConfig save() {
		Inspecio.get().log("Saving configuration...");
		if (!createConfigDirectoryIfNeeded())
			return this;

		Optional<JsonElement> config = CODEC.encode(this, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).result();
		if (!config.isPresent()) {
			Inspecio.get().warn("Failed to serialize configuration.");
			return this;
		}
		try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH, StandardOpenOption.CREATE)) {
			JsonWriter jsonWriter = GSON.newJsonWriter(writer);
			GSON.toJson(config.get().getAsJsonObject(), jsonWriter);
		} catch (IOException e) {
			Inspecio.get().warn("Failed to save configuration.", e);
		}
		return this;
	}

	public static class ContainerConfig {
		public static final Codec<ContainerConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("id").forGetter(ContainerConfig::getId),
				Codec.BOOL.fieldOf("compact").forGetter(ContainerConfig::isCompact),
				Codec.BOOL.optionalFieldOf("color", false).forGetter(ContainerConfig::isColored),
				Codec.BOOL.optionalFieldOf("loot_table", false).forGetter(ContainerConfig::hasLootTable)
		).apply(instance, ContainerConfig::new));

		private final String id;
		private boolean compact;
		private boolean color;
		private boolean lootTable;

		public ContainerConfig(String id, boolean compact, boolean color, boolean lootTable) {
			this.id = id;
			this.compact = compact;
			this.color = color;
		}

		public String getId() {
			return this.id;
		}

		public boolean isCompact() {
			return this.compact;
		}

		public void setCompact(boolean compact) {
			this.compact = compact;
		}

		public boolean isColored() {
			return this.color;
		}

		public void setColored(boolean color) {
			this.color = color;
		}

		public boolean hasLootTable() {
			return this.lootTable;
		}

		public void setLootTable(boolean lootTable) {
			this.lootTable = lootTable;
		}

		public static ContainerConfig of(String id) {
			boolean shulkerBox = id.equals("shulker_box");
			return new ContainerConfig(id, false, shulkerBox, !shulkerBox);
		}
	}

	/**
	 * Represents effects configuration.
	 *
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static class EffectsConfig {
		public static boolean DEFAULT_POTIONS = true;
		public static boolean DEFAULT_TIPPED_ARROWS = true;
		public static boolean DEFAULT_SPECTRAL_ARROW = true;
		public static boolean DEFAULT_FOOD = true;

		public static final Codec<EffectsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.fieldOf("potions").orElse(DEFAULT_POTIONS).forGetter(EffectsConfig::hasPotions),
				Codec.BOOL.fieldOf("tipped_arrows").orElse(DEFAULT_TIPPED_ARROWS).forGetter(EffectsConfig::hasTippedArrows),
				Codec.BOOL.fieldOf("spectral_arrow").orElse(DEFAULT_SPECTRAL_ARROW).forGetter(EffectsConfig::hasSpectralArrow),
				Codec.BOOL.fieldOf("food").orElse(DEFAULT_FOOD).forGetter(EffectsConfig::hasFood)
		).apply(instance, EffectsConfig::new));

		private boolean potions;
		private boolean tippedArrows;
		private boolean spectralArrow;
		private boolean food;

		public EffectsConfig(boolean potions, boolean tippedArrows, boolean spectralArrow, boolean food) {
			this.potions = potions;
			this.tippedArrows = tippedArrows;
			this.spectralArrow = spectralArrow;
			this.food = food;
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

		public static EffectsConfig defaultConfig() {
			return new EffectsConfig(DEFAULT_POTIONS, DEFAULT_TIPPED_ARROWS, DEFAULT_SPECTRAL_ARROW, DEFAULT_FOOD);
		}
	}

	/**
	 * Represents entities configuration.
	 *
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static class EntitiesConfig {
		public static final int DEFAULT_PUFF_STATE = 2;

		public static final Codec<EntitiesConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityConfig.CODEC.fieldOf("bee").orElseGet(EntityConfig::defaultConfig)
						.forGetter(EntitiesConfig::getBeeConfig),
				EntityConfig.CODEC.fieldOf("fish_bucket").orElseGet(EntityConfig::defaultConfig)
						.forGetter(EntitiesConfig::getFishBucketConfig),
				EntityConfig.CODEC.fieldOf("spawn_egg").orElseGet(EntityConfig::defaultConfig)
						.forGetter(EntitiesConfig::getSpawnEggConfig),
				Codec.INT.fieldOf("pufferfish_puff_state").orElse(DEFAULT_PUFF_STATE)
						.forGetter(EntitiesConfig::getPufferFishPuffState)
		).apply(instance, EntitiesConfig::new));

		private final EntityConfig beeConfig;
		private final EntityConfig fishBucketConfig;
		private final EntityConfig spawnEggConfig;
		private int pufferFishPuffState;

		public EntitiesConfig(EntityConfig beeConfig, EntityConfig fishBucketConfig, EntityConfig spawnEggConfig, int pufferFishPuffState) {
			this.beeConfig = beeConfig;
			this.fishBucketConfig = fishBucketConfig;
			this.spawnEggConfig = spawnEggConfig;
			this.setPufferFishPuffState(pufferFishPuffState);
		}

		public EntityConfig getBeeConfig() {
			return this.beeConfig;
		}

		public EntityConfig getFishBucketConfig() {
			return this.fishBucketConfig;
		}

		public EntityConfig getSpawnEggConfig() {
			return this.spawnEggConfig;
		}

		public int getPufferFishPuffState() {
			return this.pufferFishPuffState;
		}

		public void setPufferFishPuffState(int pufferFishPuffState) {
			this.pufferFishPuffState = MathHelper.clamp(pufferFishPuffState, 0, 2);
		}

		public static EntitiesConfig defaultConfig() {
			return new EntitiesConfig(EntityConfig.defaultConfig(), EntityConfig.defaultConfig(), EntityConfig.defaultConfig(),
					DEFAULT_PUFF_STATE);
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
	 * Represents food configuration.
	 *
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static class FoodConfig {
		public static final boolean DEFAULT_HUNGER = true;
		public static final SaturationTooltipMode DEFAULT_SATURATION_TOOLTIP_MODE = SaturationTooltipMode.MERGED;

		public static final Codec<FoodConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.fieldOf("hunger").orElse(DEFAULT_HUNGER).forGetter(FoodConfig::hasHunger),
				SaturationTooltipMode.CODEC.fieldOf("saturation").orElse(DEFAULT_SATURATION_TOOLTIP_MODE)
						.forGetter(FoodConfig::getSaturationMode)
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

	private static boolean createConfigDirectoryIfNeeded() {
		try {
			if (!Files.exists(CONFIG_PATH.getParent()))
				Files.createDirectory(CONFIG_PATH.getParent());
			return true;
		} catch (IOException e) {
			Inspecio.get().warn("Could not create missing \"config\" directory.", e);
			return false;
		}
	}

	public static InspecioConfig load(Inspecio mod) {
		mod.log("Loading configuration...");

		if (!Files.exists(CONFIG_PATH)) {
			if (!createConfigDirectoryIfNeeded())
				return defaultConfig();

			return defaultConfig().save();
		}

		try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
			DataResult<InspecioConfig> result = CODEC.decode(JsonOps.INSTANCE, JSON_PARSER.parse(reader)).map(Pair::getFirst);
			return result.result().orElseGet(() -> {
				mod.warn("Could not load configuration, using default configuration instead.");
				return defaultConfig();
			});
		} catch (IOException e) {
			mod.warn("Could not load configuration file.");
			e.printStackTrace();
			return defaultConfig();
		}
	}

	/**
	 * Returns the default Inspecio configuration.
	 *
	 * @return the default configuration
	 */
	public static InspecioConfig defaultConfig() {
		return new InspecioConfig(new ArrayList<>(),
				DEFAULT_ARMOR, DEFAULT_BANNER_PATTERN,
				EffectsConfig.defaultConfig(),
				EntitiesConfig.defaultConfig(),
				DEFAULT_FILLED_MAP,
				FoodConfig.defaultConfig(),
				DEFAULT_JUKEBOX_TOOLTIP_MODE,
				DEFAULT_SIGN_TOOLTIP_MODE);
	}
}
