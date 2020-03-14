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
package space.arim.namelessplugin;

import java.io.File;
import java.net.MalformedURLException;
import java.util.UUID;

import org.slf4j.Logger;

import com.namelessmc.NamelessAPI.NamelessAPI;
import com.namelessmc.NamelessAPI.NamelessException;

import space.arim.universal.registry.Registry;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.util.log.LoggerConverter;

import space.arim.namelessplugin.api.LiteNameless;
import space.arim.namelessplugin.api.PlayerWrapper;
import space.arim.namelessplugin.api.SenderWrapper;

public class LiteNamelessCore implements LiteNameless {
	
	private final Logger logger;
	private final Registry registry;
	
	private final Config config;
	private final Commands commands;
	
	private volatile NamelessAPI nameless;
	
	public LiteNamelessCore(Logger logger, File folder, Registry registry) {
		this.logger = logger;
		this.registry = registry;
		config = new Config(folder);
		commands = new Commands(this);
	}
	
	public LiteNamelessCore(java.util.logging.Logger logger, File folder, Registry registry) {
		this.logger = LoggerConverter.get().convert(logger);
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
	public void updateGroupAsync(PlayerWrapper player) {
		if (enabled()) {
			getRegistry().getRegistration(AsyncExecution.class).execute(() -> directUpdateGroup(player));
		}
	}
	
	@Override
	public void updateGroup(PlayerWrapper player) {
		if (enabled()) {
			directUpdateGroup(player);
		}
	}
	
	private void directUpdateGroup(PlayerWrapper player) {
		int groupId = getGroup(player);
		if (groupId != -1) {
			setGroup(player.getUniqueId(), groupId);
		}
	}
	
	private int getGroup(PlayerWrapper player) {
		int groupId = -1;
		for (int group : config().getInts("ranks-order")) {
			if (player.hasPermission("litenameless.rank." + group)) {
				groupId = group;
			}
		}
		return groupId;
	}
	
	private void setGroup(UUID uuid, int groupId) {
		try {
			nameless.getPlayer(uuid).setGroup(groupId);
		} catch (NamelessException ex) {
			logger.warn("Failed to set group for " + uuid + " to " + groupId, ex);
		}
	}
	
	Config config() {
		return config;
	}
	
}
