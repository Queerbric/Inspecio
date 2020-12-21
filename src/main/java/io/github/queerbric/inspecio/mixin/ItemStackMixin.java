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

package io.github.queerbric.inspecio.mixin;

import com.google.common.collect.Lists;
import io.github.queerbric.inspecio.Inspecio;
import io.github.queerbric.inspecio.InspecioConfig;
import io.github.queerbric.inspecio.tooltip.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	@Shadow
	public abstract int getRepairCost();

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
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isDamaged()Z"
			)
	)
	private void onGetTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
		if (!context.isAdvanced())
			return;

		List<Text> tooltip = this.inspecio$tooltipList.get();

		int repairCost;
		if ((repairCost = this.getRepairCost()) != 0) {
			tooltip.add(new TranslatableText("inspecio.tooltip.repair_cost", repairCost)
					.formatted(Formatting.GRAY));
		}
	}

	@Inject(method = "getTooltipData", at = @At("RETURN"), cancellable = true)
	private void getTooltipData(CallbackInfoReturnable<Optional<TooltipData>> info) {
		// Data is the plural and datum is the singular actually, but no one cares
		List<TooltipData> datas = Lists.newArrayList();
		info.getReturnValue().ifPresent(datas::add);
		if ((datas.size() > 0 && datas.get(0) instanceof ConvertibleTooltipData)) {
			// We can't wrap arbitrary TooltipDatas until ConvertibleTooltipData is merged
			return;
		}
		InspecioConfig config = Inspecio.get().getConfig();

		ItemStack stack = (ItemStack) (Object) this;
		if (stack.isFood() && config.getFoodConfig().isEnabled()) {
			FoodComponent comp = stack.getItem().getFoodComponent();
			datas.add(new FoodTooltipComponent(comp));
			if (comp.getStatusEffects().size() > 0 && config.getEffectsConfig().hasFood()) {
				datas.add(new StatusEffectTooltipComponent(comp.getStatusEffects()));
			}
		} else if (stack.getItem() instanceof ArmorItem && config.hasArmor()) {
			ArmorItem armor = (ArmorItem) stack.getItem();
			int prot = armor.getMaterial().getProtectionAmount(armor.getSlotType());
			datas.add(new ArmorTooltipComponent(prot));
		}

		if (datas.size() == 1) {
			info.setReturnValue(Optional.of(datas.get(0)));
		} else if (datas.size() > 1) {
			CompoundTooltipComponent comp = new CompoundTooltipComponent();
			for (TooltipData data : datas) {
				comp.addComponent(((ConvertibleTooltipData) data).getComponent());
			}
			info.setReturnValue(Optional.of(comp));
		}
	}
}
