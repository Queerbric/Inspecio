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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class SpawnEntityTooltipComponent extends EntityTooltipComponent {
	private final Entity entity;

	public SpawnEntityTooltipComponent(EntityType<?> type, CompoundTag itemTag) {
		this.entity = type.create(this.client.world);
		if (this.entity != null) {

			CompoundTag itemEntityTag = itemTag.getCompound("EntityTag").copy();
			if (!itemEntityTag.contains("VillagerData")) {
				CompoundTag villagerData = new CompoundTag();
				villagerData.putString("profession", "minecraft:none");
				villagerData.putInt("level", 1);
				villagerData.putString("type", "minecraft:plains");
				itemEntityTag.put("VillagerData", villagerData);
			}
			CompoundTag entityTag = entity.toTag(new CompoundTag());
			UUID uuid = entity.getUuid();
			entityTag.copyFrom(itemEntityTag);
			entity.setUuid(uuid);
			entity.fromTag(entityTag);
			if (itemTag.contains("BucketVariantTag", 3) && this.entity instanceof TropicalFishEntity) {
				((TropicalFishEntity) this.entity).setVariant(itemTag.getInt("BucketVariantTag"));
			}
		}
	}

	@Override
	public int getHeight() {
		return super.getHeight() + 36;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 60 + 24;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
		if (this.shouldRender()) {
			matrices.push();
			matrices.translate(30, 0, z);
			((EntityAccessor) this.entity).setTouchingWater(true);
			this.entity.setVelocity(1.f, 1.f, 1.f);
			this.renderEntity(matrices, x + 16, y + 20, this.entity, 0, true, false, 90.f);
			matrices.pop();
		}
	}

	@Override
	protected boolean shouldRender() {
		return this.entity != null;
	}

	@Override
	protected boolean shouldRenderCustomNames() {
		return this.entity.hasCustomName() && Screen.hasControlDown();
	}
}
