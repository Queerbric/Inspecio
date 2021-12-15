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
import io.github.queerbric.inspecio.Inspecio;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

/**
 * Represents a campfire tooltip. Displays a campfire inventory and the flame if lit.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class CampfireTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private static final Identifier ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");

	private final DefaultedList<ItemStack> inventory;
	private final Identifier fireTexture;

	public CampfireTooltipComponent(DefaultedList<ItemStack> inventory, Identifier fireTexture) {
		this.inventory = inventory;
		this.fireTexture = fireTexture;
	}

	public static Optional<TooltipData> of(ItemStack stack) {
		if (!Inspecio.get().getConfig().getContainersConfig().isCampfireEnabled())
			return Optional.empty();

		var nbt = BlockItem.getBlockEntityNbtFromStack(stack);
		if (nbt == null)
			return Optional.empty();

		var inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
		Inventories.readNbt(nbt, inventory);

		boolean empty = true;
		for (var item : inventory) {
			if (!item.isEmpty()) {
				empty = false;
				break;
			}
		}

		if (empty)
			return Optional.empty();

		var itemId = Registry.ITEM.getId(stack.getItem());
		var fireId = new Identifier(itemId.getNamespace(), "block/" + itemId.getPath() + "_fire");

		var stateNbt = stack.getSubNbt(BlockItem.BLOCK_STATE_TAG_KEY);
		if (stateNbt != null && stateNbt.contains("lit")) {
			if (stateNbt.get("lit").asString().equals("false"))
				fireId = null;
		}

		return Optional.of(new CampfireTooltipComponent(inventory, fireId));
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return 3 * 18 + 2;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 3 * 18 + 2;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int xOffset, int yOffset, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
		int x = 1 + 18 * 2;
		int y = 1 + 18 * 2;

		for (int i = 0; i < this.inventory.size(); i++) {
			var stack = this.inventory.get(i);

			InventoryTooltipComponent.drawSlot(matrices, x + xOffset - 1, y + yOffset - 1, z, null);
			itemRenderer.renderInGuiWithOverrides(stack, xOffset + x, yOffset + y);
			itemRenderer.renderGuiItemOverlay(textRenderer, stack, xOffset + x, yOffset + y);

			if (i == 1)
				y -= 18 * 2;
			else if (i == 0)
				x -= 18 * 2;
			else if (i == 2)
				x += 18 * 2;
		}

		if (this.fireTexture != null) {
			RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);

			var sprite = MinecraftClient.getInstance().getSpriteAtlas(ATLAS_TEXTURE).apply(this.fireTexture);
			if (sprite != null)
				DrawableHelper.drawSprite(matrices, xOffset + 19, yOffset + 19, z, 16, 16, sprite);
		}
	}
}
