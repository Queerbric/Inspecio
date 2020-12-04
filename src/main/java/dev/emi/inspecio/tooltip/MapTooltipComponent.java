package dev.emi.inspecio.tooltip;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.map.MapState;

public class MapTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	public int map;

	public MapTooltipComponent(int map) {
		this.map = map;
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		if (map == -1) return 0;
		return 128;
	}
	
	@Override
	public int getWidth(TextRenderer textRenderer) {
		if (map == -1) return 0;
		return 128;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
		if (map == -1) return;
		MinecraftClient client = MinecraftClient.getInstance();
		VertexConsumerProvider vertices = client.getBufferBuilders().getEntityVertexConsumers();
		MapRenderer map = client.gameRenderer.getMapRenderer();
		MapState state = map.method_32599(this.map);
		if (state == null) return;
		matrices.push();
		matrices.scale(0, 0, 0);
		map.draw(matrices, vertices, this.map, state, false, 13);
		matrices.pop();
		DrawableHelper.drawTexture(matrices, x, y, z, 0, 0, 128, 128, 128, 128);
	}
}
