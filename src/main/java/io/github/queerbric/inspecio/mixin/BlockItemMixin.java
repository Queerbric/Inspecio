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

import io.github.queerbric.inspecio.tooltip.BeesTooltipComponent;
import io.github.queerbric.inspecio.tooltip.InventoryTooltipComponent;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {
	@Shadow
	public abstract Block getBlock();

	public BlockItemMixin(Settings settings) {
		super(settings);
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		if (this.getBlock() instanceof BeehiveBlock) {
			CompoundTag blockEntityTag = stack.getOrCreateSubTag("BlockEntityTag");
			ListTag bees = blockEntityTag.getList("Bees", 10);
			if (!bees.isEmpty())
				return Optional.of(new BeesTooltipComponent(bees));
		} else if (this.getBlock() instanceof ShulkerBoxBlock) {
			DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
			Inventories.fromTag(stack.getOrCreateSubTag("BlockEntityTag"), inventory);
			return inventory.stream().allMatch(ItemStack::isEmpty) ? Optional.empty()
					: Optional.of(new InventoryTooltipComponent(inventory, ((ShulkerBoxBlock) this.getBlock()).getColor()));
		}
		return super.getTooltipData(stack);
	}

	@Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
	private void onAppendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
		if (this.getBlock() instanceof ShulkerBoxBlock && !Screen.hasControlDown()) {
			ci.cancel();
		}
	}
}
