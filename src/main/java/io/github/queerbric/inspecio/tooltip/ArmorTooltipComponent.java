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

import io.github.queerbric.inspecio.Inspecio;
import io.github.queerbric.inspecio.mixin.ItemStackAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import org.quiltmc.qsl.tooltip.api.ConvertibleTooltipData;

import java.util.Optional;

public class ArmorTooltipComponent implements ConvertibleTooltipData, TooltipComponent {
	private final int prot;

	public ArmorTooltipComponent(int prot) {
		this.prot = prot;
	}

	public static Optional<ArmorTooltipComponent> of(ItemStack stack) {
		if (stack.getItem() instanceof ArmorItem armor && Inspecio.getConfig().hasArmor()) {
			int prot = armor.getMaterial().getProtection(armor.getArmorSlot());

			int hideFlags = ((ItemStackAccessor) (Object) stack).invokeGetHideFlags();
			if (ItemStackAccessor.invokeIsSectionVisible(hideFlags, ItemStack.TooltipSection.MODIFIERS)) {
				return Optional.of(new ArmorTooltipComponent(prot));
			}
		}

		return Optional.empty();
	}

	@Override
	public TooltipComponent toComponent() {
		return this;
	}

	@Override
	public int getHeight() {
		return 11;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return this.prot / 2 * 9;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, GuiGraphics graphics) {
		for (int i = 0; i < this.prot / 2; i++) {
			graphics.drawTexture(Inspecio.GUI_ICONS_TEXTURE, x + i * 9, y, 34, 9, 9, 9, 256, 256);
		}
		if (this.prot % 2 == 1) {
			graphics.drawTexture(Inspecio.GUI_ICONS_TEXTURE, x + this.prot / 2 * 9, y, 25, 9, 9, 9, 256, 256);
		}
	}
}
