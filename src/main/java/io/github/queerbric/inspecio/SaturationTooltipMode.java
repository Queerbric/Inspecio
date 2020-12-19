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
 * Represents the different tooltip modes for saturation.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public enum SaturationTooltipMode {
	DISABLED,
	MERGED,
	SEPARATED;

	public static final PrimitiveCodec<SaturationTooltipMode> CODEC = new PrimitiveCodec<SaturationTooltipMode>() {
		@Override
		public <T> DataResult<SaturationTooltipMode> read(final DynamicOps<T> ops, final T input) {
			return ops.getStringValue(input).map(id -> byId(id).orElse(DISABLED));
		}

		@Override
		public <T> T write(final DynamicOps<T> ops, final SaturationTooltipMode value) {
			return ops.createString(value.getName());
		}

		@Override
		public String toString() {
			return "SaturationTooltipMode";
		}
	};

	public boolean isEnabled() {
		return this != DISABLED;
	}

	/**
	 * Returns the next saturation tooltip mode available.
	 *
	 * @return The next available saturation tooltip mode.
	 */
	public SaturationTooltipMode next() {
		SaturationTooltipMode[] v = values();
		if (v.length == this.ordinal() + 1)
			return v[0];
		return v[this.ordinal() + 1];
	}

	public @NotNull String getName() {
		return this.name().toLowerCase();
	}

	/**
	 * Gets the saturation tooltip mode from its identifier.
	 *
	 * @param id The identifier of the saturation tooltip mode.
	 * @return The saturation tooltip mode if found, else empty.
	 */
	public static @NotNull Optional<SaturationTooltipMode> byId(@NotNull String id) {
		return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
	}
}
