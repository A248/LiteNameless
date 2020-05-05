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

import com.namelessmc.NamelessAPI.NamelessAPI;
import com.namelessmc.NamelessAPI.NamelessException;

import space.arim.shaded.org.slf4j.Logger;
import space.arim.shaded.org.slf4j.LoggerFactory;

import space.arim.universal.registry.Registry;

import space.arim.api.concurrent.AsyncExecution;

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
	 * Primary constructor, based on a configuration folder and {@link Registry}.
	 * 
	 * @param folder the config folder
	 * @param registry the registry
	 */
	public LiteNamelessCore(File folder, Registry registry) {
		this.logger = LoggerFactory.getLogger(LiteNamelessCore.class);
		this.registry = registry;
		config = new Config(folder);
		commands = new Commands(this);
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
