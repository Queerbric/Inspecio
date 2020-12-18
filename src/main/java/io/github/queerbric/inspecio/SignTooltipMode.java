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

public enum SignTooltipMode {
	DISABLED,
	FAST,
	FANCY;

	public static final PrimitiveCodec<SignTooltipMode> CODEC = new PrimitiveCodec<SignTooltipMode>() {
		@Override
		public <T> DataResult<SignTooltipMode> read(final DynamicOps<T> ops, final T input) {
			return ops.getStringValue(input).map(id -> byId(id).orElse(DISABLED));
		}

		@Override
		public <T> T write(final DynamicOps<T> ops, final SignTooltipMode value) {
			return ops.createString(value.getName());
		}

		@Override
		public String toString() {
			return "SignTooltipMode";
		}
	};

	public boolean isEnabled() {
		return this != DISABLED;
	}

	/**
	 * Returns the next sign tooltip mode available.
	 *
	 * @return The next available sign tooltip mode.
	 */
	public SignTooltipMode next() {
		SignTooltipMode[] v = values();
		if (v.length == this.ordinal() + 1)
			return v[0];
		return v[this.ordinal() + 1];
	}

	public @NotNull String getName() {
		return this.name().toLowerCase();
	}

	/**
	 * Gets the sign tooltip mode from its identifier.
	 *
	 * @param id The identifier of the sign tooltip mode.
	 * @return The sign tooltip mode if found, else empty.
	 */
	public static @NotNull Optional<SignTooltipMode> byId(@NotNull String id) {
		return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
	}
}
