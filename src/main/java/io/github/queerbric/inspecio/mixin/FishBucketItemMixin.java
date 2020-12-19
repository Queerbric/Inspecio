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

import io.github.queerbric.inspecio.tooltip.FishTooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.EntityType;
import net.minecraft.item.FishBucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(FishBucketItem.class)
public abstract class FishBucketItemMixin extends Item {
	@Shadow
	@Final
	private EntityType<?> fishType;

	public FishBucketItemMixin(Settings settings) {
		super(settings);
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		Optional<TooltipData> data = FishTooltipComponent.of(this.fishType, stack.getOrCreateTag());
		if (data.isPresent()) return data;
		return super.getTooltipData(stack);
	}
}
