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

package io.github.queerbric.inspecio.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the shulker box tooltip component.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class InventoryTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private final DefaultedList<ItemStack> inventory;
	private final DyeColor color;

	public InventoryTooltipComponent(DefaultedList<ItemStack> inventory, @Nullable DyeColor color) {
		this.inventory = inventory;
		this.color = color;
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return 18 * this.inventory.size() / this.getColumns() + 3;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return this.getColumns() * 18;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int xOffset, int yOffset, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
		int x = 1;
		int y = 1;
		int lines = this.getColumns();

		for (ItemStack stack : this.inventory) {
			this.drawSlot(matrices, x + xOffset - 1, y + yOffset - 1, z, textureManager);
			itemRenderer.renderInGuiWithOverrides(stack, xOffset + x, yOffset + y);
			itemRenderer.renderGuiItemOverlay(textRenderer, stack, xOffset + x, yOffset + y);
			x += 18;
			if (x >= 18 * lines) {
				x = 1;
				y += 18;
			}
		}
	}

	private void drawSlot(MatrixStack matrices, int x, int y, int z, TextureManager textureManager) {
		float[] color = this.color != null ? this.color.getColorComponents() : new float[]{1.f, 1.f, 1.f};
		RenderSystem.color4f(color[0], color[1], color[2], 1.f);
		textureManager.bindTexture(DrawableHelper.STATS_ICON_TEXTURE);
		DrawableHelper.drawTexture(matrices, x, y, z, 0.f, 0.f, 18, 18, 128, 128);
	}

	protected int getColumns() {
		return 9;
	}
}
