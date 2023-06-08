/*
 * Copyright (c) 2021 LambdAurora <email@lambdaurora.dev>, Emi
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

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.quiltmc.qsl.command.api.client.ClientCommandManager.argument;
import static org.quiltmc.qsl.command.api.client.ClientCommandManager.literal;

public final class InspecioCommand implements ClientCommandRegistrationCallback {
	@Override
	public void registerCommands(CommandDispatcher<QuiltClientCommandSource> dispatcher, CommandBuildContext buildContext,
			CommandManager.RegistrationEnvironment environment) {
		var literalSubCommand = literal("config");

		{
			literalSubCommand.then(literal("reload")
					.executes(ctx -> {
						ctx.getSource().sendFeedback(Text.translatable("inspecio.config.reloading").formatted(Formatting.GREEN));
						Inspecio.reloadConfig();
						return 0;
					})
			).then(literal("armor")
					.executes(onGetter("armor", getter(InspecioConfig::hasArmor)))
					.then(argument("value", BoolArgumentType.bool())
							.executes(onBooleanSetter("armor", setter(InspecioConfig::setArmor))))
			).then(literal("banner_pattern")
					.executes(onGetter("banner_pattern", getter(InspecioConfig::hasBannerPattern)))
					.then(argument("value", BoolArgumentType.bool())
							.executes(onBooleanSetter("armor", setter(InspecioConfig::setBannerPattern))))
			).then(literal("containers")
					.then(literal("campfire")
							.executes(onGetter("containers/campfire", getter(cfg -> cfg.getContainersConfig().isCampfireEnabled())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("containers/campfire", setter((cfg, val) -> cfg.getContainersConfig().setCampfire(val))))))
					.then(initContainer("storage", cfg -> cfg.getContainersConfig().getStorageConfig()))
					.then(initContainer("shulker_box", cfg -> cfg.getContainersConfig().getShulkerBoxConfig())
							.then(literal("color")
									.executes(onGetter("containers/shulker_box/color", getter(cfg -> cfg.getContainersConfig().getShulkerBoxConfig().hasColor())))
									.then(argument("value", BoolArgumentType.bool())
											.executes(onBooleanSetter("containers/shulker_box/color",
													setter((cfg, val) -> cfg.getContainersConfig().getShulkerBoxConfig().setColor(val)))))))
					.then(initContainer("chiseled_bookshelf", cfg -> cfg.getContainersConfig().getChiseledBookshelfConfig())
							.then(literal("block_render")
									.executes(onGetter("containers/chiseled_bookshelf/block_render", getter(cfg -> cfg.getContainersConfig().getChiseledBookshelfConfig().hasBlockRender())))
									.then(argument("value", BoolArgumentType.bool())
											.executes(onBooleanSetter("containers/chiseled_bookshelf/block_render",
													setter((cfg, val) -> cfg.getContainersConfig().getChiseledBookshelfConfig().setBlockRender(val)))))))
			).then(literal("effects")
					.then(literal("potions")
							.executes(onGetter("effects/potions", getter(cfg -> cfg.getEffectsConfig().hasPotions())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("effects/potions", setter((cfg, val) -> cfg.getEffectsConfig().setPotions(val)))))
					).then(literal("tipped_arrows")
							.executes(onGetter("effects/tipped_arrows", getter(cfg -> cfg.getEffectsConfig().hasTippedArrows())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("effects/tipped_arrows", setter((cfg, val) -> cfg.getEffectsConfig().setTippedArrows(val)))))
					).then(literal("spectral_arrow")
							.executes(onGetter("effects/spectral_arrow", getter(cfg -> cfg.getEffectsConfig().hasSpectralArrow())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("effects/spectral_arrow", setter((cfg, val) -> cfg.getEffectsConfig().setSpectralArrow(val)))))
					).then(literal("food")
							.executes(onGetter("effects/food", getter(cfg -> cfg.getEffectsConfig().hasFood())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("effects/food", setter((cfg, val) -> cfg.getEffectsConfig().setFood(val)))))
					).then(literal("hidden_motion")
							.executes(onGetter("effects/hidden_motion", getter(cfg -> cfg.getEffectsConfig().hasHiddenMotion())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("effects/hidden_motion", setter((cfg, val) -> cfg.getEffectsConfig().setHiddenMotion(val)))))
					).then(literal("hidden_effect_mode")
							.executes(onGetter("hidden_effect_mode", getter(cfg -> cfg.getEffectsConfig().getHiddenEffectMode())))
							.then(argument("value", HiddenEffectMode.HiddenEffectType.hiddenEffectMode())
									.executes(InspecioCommand::onSetHiddenEffect))
					).then(literal("beacon")
							.executes(onGetter("effects/beacon", getter(cfg -> cfg.getEffectsConfig().hasHiddenMotion())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("effects/beacon", setter((cfg, val) -> cfg.getEffectsConfig().setBeacon(val)))))
					)
			).then(literal("entities")
					.then(initEntity("armor_stand", cfg -> cfg.getEntitiesConfig().getArmorStandConfig()))
					.then(initEntity("bee", cfg -> cfg.getEntitiesConfig().getBeeConfig())
							.then(literal("show_honey_level")
									.executes(onGetter("entities/bee/show_honey_level",
											() -> Inspecio.getConfig().getEntitiesConfig().getBeeConfig().shouldShowHoney())
									)
									.then(argument("value", BoolArgumentType.bool())
											.executes(onBooleanSetter("entities/bee/show_honey_level",
													val -> Inspecio.getConfig().getEntitiesConfig().getBeeConfig().setShowHoneyLevel(val))))))
					.then(initEntity("fish_bucket", cfg -> cfg.getEntitiesConfig().getFishBucketConfig()))
					.then(initEntity("spawn_egg", cfg -> cfg.getEntitiesConfig().getSpawnEggConfig()))
					.then(literal("pufferfish_puff_state")
							.executes(onGetter("entities/pufferfish_puff_state", getter(cfg -> cfg.getEntitiesConfig().getPufferFishPuffState())))
							.then(argument("value", IntegerArgumentType.integer(0, 2))
									.executes(onIntegerSetter("entities/pufferfish_puff_state", setter((cfg, val) -> cfg.getEntitiesConfig().setPufferFishPuffState(val))))))
			).then(literal("filled_map")
					.executes(onGetter("filled_map", getter(cfg -> cfg.getFilledMapConfig().isEnabled())))
					.then(argument("value", BoolArgumentType.bool())
							.executes(onBooleanSetter("filled_map", setter((cfg, val) -> cfg.getFilledMapConfig().setEnabled(val)))))
					.then(literal("show_player_icon")
							.executes(onGetter("filled_map/show_player_icon", getter(cfg -> cfg.getFilledMapConfig().shouldShowPlayerIcon())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("filled_map/show_player_icon", setter((cfg, val) -> cfg.getFilledMapConfig().setShowPlayerIcon(val))))))
			).then(literal("food")
					.then(literal("hunger")
							.executes(onGetter("food/hunger", getter(cfg -> cfg.getFoodConfig().hasHunger())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("food/hunger", setter((cfg, val) -> cfg.getFoodConfig().setHunger(val))))))
					.then(literal("saturation")
							.executes(onGetter("food/saturation", getter(cfg -> cfg.getFoodConfig().getSaturationMode())))
							.then(argument("value", SaturationTooltipMode.SaturationArgumentType.saturationTooltipMode())
									.executes(InspecioCommand::onSetSaturation)))
			).then(literal("jukebox")
					.executes(onGetter("jukebox", getter(InspecioConfig::getJukeboxTooltipMode)))
					.then(argument("value", JukeboxTooltipMode.JukeboxArgumentType.jukeboxTooltipMode())
							.executes(InspecioCommand::onSetJukebox))
			).then(literal("sign")
					.executes(onGetter("sign", getter(InspecioConfig::getSignTooltipMode)))
					.then(argument("value", SignTooltipMode.SignArgumentType.signTooltipMode())
							.executes(InspecioCommand::onSetSign))
			).then(literal("advanced_tooltips")
					.then(literal("repair_cost")
							.executes(onGetter("advanced_tooltips/repair_cost", getter(cfg -> cfg.getAdvancedTooltipsConfig().hasRepairCost())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("advanced_tooltips/repair_cost", setter((cfg, val) -> cfg.getAdvancedTooltipsConfig().setRepairCost(val))))))
					.then(literal("lodestone_coords")
							.executes(onGetter("advanced_tooltips/lodestone_coords", getter(cfg -> cfg.getAdvancedTooltipsConfig().hasLodestoneCoords())))
							.then(argument("value", BoolArgumentType.bool())
									.executes(onBooleanSetter("advanced_tooltips/lodestone_coords", setter((cfg, val) -> cfg.getAdvancedTooltipsConfig().setLodestoneCoords(val))))))
			);
		}

		dispatcher.register(
				literal("inspecio")
						.executes(onInspecioCommand(literalSubCommand.build()))
						.then(literalSubCommand)
		);
	}

	private static LiteralArgumentBuilder<QuiltClientCommandSource> initContainer(String name,
			Function<InspecioConfig, InspecioConfig.StorageContainerConfig> containerGetter) {
		var prefix = "containers/" + name;
		return literal(name)
				.executes(onGetter(prefix, () -> containerGetter.apply(Inspecio.getConfig()).isEnabled()))
				.then(argument("value", BoolArgumentType.bool())
						.executes(onBooleanSetter(prefix, val -> containerGetter.apply(Inspecio.getConfig()).setEnabled(val))))
				.then(literal("compact")
						.executes(onGetter(prefix + "/compact", () -> containerGetter.apply(Inspecio.getConfig()).isCompact()))
						.then(argument("value", BoolArgumentType.bool())
								.executes(onBooleanSetter(prefix + "/compact", val -> containerGetter.apply(Inspecio.getConfig()).setCompact(val)))))
				.then(literal("loot_table")
						.executes(onGetter(prefix + "/loot_table", () -> containerGetter.apply(Inspecio.getConfig()).hasLootTable()))
						.then(argument("value", BoolArgumentType.bool())
								.executes(onBooleanSetter(prefix + "/loot_table", val -> containerGetter.apply(Inspecio.getConfig()).setLootTable(val)))));
	}

	private static LiteralArgumentBuilder<QuiltClientCommandSource> initEntity(String name,
			Function<InspecioConfig, InspecioConfig.EntityConfig> containerGetter) {
		var prefix = "entities/" + name;
		return literal(name)
				.executes(onGetter(prefix, () -> containerGetter.apply(Inspecio.getConfig()).isEnabled()))
				.then(argument("value", BoolArgumentType.bool())
						.executes(onBooleanSetter(prefix, val -> containerGetter.apply(Inspecio.getConfig()).setEnabled(val))))
				.then(literal("always_show_name")
						.executes(onGetter(prefix + "/always_show_name", () -> containerGetter.apply(Inspecio.getConfig()).shouldAlwaysShowName()))
						.then(argument("value", BoolArgumentType.bool())
								.executes(onBooleanSetter(prefix + "/always_show_name", val -> containerGetter.apply(Inspecio.getConfig()).setAlwaysShowName(val)))))
				.then(literal("spin")
						.executes(onGetter(prefix + "/spin", () -> containerGetter.apply(Inspecio.getConfig()).shouldSpin()))
						.then(argument("value", BoolArgumentType.bool())
								.executes(onBooleanSetter(prefix + "/spin", val -> containerGetter.apply(Inspecio.getConfig()).setSpin(val)))));
	}

	private static Text formatBoolean(boolean bool) {
		return bool ? Text.literal("true").formatted(Formatting.GREEN) : Text.literal("false").formatted(Formatting.RED);
	}

	private static Command<QuiltClientCommandSource> onInspecioCommand(LiteralCommandNode<QuiltClientCommandSource> config) {
		var msg = Text.literal("Inspecio").formatted(Formatting.GOLD)
				.append(Text.literal(" v" + Inspecio.getVersion() + "\n").formatted(Formatting.GRAY));
		buildHelpCommand(config, 0, msg);
		return ctx -> {
			ctx.getSource().sendFeedback(msg);
			return 0;
		};
	}

	private static void buildHelpCommand(LiteralCommandNode<QuiltClientCommandSource> node, int step, MutableText text) {
		text.append(Text.literal('\n' + " ".repeat(step * 2) + "- ").formatted(Formatting.GRAY)
				.append(Text.literal(node.getLiteral()).formatted(Formatting.GOLD)));

		for (var child : node.getChildren()) {
			if (child instanceof LiteralCommandNode) {
				buildHelpCommand((LiteralCommandNode<QuiltClientCommandSource>) child, step + 1, text);
			}
		}
	}

	private static int onSetJukebox(CommandContext<QuiltClientCommandSource> context) {
		var value = JukeboxTooltipMode.JukeboxArgumentType.getJukeboxTooltipMode(context, "value");
		var config = Inspecio.getConfig();
		config.setJukeboxTooltipMode(value);
		config.save();
		context.getSource().sendFeedback(prefix("jukebox").append(Text.literal(value.toString()).formatted(Formatting.WHITE)));
		return 0;
	}

	private static int onSetSaturation(CommandContext<QuiltClientCommandSource> context) {
		var value = SaturationTooltipMode.SaturationArgumentType.getSaturationTooltipMode(context, "value");
		var config = Inspecio.getConfig();
		config.getFoodConfig().setSaturationMode(value);
		config.save();
		context.getSource().sendFeedback(prefix("food/saturation").append(Text.literal(value.toString()).formatted(Formatting.WHITE)));
		return 0;
	}

	private static int onSetSign(CommandContext<QuiltClientCommandSource> context) {
		var value = SignTooltipMode.SignArgumentType.getSignTooltipMode(context, "value");
		var config = Inspecio.getConfig();
		config.setSignTooltipMode(value);
		config.save();
		context.getSource().sendFeedback(prefix("sign").append(Text.literal(value.toString()).formatted(Formatting.WHITE)));
		return 0;
	}

	private static int onSetHiddenEffect(CommandContext<QuiltClientCommandSource> context) {
		var value = HiddenEffectMode.HiddenEffectType.getHiddenEffectMode(context, "value");
		var config = Inspecio.getConfig().getEffectsConfig();
		config.setHiddenEffectMode(value);
		Inspecio.getConfig().save();
		context.getSource().sendFeedback(prefix("effects/hidden_effect_mode").append(Text.literal(value.toString()).formatted(Formatting.WHITE)));
		return 0;
	}

	private static MutableText prefix(String path) {
		return Text.literal(path).formatted(Formatting.GOLD).append(Text.literal(": ").formatted(Formatting.GRAY));
	}

	private static <T> Supplier<T> getter(Function<InspecioConfig, T> func) {
		return () -> func.apply(Inspecio.getConfig());
	}

	private static <T> Consumer<T> setter(BiConsumer<InspecioConfig, T> func) {
		return val -> func.accept(Inspecio.getConfig(), val);
	}

	private static <T> Command<QuiltClientCommandSource> onGetter(String path, Supplier<T> getter) {
		return context -> {
			var value = getter.get();

			Text valueText;

			if (value instanceof Boolean boolValue) valueText = formatBoolean(boolValue);
			else valueText = Text.literal(value.toString()).formatted(Formatting.WHITE);

			context.getSource().sendFeedback(prefix(path).append(valueText));

			return 0;
		};
	}

	private static Command<QuiltClientCommandSource> onBooleanSetter(String path, Consumer<Boolean> setter) {
		return context -> {
			var value = BoolArgumentType.getBool(context, "value");

			setter.accept(value);

			Inspecio.getConfig().save();

			context.getSource().sendFeedback(prefix(path).append(formatBoolean(value)));

			return 0;
		};
	}

	private static Command<QuiltClientCommandSource> onIntegerSetter(String path, Consumer<Integer> setter) {
		return context -> {
			var value = IntegerArgumentType.getInteger(context, "value");

			setter.accept(value);

			Inspecio.getConfig().save();

			context.getSource().sendFeedback(prefix(path).append(Text.literal(String.valueOf(value)).formatted(Formatting.WHITE)));

			return 0;
		};
	}
}
