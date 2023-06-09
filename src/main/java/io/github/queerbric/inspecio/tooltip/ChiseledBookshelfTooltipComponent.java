/*
 * Copyright (c) 2022 LambdAurora <email@lambdaurora.dev>, Emi
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

import com.mojang.blaze3d.lighting.DiffuseLighting;
import io.github.queerbric.inspecio.Inspecio;
import io.github.queerbric.inspecio.api.InventoryProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.tooltip.api.ConvertibleTooltipData;

import java.util.Optional;

/**
 * Represents the chiseled bookshelf tooltip component.
 *
 * @author LambdAurora
 * @version 1.8.0
 * @since 1.7.0
 */
@ClientOnly
public class ChiseledBookshelfTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private final BlockState state;

	public ChiseledBookshelfTooltipComponent(BlockState state) {
		this.state = state;
	}

	public static Optional<TooltipData> of(ItemStack stack) {
		var config = Inspecio.getConfig().getContainersConfig().getChiseledBookshelfConfig();
		if (!config.isEnabled()) {
			return Optional.empty();
		}

		var nbt = BlockItem.getBlockEntityNbtFromStack(stack);
		if (nbt == null)
			return Optional.empty();

		var inventory = Inspecio.readInventory(nbt, 6);

		if (inventory == null)
			return Optional.empty();

		if (!config.hasBlockRender()) {
			return InventoryTooltipComponent.of(stack, config.isCompact(), new InventoryProvider.Context(inventory, 3));
		}

		var state = Blocks.CHISELED_BOOKSHELF.getDefaultState();
		for (int slot = 0; slot < ChiseledBookshelfBlock.SLOT_OCCUPATION_PROPERTIES.size(); slot++) {
			state = state.with(ChiseledBookshelfBlock.SLOT_OCCUPATION_PROPERTIES.get(slot), !inventory.get(slot).isEmpty());
		}

		return Optional.of(new ChiseledBookshelfTooltipComponent(state));
	}

	@Override
	public TooltipComponent toComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return 24;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 24;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, GuiGraphics graphics) {
		DiffuseLighting.setupInventoryEntityLighting();
		MatrixStack matrices = graphics.getMatrices();
		matrices.translate(x, y, 0);
		matrices.scale(-1, -1, 1);
		matrices.translate(-20, -20, 0);
		matrices.scale(20, 20, 1);
		var vertexConsumer = CLIENT.getBufferBuilders().getEntityVertexConsumers();
		CLIENT.getBlockRenderManager().renderBlockAsEntity(this.state, matrices, vertexConsumer,
				LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV
		);
		vertexConsumer.draw();
		DiffuseLighting.setup3DGuiLighting();
	}
}
