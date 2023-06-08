/*
 * Copyright (c) 2020 LambdAurora <email@lambdaurora.dev>, Emi
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

import io.github.queerbric.inspecio.api.InspecioEntrypoint;
import io.github.queerbric.inspecio.api.InventoryProvider;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.random.LegacySimpleRandom;
import net.minecraft.util.random.RandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.tag.api.QuiltTagKey;
import org.quiltmc.qsl.tag.api.TagType;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents the Inspecio mod.
 *
 * @version 1.7.0
 * @since 1.0.0
 */
public class Inspecio implements ClientModInitializer {
	public static final String NAMESPACE = "inspecio";
	private static final Logger LOGGER = LogManager.getLogger(NAMESPACE);
	public static final TagKey<Item> HIDDEN_EFFECTS_TAG = QuiltTagKey.of(
			RegistryKeys.ITEM, new Identifier(NAMESPACE, "hidden_effects"),
			TagType.CLIENT_FALLBACK
	);
	public static final RandomGenerator COMMON_RANDOM = new LegacySimpleRandom(System.currentTimeMillis());
	private static InspecioConfig config = InspecioConfig.defaultConfig();
	private static ModContainer mod;

	@Override
	public void onInitializeClient(ModContainer mod) {
		Inspecio.mod = mod;
		reloadConfig();

		InventoryProvider.register((stack, config) -> {
			if (config != null && config.isEnabled() && stack.getItem() instanceof BlockItem blockItem) {
				DyeColor color = null;
				if (blockItem.getBlock() instanceof ShulkerBoxBlock shulkerBoxBlock && ((InspecioConfig.ShulkerBoxConfig) config).hasColor())
					color = shulkerBoxBlock.getColor();

				var nbt = BlockItem.getBlockEntityNbtFromStack(stack);
				if (nbt == null) return null;

				var inventory = readInventory(nbt, getInvSizeFor(stack));
				if (inventory == null) return null;

				return new InventoryProvider.Context(inventory, color);
			}

			return null;
		});

		var entrypoints = QuiltLoader.getEntrypoints("inspecio", InspecioEntrypoint.class);
		for (var entrypoint : entrypoints) {
			entrypoint.onInspecioInitialized();
		}
	}

	/**
	 * Prints a message to the terminal.
	 *
	 * @param info the message to log
	 */
	public static void log(String info) {
		LOGGER.info("[Inspecio] " + info);
	}

	/**
	 * Prints a warning message to the terminal.
	 *
	 * @param info the message to log
	 */
	public static void warn(String info) {
		LOGGER.warn("[Inspecio] " + info);
	}

	/**
	 * Prints a warning message to the terminal.
	 *
	 * @param info the message to log
	 * @param params parameters to the message.
	 */
	public static void warn(String info, Object... params) {
		LOGGER.warn("[Inspecio] " + info, params);
	}

	/**
	 * Prints a warning message to the terminal.
	 *
	 * @param info the message to log
	 * @param throwable the exception to log, including its stack trace.
	 */
	public static void warn(String info, Throwable throwable) {
		LOGGER.warn("[Inspecio] " + info, throwable);
	}

	public static InspecioConfig getConfig() {
		return config;
	}

	static void reloadConfig() {
		config = InspecioConfig.load();
	}

	static Consumer<String> onConfigError(String path) {
		return error -> {
			InspecioConfig.shouldSaveConfigAfterLoad = true;
			warn("Configuration error at \"" + path + "\", error: " + error);
		};
	}

	static String getVersion() {
		var version = mod.metadata().version().raw();
		if (version.equals("${version}"))
			return "dev";
		return version;
	}

	private static int getInvSizeFor(ItemStack stack) {
		if (stack.getItem() instanceof BlockItem blockItem) {
			var block = blockItem.getBlock();
			if (block instanceof DispenserBlock)
				return 9;
			else if (block instanceof HopperBlock)
				return 5;
			return 27;
		}
		return 0;
	}

	/**
	 * Appends block item tooltips.
	 *
	 * @param stack the stack to add tooltip to
	 * @param block the block
	 * @param tooltip the tooltip
	 */
	public static void appendBlockItemTooltip(ItemStack stack, Block block, List<Text> tooltip) {
		var config = Inspecio.getConfig().getContainersConfig().forBlock(block);
		if (config != null && config.hasLootTable()) {
			var blockEntityNbt = BlockItem.getBlockEntityNbtFromStack(stack);
			if (blockEntityNbt != null && blockEntityNbt.contains("LootTable")) {
				tooltip.add(Text.translatable("inspecio.tooltip.loot_table",
								Text.literal(blockEntityNbt.getString("LootTable"))
										.formatted(Formatting.GOLD))
						.formatted(Formatting.GRAY));
			}
		}
	}

	public static void removeVanillaTooltips(List<Text> tooltips, int fromIndex) {
		if (fromIndex >= tooltips.size()) return;

		int keepIndex = tooltips.indexOf(Text.empty());
		if (keepIndex != -1) {
			// we wanna keep tooltips that come after a line break
			keepIndex++;

			int tooltipsToKeep = tooltips.size() - keepIndex;

			// shift tooltips to keep to the front
			for (int i = 0; i < tooltipsToKeep; i++) {
				tooltips.set(fromIndex + i, tooltips.get(keepIndex + i));
			}

			// don't remove them
			fromIndex += tooltipsToKeep;
		}

		tooltips.subList(fromIndex, tooltips.size()).clear();
	}

	public static @Nullable StatusEffectInstance getRawEffectFromTag(NbtCompound tag, String tagKey) {
		if (tag == null) {
			return null;
		}
		if (tag.contains(tagKey, NbtElement.INT_TYPE)) {
			var effect = StatusEffect.byRawId(tag.getInt(tagKey));
			if (effect != null)
				return new StatusEffectInstance(effect, 200, 0);
		}
		return null;
	}

	/**
	 * Reads the inventory from the given NBT.
	 *
	 * @param nbt the NBT to read
	 * @param size the size of the inventory
	 * @return {@code null} if the inventory is empty, or the inventory otherwise
	 */
	public static @Nullable DefaultedList<ItemStack> readInventory(NbtCompound nbt, int size) {
		var inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
		Inventories.readNbt(nbt, inventory);

		boolean empty = true;
		for (var item : inventory) {
			if (!item.isEmpty()) {
				empty = false;
				break;
			}
		}

		if (empty) {
			return null;
		}

		return inventory;
	}
}
