/*
 * Copyright (c) 2020 LambdAurora <email@lambdaurora.dev>, Emi
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

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.github.queerbric.inspecio.HiddenEffectMode;
import io.github.queerbric.inspecio.Inspecio;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.quiltmc.qsl.tooltip.api.ConvertibleTooltipData;

import java.util.List;

public class StatusEffectTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private static final Identifier MYSTERY_TEXTURE = new Identifier(Inspecio.NAMESPACE, "textures/mob_effects/mystery.png");
	private List<StatusEffectInstance> list = Lists.newArrayList();
	private final FloatList chances = new FloatArrayList();
	private boolean hidden = false;
	private float multiplier;

	public StatusEffectTooltipComponent(List<StatusEffectInstance> list, float multiplier) {
		this.list = list;
		this.multiplier = multiplier;
	}

	public StatusEffectTooltipComponent(List<Pair<StatusEffectInstance, Float>> list) {
		for (var pair : list) {
			this.list.add(pair.getFirst());
			this.chances.add(pair.getSecond().floatValue());
		}
		this.multiplier = 1.f;
	}

	public StatusEffectTooltipComponent() {
		this.hidden = true;
	}

	private Text getHiddenText() {
		var effectsConfig = Inspecio.getConfig().getEffectsConfig();
		boolean hiddenMotion = effectsConfig.hasHiddenMotion();
		HiddenEffectMode hiddenEffectMode = effectsConfig.getHiddenEffectMode();

		return hiddenEffectMode.stylize(Text.literal(hiddenEffectMode.getText(true, hiddenMotion)), hiddenMotion);
	}

	private Text getHiddenTime() {
		var effectsConfig = Inspecio.getConfig().getEffectsConfig();
		boolean hiddenMotion = effectsConfig.hasHiddenMotion();
		HiddenEffectMode hiddenEffectMode = effectsConfig.getHiddenEffectMode();

		String timeColon = hiddenEffectMode == HiddenEffectMode.ENCHANTMENT && hiddenMotion ? "i" : ":";

		MutableText minutes = hiddenEffectMode.stylize(Text.literal(hiddenEffectMode.getText(false, hiddenMotion)), hiddenMotion);
		Text seconds = minutes.copy();

		return Text.empty().append(minutes)
				.append(hiddenEffectMode.stylize(Text.literal(timeColon), false))
				.append(seconds);
	}

	@Override
	public TooltipComponent toComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		if (this.hidden) {
			return 20;
		}
		return this.list.size() * 20;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		if (this.hidden) {
			return 26 + textRenderer.getWidth(this.getHiddenText());
		}

		int max = 64;
		for (int i = 0; i < this.list.size(); i++) {
			StatusEffectInstance statusEffectInstance = this.list.get(i);
			String statusEffectName = this.getStatusEffectName(statusEffectInstance);

			if (statusEffectInstance.getDuration() > 1) {
				var duration = this.getDuration(i, statusEffectInstance);
				max = Math.max(max, 26 + textRenderer.getWidth(duration));
			} else if (this.chances.size() > i && this.chances.getFloat(i) < 1f) {
				String string2 = (int) (this.chances.getFloat(i) * 100f) + "%";
				max = Math.max(max, 26 + textRenderer.getWidth(string2));
			}
			max = Math.max(max, 26 + textRenderer.getWidth(statusEffectName));
		}
		return max;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, GuiGraphics graphics) {
		if (this.hidden) {
			graphics.drawTexture(MYSTERY_TEXTURE, x, y, 0, 0, 18, 18, 18, 18);
		} else {
			MinecraftClient client = MinecraftClient.getInstance();
			StatusEffectSpriteManager statusEffectSpriteManager = client.getStatusEffectSpriteManager();
			for (int i = 0; i < list.size(); i++) {
				StatusEffectInstance statusEffectInstance = list.get(i);
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				var sprite = statusEffectSpriteManager.getSprite(statusEffect);
				graphics.drawSprite(x, y + i * 20, 0, 18, 18, sprite);
			}
		}
	}

	@Override
	public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f model, Immediate immediate) {
		if (this.hidden) {
			textRenderer.draw(this.getHiddenText(), x + 24, y, 8355711, true,
					model, immediate, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
			textRenderer.draw(this.getHiddenTime(), x + 24, y + 10, 8355711, true,
					model, immediate, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		} else {
			for (int i = 0; i < this.list.size(); i++) {
				StatusEffectInstance statusEffectInstance = this.list.get(i);
				String statusEffectName = this.getStatusEffectName(statusEffectInstance);

				int off = 0;
				if (statusEffectInstance.getDuration() <= 1) {
					off += 5;
				}

				Integer color = statusEffectInstance.getEffectType().getType().getFormatting().getColorValue();
				textRenderer.draw(statusEffectName, x + 24, y + i * 20 + off, color != null ? color : 16777215,
						true, model, immediate, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
				if (statusEffectInstance.getDuration() > 1) {
					var duration = this.getDuration(i, statusEffectInstance);
					textRenderer.draw(duration, x + 24, y + i * 20 + 10, 8355711, true,
							model, immediate, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
				} else if (this.chances.size() > i && this.chances.getFloat(i) < 1f) {
					String chance = (int) (this.chances.getFloat(i) * 100f) + "%";
					textRenderer.draw(chance, x + 24, y + i * 20 + 10, 8355711, true,
							model, immediate, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
				}
			}
		}
	}

	private String getStatusEffectName(StatusEffectInstance statusEffectInstance) {
		String statusEffectName = I18n.translate(statusEffectInstance.getEffectType().getTranslationKey());

		if (statusEffectInstance.getAmplifier() >= 1 && statusEffectInstance.getAmplifier() <= 9) {
			statusEffectName = statusEffectName + ' ' + I18n.translate("enchantment.level." + (statusEffectInstance.getAmplifier() + 1));
		}

		return statusEffectName;
	}

	private Text getDuration(int index, StatusEffectInstance statusEffect) {
		var duration = StatusEffectUtil.durationToString(statusEffect, multiplier);

		if (this.chances.size() > index && this.chances.getFloat(index) < 1f) {
			duration = duration.copy().append(" - " + (int) (this.chances.getFloat(index) * 100f) + "%");
		}

		return duration;
	}
}
