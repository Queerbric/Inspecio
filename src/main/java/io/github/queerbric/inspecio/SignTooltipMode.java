/*
 * Copyright (c) 2020 - 2022 LambdAurora <aurora42lambda@gmail.com>, Emi
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

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Represents the different tooltip modes for signs.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
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
	 * @return the next available sign tooltip mode
	 */
	public SignTooltipMode next() {
		var v = values();
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
	 * @param id the identifier of the sign tooltip mode
	 * @return the sign tooltip mode if found, else empty
	 */
	public static @NotNull Optional<SignTooltipMode> byId(@NotNull String id) {
		return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
	}

	public static class SignArgumentType implements ArgumentType<SignTooltipMode> {
		private static final SimpleCommandExceptionType UNKNOWN_VALUE = new SimpleCommandExceptionType(
				new TranslatableText("inspecio.command.error.unknown_sign_tooltip_mode"));
		private static final List<SignTooltipMode> VALUES = List.of(values());

		private SignArgumentType() {
		}

		public static SignArgumentType signTooltipMode() {
			return new SignArgumentType();
		}

		public static SignTooltipMode getSignTooltipMode(final CommandContext<?> context, final String name) {
			return context.getArgument(name, SignTooltipMode.class);
		}

		@Override
		public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
			VALUES.stream().map(SignTooltipMode::getName)
					.filter(s -> s.startsWith(builder.getRemainingLowerCase()))
					.forEach(builder::suggest);
			return builder.buildFuture();
		}

		@Override
		public Collection<String> getExamples() {
			return VALUES.stream().map(SignTooltipMode::getName).collect(Collectors.toList());
		}

		@Override
		public SignTooltipMode parse(StringReader reader) throws CommandSyntaxException {
			var value = reader.readString();
			return VALUES.stream().filter(s -> s.name().equalsIgnoreCase(value)).findFirst().orElseThrow(() -> UNKNOWN_VALUE.createWithContext(reader));
		}
	}
}
