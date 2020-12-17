package io.github.queerbric.inspecio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;

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
	public static final Codec<InspecioConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.list(ContainerConfig.CODEC).fieldOf("containers").forGetter(InspecioConfig::getContainers),
			Codec.BOOL.fieldOf("armor").forGetter(InspecioConfig::hasArmor),
			Codec.BOOL.fieldOf("banner_pattern").forGetter(InspecioConfig::hasBannerPattern)
	).apply(instance, InspecioConfig::new));

	private final List<ContainerConfig> containers;
	private boolean armor;
	private boolean bannerPattern;

	public InspecioConfig(List<ContainerConfig> containers, boolean armor, boolean bannerPattern) {
		this.containers = containers;
		this.armor = armor;
		this.bannerPattern = bannerPattern;
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
}
