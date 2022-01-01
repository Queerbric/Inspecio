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

package io.github.queerbric.inspecio.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.queerbric.inspecio.Inspecio;
import io.github.queerbric.inspecio.SaturationTooltipMode;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FoodComponent;

public record FoodTooltipComponent(FoodComponent component) implements ConvertibleTooltipData, TooltipComponent {
	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		var foodConfig = Inspecio.get().getConfig().getFoodConfig();

		int height = 11;
		if (foodConfig.hasHunger() && foodConfig.getSaturationMode() == SaturationTooltipMode.SEPARATED)
			height += 11;
		return height;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return Math.max(this.component.getHunger() / 2 * 9, (int) (this.component.getHunger() * this.component.getSaturationModifier() * 9));
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
		var foodConfig = Inspecio.get().getConfig().getFoodConfig();

		RenderSystem.setShaderTexture(0, InGameHud.GUI_ICONS_TEXTURE);
		int saturationY = y;
		if (foodConfig.getSaturationMode() == SaturationTooltipMode.SEPARATED && foodConfig.hasHunger()) saturationY += 11;

		// Draw hunger outline.
		if (foodConfig.hasHunger()) {
			for (int i = 0; i < (this.component.getHunger() + 1) / 2; i++) {
				DrawableHelper.drawTexture(matrices, x + i * 9, y, 16, 27, 9, 9, 256, 256);
			}
		}

		// Draw saturation outline.
		float saturation = this.component.getHunger() * this.component.getSaturationModifier();
		if (foodConfig.getSaturationMode().isEnabled()) {
			RenderSystem.setShaderColor(159 / 255.f, 134 / 255.f, 9 / 255.f, 1.f);
			for (int i = 0; i < saturation; i++) {
				int width = 9;
				if (saturation - i < 1f) {
					width = Math.round(width * (saturation - i));
				}
				DrawableHelper.drawTexture(matrices, x + i * 9, saturationY, 25, 27, width, 9, 256, 256);
			}
		}

		// Draw hunger bars.
		RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
		if (foodConfig.hasHunger()) {
			for (int i = 0; i < this.component.getHunger() / 2; i++) {
				DrawableHelper.drawTexture(matrices, x + i * 9, y, 52, 27, 9, 9, 256, 256);
			}
			if (this.component.getHunger() % 2 == 1) {
				DrawableHelper.drawTexture(matrices, x + this.component.getHunger() / 2 * 9, y, 61, 27, 9, 9, 256, 256);
			}
		}

		// Draw saturation bar if separate (or alone).
		if (foodConfig.getSaturationMode() == SaturationTooltipMode.SEPARATED || !foodConfig.hasHunger()) {
			RenderSystem.setShaderColor(229 / 255.f, 204 / 255.f, 209 / 255.f, 1.f);
			int intSaturation = Math.max(1, this.getSaturation());
			if (saturation * 2 - intSaturation > 0.2)
				intSaturation++;
			for (int i = 0; i < intSaturation / 2; i++) {
				DrawableHelper.drawTexture(matrices, x + i * 9, saturationY, 52, 27, 9, 9, 256, 256);
			}
			if (intSaturation % 2 == 1) {
				DrawableHelper.drawTexture(matrices, x + this.getSaturation() / 2 * 9, saturationY, 61, 27, 9, 9, 256, 256);
			}
			RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
		}
	}

	private int getSaturation() {
		return (int) (this.component.getHunger() * this.component.getSaturationModifier() * 2.f);
	}
}
