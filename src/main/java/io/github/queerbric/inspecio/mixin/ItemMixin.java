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

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.queerbric.inspecio.tooltip.ArmorTooltipComponent;
import io.github.queerbric.inspecio.tooltip.FoodTooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(Item.class)
public class ItemMixin {
	
	@Inject(at = @At("HEAD"), method = "getTooltipData", cancellable = true)
	public void getTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> info) {
		if (stack.isFood()) {
			info.setReturnValue(Optional.of(new FoodTooltipComponent(stack.getItem().getFoodComponent())));
		} else if (stack.getItem() instanceof ArmorItem) {
			ArmorItem armor = (ArmorItem) stack.getItem();
			int prot = armor.getMaterial().getProtectionAmount(armor.getSlotType());
			info.setReturnValue(Optional.of(new ArmorTooltipComponent(prot)));
		}
	}
}
