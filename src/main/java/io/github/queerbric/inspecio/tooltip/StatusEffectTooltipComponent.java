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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.util.math.Matrix4f;

import java.util.List;

public class StatusEffectTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private final List<StatusEffectInstance> list;
	private final float multiplier;

	public StatusEffectTooltipComponent(List<StatusEffectInstance> list, float multiplier) {
		this.list = list;
		this.multiplier = multiplier;
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return list.size() * 20;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		int max = 64;
		for (int i = 0; i < list.size(); i++) {
			StatusEffectInstance statusEffectInstance = list.get(i);
			String string = I18n.translate(statusEffectInstance.getEffectType().getTranslationKey());
			if (statusEffectInstance.getAmplifier() >= 1 && statusEffectInstance.getAmplifier() <= 9) {
				string = string + ' ' + I18n.translate("enchantment.level." + (statusEffectInstance.getAmplifier() + 1));
			}
			max = Math.max(max, 26 + textRenderer.getWidth(string));
		}
		return max;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
		MinecraftClient client = MinecraftClient.getInstance();
		StatusEffectSpriteManager statusEffectSpriteManager = client.getStatusEffectSpriteManager();
		for (int i = 0; i < list.size(); i++) {
			StatusEffectInstance statusEffectInstance = list.get(i);
			StatusEffect statusEffect = statusEffectInstance.getEffectType();
			Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
			client.getTextureManager().bindTexture(sprite.getAtlas().getId());
			DrawableHelper.drawSprite(matrices, x, y + i * 20, z, 18, 18, sprite);
		}
	}

	@Override
	public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, Immediate immediate) {
		for (int i = 0; i < list.size(); i++) {
			StatusEffectInstance statusEffectInstance = list.get(i);
			String string = I18n.translate(statusEffectInstance.getEffectType().getTranslationKey());
			if (statusEffectInstance.getAmplifier() >= 1 && statusEffectInstance.getAmplifier() <= 9) {
				string = string + ' ' + I18n.translate("enchantment.level." + (statusEffectInstance.getAmplifier() + 1));
			}
			int off = 0;
			if (statusEffectInstance.getDuration() <= 1) {
				off += 5;
			}
			Integer color = statusEffectInstance.getEffectType().getType().getFormatting().getColorValue();
			textRenderer.draw(string, x + 24, y + i * 20 + off, color != null ? color : 16777215,
					true, matrix4f, immediate, false, 0, 15728880);
			if (statusEffectInstance.getDuration() > 1) {
				String string2 = StatusEffectUtil.durationToString(statusEffectInstance, multiplier);
				textRenderer.draw(string2, x + 24, y + i * 20 + 10, 8355711, true, matrix4f, immediate, false, 0, 15728880);
			}
		}
	}
}
