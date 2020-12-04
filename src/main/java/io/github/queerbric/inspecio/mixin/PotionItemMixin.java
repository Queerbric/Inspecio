package io.github.queerbric.inspecio.mixin;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.queerbric.inspecio.tooltip.StatusEffectTooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.world.World;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin extends Item {
	
	public PotionItemMixin(Settings settings) {
		super(settings);
	}

	@Inject(at = @At("HEAD"), method = "appendTooltip", cancellable = true)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo info) {
		info.cancel();
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return Optional.of(new StatusEffectTooltipComponent(PotionUtil.getPotionEffects(stack)));
	}
}
