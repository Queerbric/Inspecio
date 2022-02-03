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

package io.github.queerbric.inspecio.tooltip;

import io.github.queerbric.inspecio.Inspecio;
import io.github.queerbric.inspecio.SignTooltipMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.SignType;
import net.minecraft.util.math.Matrix4f;
import org.quiltmc.qsl.tooltip.api.ConvertibleTooltipData;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class SignTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final SignTooltipMode tooltipMode = Inspecio.get().getConfig().getSignTooltipMode();
	private final SignType type;
	private final OrderedText[] text;
	private final DyeColor color;
	private final boolean glowingText;
	private final SignBlockEntityRenderer.SignModel model;

	public SignTooltipComponent(SignType type, OrderedText[] text, DyeColor color, boolean glowingText) {
		this.type = type;
		this.text = text;
		this.color = color;
		this.glowingText = glowingText;
		this.model = SignBlockEntityRenderer.createSignModel(this.client.getEntityModelLoader(), this.type);
	}

	public static Optional<TooltipData> fromItemStack(ItemStack stack) {
		if (!Inspecio.get().getConfig().getSignTooltipMode().isEnabled())
			return Optional.empty();

		if (stack.getItem() instanceof SignItem signItem) {
			var block = signItem.getBlock();
			var nbt = BlockItem.getBlockEntityNbtFromStack(stack);
			if (nbt != null) return Optional.of(fromTag(SignBlockEntityRenderer.getSignType(block), nbt));
		}
		return Optional.empty();
	}

	public static SignTooltipComponent fromTag(SignType type, NbtCompound nbt) {
		var color = DyeColor.byName(nbt.getString("Color"), DyeColor.BLACK);

		var lines = new OrderedText[4];
		for (int i = 0; i < 4; ++i) {
			var serialized = nbt.getString("Text" + (i + 1));
			var text = Text.Serializer.fromJson(serialized.isEmpty() ? "\"\"" : serialized).asOrderedText();
			lines[i] = text;
		}

		boolean glowingText = nbt.getBoolean("GlowingText");

		return new SignTooltipComponent(type, lines, color, glowingText);
	}

	@Override
	public TooltipComponent toComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		if (this.tooltipMode == SignTooltipMode.FANCY)
			return 52;
		return this.text.length * 10;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		if (this.tooltipMode == SignTooltipMode.FANCY)
			return 94;
		return Arrays.stream(this.text).map(textRenderer::getWidth).max(Comparator.naturalOrder()).orElse(94);
	}

	@Override
	public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate) {
		if (this.tooltipMode != SignTooltipMode.FAST)
			return;

		int signColor = this.color.getSignColor();

		if (glowingText) {
			int outlineColor;
			if (this.color == DyeColor.BLACK) {
				outlineColor = -988212;
			} else {
				int r = (int) (NativeImage.getRed(signColor) * 0.4);
				int g = (int) (NativeImage.getGreen(signColor) * 0.4);
				int b = (int) (NativeImage.getBlue(signColor) * 0.4);

				outlineColor = NativeImage.getAbgrColor(0, b, g, r);
			}

			for (var text : this.text) {
				textRenderer.drawWithOutline(text, x, y, signColor, outlineColor, matrix4f, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE);
				y += 10;
			}
		} else {
			for (var text : this.text) {
				textRenderer.draw(text, x, y, signColor, true, matrix4f, immediate, false,
						0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
				y += 10;
			}
		}
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
		if (this.tooltipMode != SignTooltipMode.FANCY)
			return;

		DiffuseLighting.disableGuiDepthLighting();
		matrices.push();
		matrices.translate(x + 2, y, z);

		matrices.push();
		matrices.translate(45, 56, 0);
		matrices.scale(65, 65, -65);
		var immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
		var spriteIdentifier = TexturedRenderLayers.getSignTextureId(this.type);
		var vertexConsumer = spriteIdentifier.getVertexConsumer(immediate, this.model::getLayer);
		this.model.stick.visible = false;
		this.model.root.visible = true;
		this.model.root.render(matrices, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
		immediate.draw();
		matrices.pop();

		matrices.translate(0, 4, 10);

		for (int i = 0; i < this.text.length; i++) {
			var text = this.text[i];
			textRenderer.draw(matrices, text, 45 - textRenderer.getWidth(text) / 2.f, i * 10, this.color.getSignColor());
			y += textRenderer.fontHeight + 2;
		}
		matrices.pop();

		DiffuseLighting.enableGuiDepthLighting();
	}
}
