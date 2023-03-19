/*
 * Copyright (c) 2020 - 2022 LambdAurora <email@lambdaurora.dev>, Emi
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
import io.github.queerbric.inspecio.mixin.DecorationItemAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Holder;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.tooltip.api.ConvertibleTooltipData;

import java.util.Optional;

/**
 * Represents a painting tooltip for painting items with a known variant.
 *
 * @param painting the painting variant
 * @author LambdAurora
 * @version 1.8.0
 * @since 1.8.0
 */
@ClientOnly
public record PaintingTooltipComponent(PaintingVariant painting) implements ConvertibleTooltipData, TooltipComponent {
	public static Optional<TooltipData> of(ItemStack stack) {
		if (!Inspecio.getConfig().hasPainting())
			return Optional.empty();

		NbtCompound nbt = stack.getNbt();

		if (nbt != null
				&& stack.getItem() instanceof DecorationItemAccessor decorationItem
				&& decorationItem.getEntityType() == EntityType.PAINTING
		) {
			var entityNbt = nbt.getCompound("EntityTag");

			if (entityNbt != null) {
				return PaintingEntity.parse(entityNbt)
						.map(Holder::value)
						.map(PaintingTooltipComponent::new);
			}
		}

		return Optional.empty();
	}

	@Override
	public TooltipComponent toComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return this.painting.getHeight();
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return this.painting.getWidth();
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer) {
		PaintingManager paintingManager = MinecraftClient.getInstance().getPaintingManager();
		Sprite sprite = paintingManager.getPaintingSprite(this.painting);
		RenderSystem.setShaderTexture(0, paintingManager.getBackSprite().getId());
		DrawableHelper.drawSprite(matrices, x, y - 2, 0, this.getWidth(textRenderer), this.getHeight(), sprite);
	}
}
