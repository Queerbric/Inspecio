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

package io.github.queerbric.inspecio;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the different tooltip modes for jukeboxes.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public enum JukeboxTooltipMode {
	DISABLED,
	FAST,
	FANCY;

	public static final PrimitiveCodec<JukeboxTooltipMode> CODEC = new PrimitiveCodec<JukeboxTooltipMode>() {
		@Override
		public <T> DataResult<JukeboxTooltipMode> read(final DynamicOps<T> ops, final T input) {
			return ops.getStringValue(input).map(id -> byId(id).orElse(DISABLED));
		}

		@Override
		public <T> T write(final DynamicOps<T> ops, final JukeboxTooltipMode value) {
			return ops.createString(value.getName());
		}

		@Override
		public String toString() {
			return "JukeboxTooltipMode";
		}
	};

	public boolean isEnabled() {
		return this != DISABLED;
	}

	/**
	 * Returns the next jukebox tooltip mode available.
	 *
	 * @return The next available jukebox tooltip mode.
	 */
	public JukeboxTooltipMode next() {
		JukeboxTooltipMode[] v = values();
		if (v.length == this.ordinal() + 1)
			return v[0];
		return v[this.ordinal() + 1];
	}

	public @NotNull String getName() {
		return this.name().toLowerCase();
	}

	/**
	 * Gets the jukebox tooltip mode from its identifier.
	 *
	 * @param id The identifier of the jukebox tooltip mode.
	 * @return The jukebox tooltip mode if found, else empty.
	 */
	public static @NotNull Optional<JukeboxTooltipMode> byId(@NotNull String id) {
		return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
	}
}
