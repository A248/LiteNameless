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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.namelessmc.NamelessAPI.NamelessAPI;
import com.namelessmc.NamelessAPI.NamelessException;

import space.arim.universal.util.AutoClosable;

import space.arim.api.concurrent.AsyncExecution;

import space.arim.namelessplugin.api.PlayerWrapper;
import space.arim.namelessplugin.api.SenderWrapper;
import space.arim.namelessplugin.api.ServerEnv;

public class LiteNameless implements AutoClosable {
	
	private final Logger logger;
	private final File folder;
	private final ServerEnv env;
	
	private final Config config;
	private final Commands commands;
	
	private NamelessAPI nameless;
	
	public LiteNameless(Logger logger, File folder, ServerEnv env) {
		this.logger = logger;
		this.folder = folder;
		this.env = env;
		config = new Config(this);
		commands = new Commands(this);
	}
	
	Logger logger() {
		return logger;
	}
	
	File folder() {
		return folder;
	}
	
	public void reload() {
		config.reload();
		if (config.getBoolean("enable-plugin")) {
			try {
				nameless = new NamelessAPI(config.getString("settings.host"), config.getString("settings.api-key"), false);
			} catch (MalformedURLException ex) {
				logger.log(Level.WARNING, "Could not initialise API! Are you sure your host and api key settings are correct?", ex);
			}
		}
	}
	
	public boolean executeCommand(SenderWrapper player, String[] args) {
		return commands.executeCommand(player, args);
	}
	
	public void login(PlayerWrapper player) {
		if (nameless != null) {
			env.getRegistry().getRegistration(AsyncExecution.class).execute(() -> {
				updateUsername(player.getUniqueId(), player.getName());
				updateGroup(player);
			});
		}
	}
	
	private void updateUsername(UUID uuid, String name) {
		//logger().log(Level.WARNING, "Failed to update username for " + uuid + " / " + name, ex);
	}
	
	private void updateGroup(PlayerWrapper player) {
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
	
	void setGroup(UUID uuid, int groupId) {
		try {
			nameless.getPlayer(uuid).setGroup(groupId);
		} catch (NamelessException ex) {
			logger.log(Level.WARNING, "Failed to set group for " + uuid + " to " + groupId, ex);
		}
	}
	
	ServerEnv env() {
		return env;
	}
	
	Config config() {
		return config;
	}
	
}
