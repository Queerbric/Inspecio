/*
 * Copyright (c) 2020 - 2022 LambdAurora <email@lambdaurora.dev>, Emi
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
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@ClientOnly
@Mixin(PotionItem.class)
public abstract class PotionItemMixin extends Item {
	@Unique
	private final ThreadLocal<Integer> inspecio$oldTooltipLength = new ThreadLocal<>(); // ThreadLocal as REI workaround

	public PotionItemMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "appendTooltip", at = @At("HEAD"))
	private void onAppendTooltipPre(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
		this.inspecio$oldTooltipLength.set(tooltip.size());
	}

	@Inject(method = "appendTooltip", at = @At("RETURN"))
	private void onAppendTooltipPost(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo info) {
		if (Inspecio.getConfig().getEffectsConfig().hasPotions()) {
			Inspecio.removeVanillaTooltips(tooltip, this.inspecio$oldTooltipLength.get());
		}
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		if (!Inspecio.getConfig().getEffectsConfig().hasPotions()) return super.getTooltipData(stack);
		return Optional.of(new StatusEffectTooltipComponent(PotionUtil.getPotionEffects(stack), 1.f));
	}
}
