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

import io.github.queerbric.inspecio.InspecioConfig;
import io.github.queerbric.inspecio.mixin.ItemEntityAccessor;
import io.github.queerbric.inspecio.mixin.WitherEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Vec3f;

/**
 * Represents a tooltip component for entities.
 *
 * @author LambdAurora
 * @version 1.0.3
 * @since 1.0.0
 */
public abstract class EntityTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	protected final MinecraftClient client = MinecraftClient.getInstance();
	protected final InspecioConfig.EntityConfig config;

	protected EntityTooltipComponent(InspecioConfig.EntityConfig config) {
		this.config = config;
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return !this.shouldRender() ? 0 : (this.shouldRenderCustomNames() ? 32 : 24);
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return this.shouldRender() ? 24 : 0;
	}

	protected void renderEntity(MatrixStack matrices, int x, int y, Entity entity, int ageOffset, boolean spin, boolean allowCustomName) {
		this.renderEntity(matrices, x, y, entity, ageOffset, spin, allowCustomName, 180.f);
	}

	protected void renderEntity(MatrixStack matrices, int x, int y, Entity entity, int ageOffset, boolean spin, boolean allowCustomName, float defaultYaw) {
		float size = 24;
		if (Math.max(entity.getWidth(), entity.getHeight()) > 1.0) {
			size /= Math.max(entity.getWidth(), entity.getHeight());
		}
		DiffuseLighting.disableGuiDepthLighting();
		matrices.push();
		int yOffset = 16;
		if (entity instanceof SquidEntity) {
			size = 16;
			yOffset = 2;
		} else if (entity instanceof ItemEntity) {
			size = 48;
			yOffset = 28;
		}
		matrices.translate(x + 10, y + yOffset, 1050);
		matrices.scale(1f, 1f, -1);
		matrices.translate(0, 0, 1000);
		matrices.scale(size, size, size);
		var quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.f);
		var quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(-10.f);
		quaternion.hamiltonProduct(quaternion2);
		matrices.multiply(quaternion);
		this.setupAngles(entity, this.client.player.age, ageOffset, spin, defaultYaw);
		var entityRenderDispatcher = this.client.getEntityRenderDispatcher();
		quaternion2.conjugate();
		entityRenderDispatcher.setRotation(quaternion2);
		entityRenderDispatcher.setRenderShadows(false);
		var immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
		entity.setCustomNameVisible(allowCustomName && entity.hasCustomName() && (this.config.shouldAlwaysShowName() || Screen.hasControlDown()));
		entityRenderDispatcher.render(entity, 0, 0, 0, 0.f, 1.f, matrices, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		immediate.draw();
		entityRenderDispatcher.setRenderShadows(true);
		matrices.pop();
		DiffuseLighting.enableGuiDepthLighting();
	}

	protected void setupAngles(Entity entity, int age, int ageOffset, boolean spin, float defaultYaw) {
		entity.age = age + ageOffset;

		float yaw = spin ? (float) (((System.currentTimeMillis() / 10) + ageOffset) % 360) : defaultYaw;
		entity.setYaw(yaw);
		entity.setHeadYaw(yaw);
		entity.setPitch(0.f);
		if (entity instanceof LivingEntity living) {
			if (living instanceof GoatEntity) living.headYaw = yaw;
			else if (living instanceof WitherEntityAccessor wither) {
				wither.getSideHeadYaws()[0] = wither.getSideHeadYaws()[1] = yaw;
			}
			living.bodyYaw = yaw;
		} else if (entity instanceof ItemEntityAccessor itemEntity) {
			itemEntity.setItemAge(entity.age);
			itemEntity.setUniqueOffset(0.f);
		} else if (entity instanceof EndCrystalEntity endCrystal) {
			endCrystal.endCrystalAge = endCrystal.age;
		}
	}

	protected abstract boolean shouldRender();

	protected abstract boolean shouldRenderCustomNames();

	protected static void adjustEntity(Entity entity, NbtCompound itemNbt, InspecioConfig.EntitiesConfig config) {
		if (entity instanceof Bucketable bucketable) {
			bucketable.copyDataFromNbt(itemNbt);
			if (entity instanceof PufferfishEntity pufferfish) {
				pufferfish.setPuffState(config.getPufferFishPuffState());
			} else if (entity instanceof TropicalFishEntity tropicalFish) {
				if (itemNbt.contains("BucketVariantTag", NbtElement.INT_TYPE)) {
					tropicalFish.setVariant(itemNbt.getInt("BucketVariantTag"));
				}
			}
		}
	}
}
