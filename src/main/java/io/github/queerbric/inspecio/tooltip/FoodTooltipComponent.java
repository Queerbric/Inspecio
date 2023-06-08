/*
 * Copyright (c) 2020 LambdAurora <email@lambdaurora.dev>, Emi
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
import net.minecraft.util.math.MathHelper;
import org.quiltmc.qsl.tooltip.api.ConvertibleTooltipData;

public record FoodTooltipComponent(int hunger, float saturation) implements ConvertibleTooltipData, TooltipComponent {
	public FoodTooltipComponent(FoodComponent component) {
		this(component.getHunger(), component.getHunger() * component.getSaturationModifier());
	}

	private static final int COLUMNS = 16;

	@Override
	public TooltipComponent toComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		var foodConfig = Inspecio.getConfig().getFoodConfig();
		int height = Math.max(
				11 * this.getLines(this.getHungerChunks()),
				11 * this.getLines(MathHelper.ceil(this.saturation))
		);

		if (foodConfig.hasHunger() && foodConfig.getSaturationMode() == SaturationTooltipMode.SEPARATED)
			height += 11 * this.getLines(this.getSaturationChunks());

		return height;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return Math.min(
				Math.max(this.hunger / 2 * 9, (int) this.saturation * 9),
				COLUMNS * 9
		);
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer) {
		var foodConfig = Inspecio.getConfig().getFoodConfig();

		RenderSystem.setShaderTexture(0, InGameHud.GUI_ICONS_TEXTURE);
		int saturationY = y;
		if (foodConfig.getSaturationMode() == SaturationTooltipMode.SEPARATED && foodConfig.hasHunger()) {
			saturationY += 11 * this.getLines(this.getHungerChunks());
		}

		var pos = new ChunkPos(x, y);

		// Draw hunger outline.
		if (foodConfig.hasHunger()) {
			for (int i = 0; i < (this.hunger + 1) / 2; i++) {
				pos.wrap(i);
				DrawableHelper.drawTexture(matrices, pos.x, pos.y, 16, 27, 9, 9, 256, 256);
				pos.moveForward();
			}
		}

		// Draw saturation outline.
		if (foodConfig.getSaturationMode().isEnabled()) {
			RenderSystem.setShaderColor(159 / 255.f, 134 / 255.f, 9 / 255.f, 1.f);

			pos.reset(x, saturationY);

			for (int i = 0; i < this.saturation; i++) {
				pos.wrap(i);

				int width = 9;
				if (this.saturation - i < 1f) {
					width = Math.round(width * (saturation - i));
				}
				DrawableHelper.drawTexture(matrices, pos.x, pos.y, 25, 27, width, 9, 256, 256);

				pos.moveForward();
			}
		}

		// Draw hunger bars.
		RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
		if (foodConfig.hasHunger()) {
			pos.reset(x, y);

			for (int i = 0; i < this.hunger / 2; i++) {
				pos.wrap(i);
				DrawableHelper.drawTexture(matrices, pos.x, pos.y, 52, 27, 9, 9, 256, 256);
				pos.moveForward();
			}

			if (this.hunger % 2 == 1) {
				pos.wrap(this.hunger / 2);
				DrawableHelper.drawTexture(matrices, pos.x, pos.y, 61, 27, 9, 9, 256, 256);
			}
		}

		// Draw saturation bar if separate (or alone).
		if (foodConfig.getSaturationMode() == SaturationTooltipMode.SEPARATED || !foodConfig.hasHunger()) {
			RenderSystem.setShaderColor(229 / 255.f, 204 / 255.f, 209 / 255.f, 1.f);

			pos.reset(x, saturationY);

			int intSaturation = Math.max(1, this.getSaturation());
			if (this.saturation * 2 - intSaturation > 0.2)
				intSaturation++;

			for (int i = 0; i < intSaturation / 2; i++) {
				pos.wrap(i);
				DrawableHelper.drawTexture(matrices, pos.x, pos.y, 52, 27, 9, 9, 256, 256);
				pos.moveForward();
			}

			if (intSaturation % 2 == 1) {
				pos.wrap(intSaturation / 2);
				DrawableHelper.drawTexture(matrices, pos.x, pos.y, 61, 27, 9, 9, 256, 256);
			}

			RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
		}
	}

	private int getHungerChunks() {
		return this.hunger / 2 + this.hunger % 2;
	}

	private int getSaturation() {
		return (int) (this.saturation * 2.f);
	}

	private int getSaturationChunks() {
		int intSaturation = this.getSaturation();
		if (this.saturation * 2 - intSaturation > 0.2) {
			return (int) (this.saturation + 1);
		} else {
			return (int) this.saturation;
		}
	}

	private int getLines(int chunks) {
		return chunks / COLUMNS + (chunks % COLUMNS > 0 ? 1 : 0);
	}

	private static class ChunkPos {
		private final int originalX;
		private int x;
		private int y;

		public ChunkPos(int x, int y) {
			this.originalX = this.x = x;
			this.y = y;
		}

		public void moveForward() {
			this.x += 9;
		}

		public void wrap(int progress) {
			if (progress != 0 && progress % COLUMNS == 0) {
				this.x = this.originalX;
				this.y += 11;
			}
		}

		public void reset(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
