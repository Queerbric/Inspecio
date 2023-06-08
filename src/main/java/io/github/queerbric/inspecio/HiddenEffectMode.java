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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Represents the different hidden effect modes.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public enum HiddenEffectMode {
	OBFUSCATED {
		@Override
		public String getText(boolean isLong, boolean motion) {
			return (motion ? "f" : "?").repeat(isLong ? 8 : 2);
		}

		@Override
		public MutableText stylize(MutableText text, boolean motion) {
			return motion ? text.formatted(Formatting.OBFUSCATED) : text;
		}
	},
	ENCHANTMENT {
		@Override
		public String getText(boolean isLong, boolean motion) {
			return isLong ? "lostquasar" : "na";
		}

		@Override
		public MutableText stylize(MutableText text, boolean motion) {
			text = text.styled(style -> style.withFont(ALT_FONT_ID));
			return motion ? text.formatted(Formatting.OBFUSCATED) : text;
		}
	};

	private static final Identifier ALT_FONT_ID = new Identifier("minecraft", "alt");

	public static final PrimitiveCodec<HiddenEffectMode> CODEC = new PrimitiveCodec<>() {
		@Override
		public <T> DataResult<HiddenEffectMode> read(final DynamicOps<T> ops, final T input) {
			return ops.getStringValue(input).map(id -> byId(id).orElse(ENCHANTMENT));
		}

		@Override
		public <T> T write(final DynamicOps<T> ops, final HiddenEffectMode value) {
			return ops.createString(value.getName());
		}

		@Override
		public String toString() {
			return "HiddenEffectMode";
		}
	};

	/**
	 * Gets the text to be used.
	 *
	 * @param isLong {@code true} if the text is long, or {@code false} otherwise
	 * @param motion {@code true} if motion is allowed, or {@code false} otherwise
	 * @return the text
	 */
	public abstract String getText(boolean isLong, boolean motion);

	/**
	 * Stylizes the given text.
	 *
	 * @param text the text to style
	 * @param motion {@code true} if motion is allowed, or {@code false} otherwise
	 * @return the stylized text
	 */
	public abstract MutableText stylize(MutableText text, boolean motion);

	/**
	 * {@return the next available hidden effect mode}
	 */
	public HiddenEffectMode next() {
		var v = values();
		if (v.length == this.ordinal() + 1)
			return v[0];
		return v[this.ordinal() + 1];
	}

	public @NotNull String getName() {
		return this.name().toLowerCase();
	}

	/**
	 * Gets the hidden effect mode from its identifier.
	 *
	 * @param id the identifier of the hidden effect mode
	 * @return the hidden effect mode if found, else empty
	 */
	public static @NotNull Optional<HiddenEffectMode> byId(@NotNull String id) {
		return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
	}

	public static class HiddenEffectType implements ArgumentType<HiddenEffectMode> {
		private static final SimpleCommandExceptionType UNKNOWN_VALUE = new SimpleCommandExceptionType(
				Text.translatable("inspecio.command.error.unknown_hidden_effect_mode"));
		private static final List<HiddenEffectMode> VALUES = List.of(values());

		private HiddenEffectType() {
		}

		public static HiddenEffectType hiddenEffectMode() {
			return new HiddenEffectType();
		}

		public static HiddenEffectMode getHiddenEffectMode(final CommandContext<?> context, final String name) {
			return context.getArgument(name, HiddenEffectMode.class);
		}

		@Override
		public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
			VALUES.stream().map(HiddenEffectMode::getName)
					.filter(s -> s.startsWith(builder.getRemainingLowerCase()))
					.forEach(builder::suggest);
			return builder.buildFuture();
		}

		@Override
		public Collection<String> getExamples() {
			return VALUES.stream().map(HiddenEffectMode::getName).collect(Collectors.toList());
		}

		@Override
		public HiddenEffectMode parse(StringReader reader) throws CommandSyntaxException {
			var value = reader.readString();
			return VALUES.stream().filter(s -> s.name().equalsIgnoreCase(value)).findFirst().orElseThrow(() -> UNKNOWN_VALUE.createWithContext(reader));
		}
	}
}
