package io.github.queerbric.inspecio.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.text.Text;
import net.minecraft.world.World;

@Mixin(LingeringPotionItem.class)
public class LingeringPotionItemMixin {
	
	@Inject(at = @At("HEAD"), method = "appendTooltip", cancellable = true)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo info) {
		info.cancel();
	}
}
