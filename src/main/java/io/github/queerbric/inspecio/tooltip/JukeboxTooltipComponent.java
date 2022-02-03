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

import io.github.queerbric.inspecio.Inspecio;
import io.github.queerbric.inspecio.InspecioConfig;
import io.github.queerbric.inspecio.JukeboxTooltipMode;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Matrix4f;

import java.util.Optional;

/**
 * Represents a jukebox tooltip component. Displays the inserted disc description and an inventory slot with the disc in fancy mode.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.0.0
 */
public class JukeboxTooltipComponent extends InventoryTooltipComponent {
	private final InspecioConfig config = Inspecio.get().getConfig();
	private final MusicDiscItem disc;

	public JukeboxTooltipComponent(ItemStack discStack) {
		super(DefaultedList.ofSize(1, discStack), 1, null);
		this.disc = (MusicDiscItem) discStack.getItem();
	}

	public static Optional<TooltipData> of(ItemStack stack) {
		if (!Inspecio.get().getConfig().getJukeboxTooltipMode().isEnabled()) return Optional.empty();
		var nbt = BlockItem.getBlockEntityNbtFromStack(stack);
		if (nbt != null && nbt.contains("RecordItem")) {
			var discStack = ItemStack.fromNbt(nbt.getCompound("RecordItem"));
			if (discStack.getItem() instanceof MusicDiscItem)
				return Optional.of(new JukeboxTooltipComponent(discStack));
		}
		return Optional.empty();
	}

	@Override
	public int getHeight() {
		int height = 10;
		if (this.config.getJukeboxTooltipMode() == JukeboxTooltipMode.FANCY)
			height += 20;
		return height;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return textRenderer.getWidth(this.disc.getDescription());
	}

	@Override
	public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate) {
		textRenderer.draw(this.disc.getDescription(),
				x, y, 11184810, true, matrix4f, immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
		if (this.config.getJukeboxTooltipMode() == JukeboxTooltipMode.FANCY)
			super.drawItems(textRenderer, x, y + 10, matrices, itemRenderer, z);
	}
}
