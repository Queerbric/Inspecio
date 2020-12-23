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

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the Inspecio mod.
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class Inspecio implements ClientModInitializer {
	public static final String NAMESPACE = "inspecio";
	private static Inspecio INSTANCE;
	private final Logger logger = LogManager.getLogger("inspecio");
	private InspecioConfig config;

	@Override
	public void onInitializeClient() {
		INSTANCE = this;

		this.config = InspecioConfig.load(this);
	}

	/**
	 * Prints a message to the terminal.
	 *
	 * @param info the message to log
	 */
	public void log(String info) {
		this.logger.info("[Inspecio] " + info);
	}

	/**
	 * Prints a warning message to the terminal.
	 *
	 * @param info the message to log
	 */
	public void warn(String info) {
		this.logger.warn("[Inspecio] " + info);
	}

	/**
	 * Prints a warning message to the terminal.
	 *
	 * @param info the message to log
	 * @param throwable the exception to log, including its stack trace.
	 */
	public void warn(String info, Throwable throwable) {
		this.logger.warn("[Inspecio] " + info, throwable);
	}

	public InspecioConfig getConfig() {
		return this.config;
	}

	public static Inspecio get() {
		return INSTANCE;
	}
}
