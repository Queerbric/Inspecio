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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class ArmorTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private final int prot;

	public ArmorTooltipComponent(int prot) {
		this.prot = prot;
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
		return this.prot / 2 * 9;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
		RenderSystem.setShaderTexture(0, InGameHud.GUI_ICONS_TEXTURE);
		for (int i = 0; i < this.prot / 2; i++) {
			DrawableHelper.drawTexture(matrices, x + i * 9, y, 34, 9, 9, 9, 256, 256);
		}
		if (this.prot % 2 == 1) {
			DrawableHelper.drawTexture(matrices, x + this.prot / 2 * 9, y, 25, 9, 9, 9, 256, 256);
		}
	}
}
