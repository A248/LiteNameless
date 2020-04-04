/* 
 * LiteNameless-core
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * LiteNameless-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LiteNameless-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LiteNameless-core. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.nameless.core;

import java.io.File;
import java.net.MalformedURLException;

import org.slf4j.Logger;

import com.namelessmc.NamelessAPI.NamelessAPI;
import com.namelessmc.NamelessAPI.NamelessException;

import space.arim.universal.registry.Registry;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.util.log.LoggerConverter;

import space.arim.nameless.api.LiteNameless;
import space.arim.nameless.api.PlayerWrapper;
import space.arim.nameless.api.SenderWrapper;

/**
 * Main LiteNameless implementation. <br>
 * <br>
 * {@link #reload()} should be called after construction to load the plugin for the first time.
 * 
 * @author A248
 *
 */
public class LiteNamelessCore implements LiteNameless {
	
	private final Logger logger;
	private final Registry registry;
	
	private final Config config;
	private final Commands commands;
	
	private volatile NamelessAPI nameless;
	
	/**
	 * Primary constructor, based on a logger, configuration folder, and {@link Registry}.
	 * 
	 * @param logger the logger
	 * @param folder the config folder
	 * @param registry the registry
	 */
	public LiteNamelessCore(Logger logger, File folder, Registry registry) {
		this.logger = logger;
		this.registry = registry;
		config = new Config(folder);
		commands = new Commands(this);
	}
	
	/**
	 * See {@link #LiteNamelessCore(Logger, File, Registry)} first <br>
	 * <br>
	 * Whenever possible, slf4j should be preferred and used instead. <br>
	 * This is an alternative constructor used to maintain support for the JUL logging framework.
	 * 
	 * @param logger a JUL logger
	 * @param folder the config folder
	 * @param registry the registry
	 */
	public LiteNamelessCore(java.util.logging.Logger logger, File folder, Registry registry) {
		this(LoggerConverter.get().convert(logger), folder, registry);
	}
	
	@Override
	public void reload() {
		config.reload();
		if (config.getBoolean("enable-plugin")) {
			try {
				nameless = new NamelessAPI(config.getString("settings.host"), config.getString("settings.api-key"), false);
			} catch (MalformedURLException ex) {
				logger.warn("Could not initialise API! Are you sure your host and api key settings are correct?", ex);
			}
		}
	}
	
	@Override
	public boolean enabled() {
		return nameless != null && config.getBoolean("enable-plugin");
	}
	
	@Override
	public Registry getRegistry() {
		return registry;
	}
	
	@Override
	public boolean executeCommand(SenderWrapper player, String[] args) {
		return commands.executeCommand(player, args);
	}
	
	@Override
	public void updateGroup(PlayerWrapper player) {
		if (enabled()) {
			getRegistry().load(AsyncExecution.class).execute(() -> directUpdateGroup(player));
		}
	}
	
	@Override
	public void updateGroupSynchronous(PlayerWrapper player) {
		if (enabled()) {
			directUpdateGroup(player);
		}
	}
	
	private void directUpdateGroup(PlayerWrapper player) {
		for (int group : config().getInts("ranks-order")) {
			if (player.hasPermission("litenameless.rank." + group)) {
				try {
					nameless.getPlayer(player.getUniqueId()).setGroup(group);
				} catch (NamelessException ex) {
					logger.warn("Failed to set group for " + player + " to " + group, ex);
				}
				break;
			}
		}
	}
	
	Config config() {
		return config;
	}
	
}
