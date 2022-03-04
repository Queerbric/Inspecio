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

package io.github.queerbric.inspecio.api;

import io.github.queerbric.inspecio.InspecioConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Provides an inventory context for the given item stack.
 *
 * @author LambdAurora
 * @version 1.2.0
 * @since 1.2.0
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface InventoryProvider {
	/**
	 * Returns the inventory context of the given item stack.
	 *
	 * @param stack the item stack
	 * @return {@code null} if no inventory context could be created, otherwise an inventory context
	 */
	@Nullable InventoryProvider.Context getInventoryContext(ItemStack stack, @Nullable InspecioConfig.StorageContainerConfig config);

	static @Nullable InventoryProvider.Context searchInventoryContextOf(ItemStack stack, @Nullable InspecioConfig.StorageContainerConfig config) {
		return InventoryProviderManager.getInventoryContext(stack, config);
	}

	/**
	 * Registers an inventory provider, can optionally be mapped to specific items.
	 *
	 * @param provider the inventory provider to register
	 * @param items if non-empty, the inventory provider will be only registered for those items
	 */
	static void register(InventoryProvider provider, Item... items) {
		if (items.length != 0) {
			for (var item : items) {
				InventoryProviderManager.MAPPED_PROVIDERS.put(item, provider);
			}
		} else {
			InventoryProviderManager.PROVIDERS.add(provider);
		}
	}

	record Context(List<ItemStack> inventory, @Nullable DyeColor color) {
		int getColumns() {
			return this.inventory.size() % 3 == 0 ? this.inventory.size() / 3 : this.inventory.size();
		}
	}
}
