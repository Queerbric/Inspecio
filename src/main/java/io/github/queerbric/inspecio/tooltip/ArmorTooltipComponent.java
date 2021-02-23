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

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ArmorTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private static final Identifier INSPECIO_ICONS_TEXTURE = new Identifier("inspecio", "textures/gui/inspecio_icons.png");
	private boolean drawSeparate = false; // config scared me
	private final int prot;
	private final float toughness;

	public ArmorTooltipComponent(int prot, float toughness) {
		this.prot = prot;
		this.toughness = toughness;
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return 11 * (drawSeparate ? 2 : 1);
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return Math.max(this.prot / 2 * 9, (int) (this.toughness * 9));
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
		textureManager.bindTexture(INSPECIO_ICONS_TEXTURE);
		for (int i = 0; i < this.prot / 2; i++) {
			DrawableHelper.drawTexture(matrices, x + i * 9, y + 1, 0, 0, 9, 9, 256, 256);
		}
		if (this.prot % 2 == 1) {
			DrawableHelper.drawTexture(matrices, x + this.prot / 2 * 9, y + 1, 9, 0, 9, 9, 256, 256);
		}
		
		if (drawSeparate) {
			for (int i = 0; i < (int) this.toughness / 2; i++) {
				DrawableHelper.drawTexture(matrices, x + i * 9, y + 12, 0, 9, 9, 9, 256, 256);
			}
			if (this.toughness % 2 >= 1) {
				DrawableHelper.drawTexture(matrices, x + (int) (this.toughness / 2) * 9, y + 12, 9, 9, 9, 9, 256, 256);
			}
		} else {
			for (int i = 0; i < toughness / 2f; i++) {
				int width = 9;
				if (toughness / 2f - i < 1f) {
					width = Math.round(width * (toughness / 2f - i));
				}
				DrawableHelper.drawTexture(matrices, x + i * 9, y + 1, 18, 9, width, 9, 256, 256);
			}
		}
	}
}
