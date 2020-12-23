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

package io.github.queerbric.inspecio.resource;

import io.github.queerbric.inspecio.Inspecio;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents a cursed item tag loader to load client-side tags.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class InspecioResourceReloader implements SimpleSynchronousResourceReloadListener {
	private final TagGroupLoader<Item> loader = new TagGroupLoader<>(Registry.ITEM::getOrEmpty, "tags/items");
	private TagGroup<Item> currentGroup;

	@Override
	public Identifier getFabricId() {
		return new Identifier(Inspecio.NAMESPACE, "inspecio");
	}

	@Override
	public void apply(ResourceManager manager) {
		manager = this.getGoodResourceManager(manager);
		Map<Identifier, Tag.Builder> map = this.loader.method_33174(manager);
		this.currentGroup = this.loader.applyReload(map);
	}

	/**
	 * Returns a resource manager (namespaced) with a modified resource type but with the same resource packs as the client.
	 * <p>
	 * Flaw: if a resource pack which can be loaded by client but only has server data won't be added to this resource manager as not present in client resource manager.
	 *
	 * @param base the client vanilla resource manager
	 * @return the modified resource manager
	 */
	private ResourceManager getGoodResourceManager(ResourceManager base) {
		NamespaceResourceManager good = new NamespaceResourceManager(ResourceType.SERVER_DATA, Inspecio.NAMESPACE);
		base.streamResourcePacks()
				.filter(resourcePack -> resourcePack.getNamespaces(ResourceType.SERVER_DATA).contains(Inspecio.NAMESPACE))
				.forEach(good::addPack);
		return good;
	}

	public @Nullable TagGroup<Item> getCurrentGroup() {
		return this.currentGroup;
	}
}
