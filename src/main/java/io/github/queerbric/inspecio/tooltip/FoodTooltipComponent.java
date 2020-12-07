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
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FoodComponent;

public class FoodTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private final FoodComponent component;

	public FoodTooltipComponent(FoodComponent component) {
		this.component = component;
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return 11;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return Math.max(this.component.getHunger() / 2 * 9, (int) (this.component.getHunger() * this.component.getSaturationModifier() * 9));
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
		textureManager.bindTexture(InGameHud.GUI_ICONS_TEXTURE);
		for (int i = 0; i < (this.component.getHunger() + 1) / 2; i++) {
			DrawableHelper.drawTexture(matrices, x + i * 9, y, 16, 27, 9, 9, 256, 256);
		}
		RenderSystem.color4f(159 / 255.f, 134 / 255.f, 9 / 255.f, 1.f);
		float saturation = this.component.getHunger() * this.component.getSaturationModifier();
		for (int i = 0; i < saturation; i++) {
			int width = 9;
			if (saturation - i < 1f) {
				width = Math.round(width * (saturation - i));
			}
			DrawableHelper.drawTexture(matrices, x + i * 9, y, 25, 27, width, 9, 256, 256);
		}
		RenderSystem.color4f(1.f, 1.f, 1.f, 1.f);
		for (int i = 0; i < this.component.getHunger() / 2; i++) {
			DrawableHelper.drawTexture(matrices, x + i * 9, y, 52, 27, 9, 9, 256, 256);
		}
		if (this.component.getHunger() % 2 == 1) {
			DrawableHelper.drawTexture(matrices, x + this.component.getHunger() / 2 * 9, y, 61, 27, 9, 9, 256, 256);
		}

		/*
		RenderSystem.color4f(159 / 255.f, 134 / 255.f, 9 / 255.f, 1.f);
		for (int i = 0; i <Math.max(1, (this.getSaturation() + 1) / 2); i++) {
			DrawableHelper.drawTexture(matrices, x + i * 9, y + 11, 25, 27, 9, 9, 256, 256);
		}
		RenderSystem.color4f(1.f, 1.f, 1.f, 1.f);
		for (int i = 0; i < this.getSaturation() / 2; i++) {
			DrawableHelper.drawTexture(matrices, x + i * 9, y + 11, 52, 27, 9, 9, 256, 256);
		}
		if (this.getSaturation() % 2 == 1) {
			DrawableHelper.drawTexture(matrices, x + this.getSaturation() / 2 * 9, y + 11, 61, 27, 9, 9, 256, 256);
		}*/
	}

	private int getSaturation() {
		return (int) (this.component.getHunger() * this.component.getSaturationModifier() * 2.f);
	}
}
