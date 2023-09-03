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

package io.github.queerbric.inspecio.mixin;

import io.github.queerbric.inspecio.Inspecio;
import io.github.queerbric.inspecio.InspecioConfig;
import io.github.queerbric.inspecio.tooltip.*;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.tooltip.api.client.TooltipComponentCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ClientOnly
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow
	public abstract int getRepairCost();

	@Shadow
	public abstract Item getItem();

	@Shadow
	@Nullable
	public abstract NbtCompound getNbt();

	@Unique
	private final ThreadLocal<List<Text>> inspecio$tooltipList = new ThreadLocal<>();

	@Inject(
			method = "getTooltip",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasCustomName()Z"),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onGetTooltipBeing(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
		this.inspecio$tooltipList.set(list);
	}

	@Inject(
			method = "getTooltip",
			at = @At(value = "RETURN")
	)
	private void onGetTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
		var tooltip = this.inspecio$tooltipList.get();
		InspecioConfig.AdvancedTooltipsConfig advancedTooltipsConfig = Inspecio.getConfig().getAdvancedTooltipsConfig();

		if (advancedTooltipsConfig.hasLodestoneCoords() && this.getItem() instanceof CompassItem && CompassItem.hasLodestone((ItemStack) (Object) this)) {
			var nbt = this.getNbt();
			assert nbt != null; // Should not be null since hasLodestone returns true.

			GlobalPos globalPos = CompassItem.getLodestonePosition(nbt);

			if (globalPos != null) {
				BlockPos pos = globalPos.getPos();
				var posText = Text.literal(String.format("X: %d, Y: %d, Z: %d", pos.getX(), pos.getY(), pos.getZ()))
						.formatted(Formatting.GOLD);

				tooltip.add(Text.translatable("inspecio.tooltip.lodestone_compass.target", posText).formatted(Formatting.GRAY));
				tooltip.add(Text.translatable("inspecio.tooltip.lodestone_compass.dimension",
								Text.literal(globalPos.getDimension().getValue().toString()).formatted(Formatting.GOLD))
						.formatted(Formatting.GRAY));
			}
		}

		int repairCost;
		if (advancedTooltipsConfig.hasRepairCost() && (repairCost = this.getRepairCost()) != 0) {
			tooltip.add(Text.translatable("inspecio.tooltip.repair_cost", repairCost)
					.formatted(Formatting.GRAY));
		}
	}

	@Inject(method = "getTooltipData", at = @At("RETURN"), cancellable = true)
	private void getTooltipData(CallbackInfoReturnable<Optional<TooltipData>> info) {
		// Data is the plural and datum is the singular actually, but no one cares
		var datas = new ArrayList<TooltipData>();
		info.getReturnValue().ifPresent(datas::add);

		var config = Inspecio.getConfig();
		var stack = (ItemStack) (Object) this;

		if (stack.isFood()) {
			var comp = stack.getItem().getFoodComponent();

			if (config.getFoodConfig().isEnabled()) {
				datas.add(new FoodTooltipComponent(comp));
			}

			if (config.getEffectsConfig().hasFood()) {
				if (stack.isIn(Inspecio.HIDDEN_EFFECTS_TAG)) {
					datas.add(new StatusEffectTooltipComponent());
				} else {
					if (comp.getStatusEffects().size() > 0) {
						datas.add(new StatusEffectTooltipComponent(comp.getStatusEffects()));
					} else if (stack.getItem() instanceof SuspiciousStewItem) {
						var effects = new ArrayList<StatusEffectInstance>();
						SuspiciousStewItemAccessor.invokeConsumeStatusEffects(stack, effects::add);

						if (effects.size() != 0) {
							datas.add(new StatusEffectTooltipComponent(effects, 1.f));
						}
					} else {
						datas.add(new StatusEffectTooltipComponent(PotionUtil.getPotionEffects(stack), 1.f));
					}
				}
			}
		}

		if (stack.getItem() instanceof ArmorItem) {
			ArmorTooltipComponent.of(stack).ifPresent(datas::add);
		}

		if (stack.getItem() instanceof DecorationItem) {
			PaintingTooltipComponent.of(stack).ifPresent(datas::add);
		}

		if (datas.size() == 1) {
			info.setReturnValue(Optional.of(datas.get(0)));
		} else if (datas.size() > 1) {
			var comp = new CompoundTooltipComponent();
			for (var data : datas) {
				TooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(data);
				if (component != null)
					comp.addComponent(component);
			}
			info.setReturnValue(Optional.of(comp));
		}
	}
}
