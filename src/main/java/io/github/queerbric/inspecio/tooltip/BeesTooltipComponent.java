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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Represents a tooltip component which displays bees from a beehive.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class BeesTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final List<Bee> bees = new ArrayList<>();

	public BeesTooltipComponent(ListTag bees) {
		bees.stream().map(tag -> (CompoundTag) tag).forEach(tag -> {
			CompoundTag bee = tag.getCompound("EntityData");
			bee.remove("UUID");
			bee.remove("Passengers");
			bee.remove("Leash");
			Entity entity = EntityType.loadEntityWithPassengers(bee, client.world, Function.identity());
			if (entity != null) {
				this.bees.add(new Bee(tag.getInt("TicksInHive"), entity));
			}
		});
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return this.bees.isEmpty() ? 0 : (this.shouldRenderCustomNames() ? 28 : 20);
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return this.bees.size() * 24;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
		MinecraftClient client = MinecraftClient.getInstance();
		float tickDelta = client.getTickDelta();
		matrices.push();
		matrices.translate(0, 0, z);
		int xOffset = x;
		for (Bee bee : this.bees) {
			this.renderBee(matrices, xOffset, y + (this.shouldRenderCustomNames() ? 8 : 0), bee, tickDelta);
			xOffset += 26;
		}
		matrices.pop();
	}

	private void renderBee(MatrixStack matrices, int x, int y, Bee bee, float tickDelta) {
		float size = 22;
		if (Math.max(bee.bee.getWidth(), bee.bee.getHeight()) > 1.0) {
			size /= Math.max(bee.bee.getWidth(), bee.bee.getHeight());
		}
		DiffuseLighting.disableGuiDepthLighting();
		matrices.push();
		matrices.translate(x + 10, y + 16, 1050.0);
		matrices.scale(1f, 1f, -1);
		matrices.translate(0.0D, 0.0D, 1000.0D);
		matrices.scale(size, size, size);
		Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
		Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(-10.f);
		quaternion.hamiltonProduct(quaternion2);
		matrices.multiply(quaternion);
		bee.bee.yaw = 180.0F * 40.0F;
		bee.bee.pitch = 0.f;
		if (bee.bee instanceof LivingEntity) {
			((LivingEntity) bee.bee).bodyYaw = (float) (((System.currentTimeMillis() / 10) + bee.ticksInHive) % 360);
		}
		EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
		quaternion2.conjugate();
		entityRenderDispatcher.setRotation(quaternion2);
		entityRenderDispatcher.setRenderShadows(false);
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		bee.bee.age = this.client.player.age + (bee.ticksInHive);
		bee.bee.setCustomNameVisible(bee.bee.hasCustomName() && Screen.hasControlDown());
		entityRenderDispatcher.render(bee.bee, 0, 0, 0, 0.f, 1.f, matrices, immediate, 15728880);
		immediate.draw();
		entityRenderDispatcher.setRenderShadows(true);
		matrices.pop();
		DiffuseLighting.enableGuiDepthLighting();
	}

	private boolean shouldRenderCustomNames() {
		return this.bees.stream().map(bee -> bee.bee.hasCustomName()).reduce(false, (first, second) -> first || second) && Screen.hasControlDown();
	}

	static class Bee {
		final int ticksInHive;
		final Entity bee;

		Bee(int ticksInHive, Entity bee) {
			this.ticksInHive = ticksInHive;
			this.bee = bee;
		}
	}
}
