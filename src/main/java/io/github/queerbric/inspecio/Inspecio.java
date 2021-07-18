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

import io.github.queerbric.inspecio.resource.InspecioResourceReloader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents the Inspecio mod.
 *
 * @version 1.0.1
 * @since 1.0.0
 */
public class Inspecio implements ClientModInitializer {
	public static final String NAMESPACE = "inspecio";
	public static final Identifier HIDDEN_EFFECTS_TAG = new Identifier(NAMESPACE, "hidden_effects");
	private static Inspecio INSTANCE;
	private final Logger logger = LogManager.getLogger("inspecio");
	private final InspecioResourceReloader resourceReloader = new InspecioResourceReloader();
	private InspecioConfig config;

	@Override
	public void onInitializeClient() {
		INSTANCE = this;

		this.reloadConfig();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this.resourceReloader);

		InspecioCommand.init();
	}

	/**
	 * Prints a message to the terminal.
	 *
	 * @param info the message to log
	 */
	public void log(String info) {
		this.logger.info("[Inspecio] " + info);
	}

	/**
	 * Prints a warning message to the terminal.
	 *
	 * @param info the message to log
	 */
	public void warn(String info) {
		this.logger.warn("[Inspecio] " + info);
	}

	/**
	 * Prints a warning message to the terminal.
	 *
	 * @param info the message to log
	 * @param params parameters to the message.
	 */
	public void warn(String info, Object... params) {
		this.logger.warn("[Inspecio] " + info, params);
	}

	/**
	 * Prints a warning message to the terminal.
	 *
	 * @param info the message to log
	 * @param throwable the exception to log, including its stack trace.
	 */
	public void warn(String info, Throwable throwable) {
		this.logger.warn("[Inspecio] " + info, throwable);
	}

	public InspecioConfig getConfig() {
		return this.config;
	}

	public void reloadConfig() {
		this.config = InspecioConfig.load(this);
	}

	public static Inspecio get() {
		return INSTANCE;
	}

	static Consumer<String> onConfigError(String path) {
		return error -> get().warn("Configuration error at \"" + path + "\", error: " + error);
	}

	static String getVersion() {
		return FabricLoader.getInstance().getModContainer(NAMESPACE)
				.map(container -> {
					var version = container.getMetadata().getVersion().getFriendlyString();
					if (version.equals("${version}"))
						return "dev";
					return version;
				}).orElse("unknown");
	}

	/**
	 * Appends block item tooltips.
	 *
	 * @param stack the stack to add tooltip to
	 * @param block the block
	 * @param tooltip the tooltip
	 */
	public static void appendBlockItemTooltip(ItemStack stack, Block block, List<Text> tooltip) {
		var config = Inspecio.get().getConfig().getContainersConfig().forBlock(block);
		if (config != null && config.hasLootTable()) {
			var blockEntityNbt = stack.getSubNbt("BlockEntityTag");
			if (blockEntityNbt != null && blockEntityNbt.contains("LootTable")) {
				tooltip.add(new TranslatableText("inspecio.tooltip.loot_table",
						new LiteralText(blockEntityNbt.getString("LootTable"))
								.formatted(Formatting.GOLD))
						.formatted(Formatting.GRAY));
			}
		}
	}

	public static @Nullable Tag<Item> getHiddenEffectsTag() {
		var tag = MinecraftClient.getInstance().world.getTagManager().getOrCreateTagGroup(Registry.ITEM_KEY).getTag(HIDDEN_EFFECTS_TAG);
		if (tag == null) {
			tag = get().resourceReloader.getCurrentGroup().getTag(HIDDEN_EFFECTS_TAG);
		}
		return tag;
	}
}
