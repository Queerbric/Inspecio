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
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class MapTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private final MinecraftClient client = MinecraftClient.getInstance();
	public int map;

	public MapTooltipComponent(int map) {
		this.map = map;
	}

	public static Optional<TooltipData> of(ItemStack stack) {
		if (!Inspecio.get().getConfig().getFilledMapConfig().isEnabled()) return Optional.empty();
		var map = FilledMapItem.getMapId(stack);
		return map == null ? Optional.empty() : Optional.of(new MapTooltipComponent(map));
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return 128 + 2;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 128;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
		var vertices = this.client.getBufferBuilders().getEntityVertexConsumers();
		var map = this.client.gameRenderer.getMapRenderer();
		var state = FilledMapItem.getMapState(this.map, this.client.world);
		if (state == null) return;
		matrices.push();
		matrices.translate(x, y, z);
		matrices.scale(1, 1, 0);
		map.draw(matrices, vertices, this.map, state, !Inspecio.get().getConfig().getFilledMapConfig().shouldShowPlayerIcon(),
				LightmapTextureManager.MAX_LIGHT_COORDINATE);
		vertices.draw();
		matrices.pop();
	}
}
