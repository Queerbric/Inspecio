package dev.emi.inspecio.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import dev.emi.inspecio.tooltip.MapTooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

@Mixin(FilledMapItem.class)
public abstract class FilledMapItemMixin extends Item {

	public FilledMapItemMixin(Settings settings) {
		super(settings);
	}
	
	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		int i = -1;
		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains("map", 99)) {
			i = tag.getInt("map");
		}
		return Optional.of(new MapTooltipComponent(i));
	}
}
