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

package io.github.queerbric.inspecio.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.queerbric.inspecio.api.InventoryProvider;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.tooltip.api.ConvertibleTooltipData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the inventory tooltip component.
 *
 * @author LambdAurora
 * @version 1.8.1
 * @since 1.0.0
 */
public class InventoryTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private final List<ItemStack> inventory;
	private final int columns;
	private final DyeColor color;

	public InventoryTooltipComponent(List<ItemStack> inventory, int columns, @Nullable DyeColor color) {
		this.inventory = inventory;
		this.columns = columns == 0 ? inventory.size() / 3 : columns;
		this.color = color;
	}

	public static Optional<TooltipData> of(ItemStack stack, boolean compact, @Nullable InventoryProvider.Context context) {
		if (context == null) {
			return Optional.empty();
		}

		List<ItemStack> inventory = context.inventory();
		var blockEntityNbt = BlockItem.getBlockEntityNbtFromStack(stack);
		if (blockEntityNbt == null)
			return Optional.empty();

		if (inventory.stream().allMatch(ItemStack::isEmpty))
			return Optional.empty();

		int columns = Math.min(context.columns(), 9);

		if (compact) {
			var compactedInventory = new ArrayList<ItemStack>();
			inventory.forEach(invStack -> {
				if (invStack.isEmpty())
					return;
				compactedInventory.stream().filter(other -> ItemStack.canCombine(other, invStack))
						.findFirst()
						.ifPresentOrElse(
								s -> s.increment(invStack.getCount()),
								() -> compactedInventory.add(invStack)
						);
			});

			inventory = compactedInventory;
			columns = 9;
		}

		return Optional.of(new InventoryTooltipComponent(inventory, columns, context.color()));
	}

	@Override
	public TooltipComponent toComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		int rows = this.inventory.size() / this.getColumns();
		if (this.inventory.size() % this.getColumns() != 0)
			rows++;
		return 18 * rows + 3;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return this.getColumns() * 18;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int xOffset, int yOffset, MatrixStack matrices, ItemRenderer itemRenderer) {
		int x = 1;
		int y = 1;
		int lines = this.getColumns();

		for (var stack : this.inventory) {
			drawSlot(matrices, x + xOffset - 1, y + yOffset - 1, 0, this.color == null ? null : color.getColorComponents());
			itemRenderer.method_4023(matrices, stack, xOffset + x, yOffset + y);
			itemRenderer.method_4025(matrices, textRenderer, stack, xOffset + x, yOffset + y);
			x += 18;
			if (x >= 18 * lines) {
				x = 1;
				y += 18;
			}
		}
	}

	public static void drawSlot(MatrixStack matrices, int x, int y, int z, float[] color) {
		if (color == null)
			color = new float[]{1.f, 1.f, 1.f};
		RenderSystem.setShaderColor(color[0], color[1], color[2], 1.f);
		RenderSystem.setShaderTexture(0, DrawableHelper.STATS_ICON_TEXTURE);
		DrawableHelper.drawTexture(matrices, x, y, z, 0.f, 0.f, 18, 18, 128, 128);
		RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
	}

	protected int getColumns() {
		return this.columns;
	}
}
