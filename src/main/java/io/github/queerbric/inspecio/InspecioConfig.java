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

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
	public static final Codec<InspecioConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.list(ContainerConfig.CODEC).fieldOf("containers").forGetter(InspecioConfig::getContainers),
			Codec.BOOL.fieldOf("armor").forGetter(InspecioConfig::hasArmor),
			Codec.BOOL.fieldOf("banner_pattern").forGetter(InspecioConfig::hasBannerPattern),
			SignTooltipMode.CODEC.fieldOf("sign").forGetter(InspecioConfig::getSignTooltipMode)
	).apply(instance, InspecioConfig::new));
	private static final JsonParser JSON_PARSER = new JsonParser();

	private final List<ContainerConfig> containers;
	private boolean armor;
	private boolean bannerPattern;
	private SignTooltipMode signTooltipMode;

	public InspecioConfig(List<ContainerConfig> containers, boolean armor, boolean bannerPattern, SignTooltipMode signTooltipMode) {
		this.containers = containers;
		this.armor = armor;
		this.bannerPattern = bannerPattern;
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

	public SignTooltipMode getSignTooltipMode() {
		return this.signTooltipMode;
	}

	public void setSignTooltipMode(SignTooltipMode signTooltipMode) {
		this.signTooltipMode = signTooltipMode;
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

	public static InspecioConfig load(Inspecio mod) {
		mod.log("Loading configuration...");

		if (!Files.exists(CONFIG_PATH)) {
			try {
				if (!Files.exists(CONFIG_PATH.getParent()))
					Files.createDirectory(CONFIG_PATH.getParent());
			} catch (IOException e) {
				mod.warn("Could not create missing \"config\" directory.");
				e.printStackTrace();
				return defaultConfig();
			}

			Path defaultPath = FabricLoader.getInstance().getModContainer(Inspecio.NAMESPACE)
					.map(container -> container.getPath("config.json"))
					.orElse(null);

			if (defaultPath == null)
				return defaultConfig();

			try {
				Files.copy(defaultPath, CONFIG_PATH);
			} catch (IOException e) {
				mod.warn("Could not copy default configuration.");
				e.printStackTrace();
				return defaultConfig();
			}
		}

		try {
			DataResult<InspecioConfig> result = CODEC.decode(JsonOps.INSTANCE, JSON_PARSER.parse(Files.newBufferedReader(CONFIG_PATH))).map(Pair::getFirst);
			return result.result().orElseGet(InspecioConfig::defaultConfig);
		} catch (IOException e) {
			mod.warn("Could not load configuration file.");
			e.printStackTrace();
			return defaultConfig();
		}
	}

	public static InspecioConfig defaultConfig() {
		return new InspecioConfig(new ArrayList<>(), true, true, SignTooltipMode.FANCY);
	}
}
