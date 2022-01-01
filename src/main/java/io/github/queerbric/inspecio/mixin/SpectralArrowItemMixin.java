/*
 * Copyright (c) 2020 - 2022 LambdAurora <aurora42lambda@gmail.com>, Emi
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
import io.github.queerbric.inspecio.tooltip.StatusEffectTooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpectralArrowItem;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.Optional;

@Mixin(SpectralArrowItem.class)
public class SpectralArrowItemMixin extends ArrowItem {
	public SpectralArrowItemMixin(Settings settings) {
		super(settings);
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		if (!Inspecio.get().getConfig().getEffectsConfig().hasSpectralArrow()) return super.getTooltipData(stack);
		return Optional.of(new StatusEffectTooltipComponent(Collections.singletonList(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0)), 1.f));
	}
}
