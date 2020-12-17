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

import io.github.queerbric.inspecio.mixin.EntityAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.nbt.CompoundTag;

/**
 * Represents a tooltip component which displays bees from a beehive.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class FishTooltipComponent extends EntityTooltipComponent {
	private final Entity entity;

	public FishTooltipComponent(EntityType<?> type, CompoundTag itemTag) {
		this.entity = type.create(this.client.world);
		if (this.entity != null) {
			EntityType.loadFromEntityTag(this.client.world, null, this.entity, itemTag);
			if (itemTag.contains("BucketVariantTag", 3) && this.entity instanceof TropicalFishEntity) {
				((TropicalFishEntity) this.entity).setVariant(itemTag.getInt("BucketVariantTag"));
			}
		}
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
		if (this.shouldRender()) {
			matrices.push();
			matrices.translate(0, 0, z);
			((EntityAccessor) this.entity).setTouchingWater(true);
			this.entity.setVelocity(1.f, 1.f, 1.f);
			this.renderEntity(matrices, x + 14, y, this.entity, 0, true, false, 90.f);
			matrices.pop();
		}
	}

	@Override
	protected boolean shouldRender() {
		return this.entity != null;
	}

	@Override
	protected boolean shouldRenderCustomNames() {
		return false;
	}
}
