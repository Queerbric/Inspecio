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

package io.github.queerbric.inspecio.api;

import io.github.queerbric.inspecio.InspecioConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ClientOnly
@ApiStatus.Internal
final class InventoryProviderManager {
	static final Map<Item, InventoryProvider> MAPPED_PROVIDERS = new Object2ObjectOpenHashMap<>();
	static final List<InventoryProvider> PROVIDERS = new ArrayList<>();

	static @Nullable InventoryProvider.Context getInventoryContext(ItemStack stack, @Nullable InspecioConfig.StorageContainerConfig config) {
		// We first search for providers that are specifically mapped to the given item.
		var mappedProvider = MAPPED_PROVIDERS.get(stack.getItem());
		if (mappedProvider != null) {
			InventoryProvider.Context context = mappedProvider.getInventoryContext(stack, config);
			if (context != null) {
				return context;
			}
		}

		// Otherwise, we search for the one who provides the biggest inventory for the given item (most likely to be the most complete one)
		// Note: inventory compacting happens in the inventory tooltip component directly.
		InventoryProvider.Context context = null;
		for (var provider : PROVIDERS) {
			var currentContext = provider.getInventoryContext(stack, config);

			if (currentContext != null) {
				if (context == null || currentContext.inventory().size() > context.inventory().size()) {
					context = currentContext;
				}
			}
		}

		return context;
	}
}
