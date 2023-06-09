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

import com.google.common.collect.Lists;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.quiltmc.qsl.tooltip.api.ConvertibleTooltipData;

import java.util.List;

public class CompoundTooltipComponent implements TooltipComponent, ConvertibleTooltipData {
	private final List<TooltipComponent> components = Lists.newArrayList();

	public void addComponent(TooltipComponent component) {
		components.add(component);
	}

	@Override
	public TooltipComponent toComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		int height = 0;
		for (var comp : components) {
			height += comp.getHeight();
		}
		return height;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		int width = 0;
		for (var comp : components) {
			if (comp.getWidth(textRenderer) > width) {
				width = comp.getWidth(textRenderer);
			}
		}
		return width;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, GuiGraphics graphics) {
		int yOff = 0;
		for (var comp : components) {
			comp.drawItems(textRenderer, x, y + yOff, graphics);
			yOff += comp.getHeight();
		}
	}

	@Override
	public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, Immediate immediate) {
		int yOff = 0;
		for (var comp : components) {
			comp.drawText(textRenderer, x, y + yOff, matrix4f, immediate);
			yOff += comp.getHeight();
		}
	}
}
