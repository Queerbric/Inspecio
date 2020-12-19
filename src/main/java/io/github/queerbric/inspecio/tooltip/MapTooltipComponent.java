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

import io.github.queerbric.inspecio.Inspecio;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class MapTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	public int map;

	public MapTooltipComponent(int map) {
		this.map = map;
	}

	public static Optional<TooltipData> of(CompoundTag tag) {
		if (!Inspecio.get().getConfig().hasFilledMap()) return Optional.empty();
		int map = -1;
		if (tag != null && tag.contains("map", 99)) {
			map = tag.getInt("map");
		}
		return map == -1 ? Optional.empty() : Optional.of(new MapTooltipComponent(map));
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		if (map == -1) return 0;
		return 128 + 2;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		if (map == -1) return 0;
		return 128;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
		if (map == -1) return;
		MinecraftClient client = MinecraftClient.getInstance();
		VertexConsumerProvider vertices = client.getBufferBuilders().getEntityVertexConsumers();
		MapRenderer map = client.gameRenderer.getMapRenderer();
		MapState state = map.method_32599(this.map);
		if (state == null) return;
		matrices.push();
		matrices.scale(0, 0, 0);
		map.draw(matrices, vertices, this.map, state, false, 13);
		matrices.pop();
		DrawableHelper.drawTexture(matrices, x, y, z, 0, 0, 128, 128, 128, 128);
	}
}
