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
import io.github.queerbric.inspecio.InspecioConfig;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a tooltip component which displays bees from a beehive.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.0.0
 */
public class BeesTooltipComponent extends EntityTooltipComponent {
	private final List<Bee> bees = new ArrayList<>();

	public BeesTooltipComponent(InspecioConfig.EntityConfig config, NbtList bees) {
		super(config);
		bees.stream().map(nbt -> (NbtCompound) nbt).forEach(nbt -> {
			var bee = nbt.getCompound("EntityData");
			bee.remove("UUID");
			bee.remove("Passengers");
			bee.remove("Leash");
			var entity = EntityType.loadEntityWithPassengers(bee, this.client.world, Function.identity());
			if (entity != null) {
				this.bees.add(new Bee(nbt.getInt("TicksInHive"), entity));
			}
		});
	}

	public static Optional<TooltipData> of(ItemStack stack) {
		var config = Inspecio.get().getConfig().getEntitiesConfig().getBeeConfig();
		if (!config.isEnabled())
			return Optional.empty();
		var nbt = BlockItem.getBlockEntityNbtFromStack(stack);
		if (nbt == null || !nbt.contains(BeehiveBlockEntity.BEES_KEY, NbtElement.LIST_TYPE))
			return Optional.empty();
		var bees = nbt.getList(BeehiveBlockEntity.BEES_KEY, NbtElement.COMPOUND_TYPE);
		if (!bees.isEmpty())
			return Optional.of(new BeesTooltipComponent(config, bees));
		return Optional.empty();
	}

	@Override
	public int getHeight() {
		return this.bees.isEmpty() ? 0 : (this.shouldRenderCustomNames() ? 32 : 24);
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return this.bees.size() * 24;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
		matrices.push();
		matrices.translate(2, 4, z);
		int xOffset = x;
		for (var bee : this.bees) {
			this.renderEntity(matrices, xOffset, y + (this.shouldRenderCustomNames() ? 8 : 0), bee.bee(), bee.ticksInHive(),
					this.config.shouldSpin(), true);
			xOffset += 26;
		}
		matrices.pop();
	}

	@Override
	protected boolean shouldRender() {
		return !this.bees.isEmpty();
	}

	@Override
	protected boolean shouldRenderCustomNames() {
		return this.bees.stream().map(bee -> bee.bee().hasCustomName()).reduce(false, (first, second) -> first || second)
				&& (this.config.shouldAlwaysShowName() || Screen.hasControlDown());
	}

	record Bee(int ticksInHive, Entity bee) {
	}
}
