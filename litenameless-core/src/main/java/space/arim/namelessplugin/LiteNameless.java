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
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import space.arim.universal.util.exception.HttpStatusException;

import space.arim.api.concurrent.AsyncExecutor;
import space.arim.api.concurrent.SyncExecutor;
import space.arim.api.util.web.NamelessHandler;
import space.arim.api.util.web.SenderException;

import space.arim.namelessplugin.api.PlayerWrapper;
import space.arim.namelessplugin.api.ServerEnv;

public class LiteNameless {
	
	private final Logger logger;
	private final File folder;
	private final ServerEnv env;
	
	private final Config config;
	private final Commands commands;
	
	private final ConcurrentHashMap<UUID, Long> loginTime = new ConcurrentHashMap<UUID, Long>();
	
	private NamelessHandler nameless;
	private boolean sending = false;
	
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
	
	public void updateLoginTime(UUID uuid, long millis) {
		loginTime.put(uuid, millis);
	}
	
	public void reload() {
		config.reload();
		startSending();
	}
	
	public void updateUsername(UUID uuid, String name) {
		if (nameless != null) {
			env.getRegistry().getRegistration(AsyncExecutor.class).execute(() -> {
				try {
					nameless.updateUsername(uuid, name);
				} catch (HttpStatusException | SenderException ex) {
					logger().log(Level.WARNING, "Failed to update username for " + name + " / " + uuid, ex);
				}
			});
		}
	}
	
	public void executeCommand(PlayerWrapper player, String[] args) {
		commands.executeCommand(player, args);
	}
	
	Config config() {
		return config;
	}
	
	void startSending() {
		if (!sending && config.getInt("settings.server-id") != 0) {
			sending = true;
			nameless = new NamelessHandler(config.getString("settings.api.host"), config.getString("settings.api.key"));
			sendData(config.getInt("settings.server-id"), config.getInt("settings.upload-period"));
		}
	}
	
	private void sendData(int serverId, long uploadRate) {		
		env.getRegistry().getRegistration(SyncExecutor.class).runTaskTimer(() -> {
			env.getRegistry().getRegistration(AsyncExecutor.class).execute(() -> {
				
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put("tps", env.getCurrentTps());
				data.put("time", System.currentTimeMillis());
				data.put("free-memory", Runtime.getRuntime().freeMemory());
				data.put("max-memory", Runtime.getRuntime().maxMemory());
				data.put("allocated-memory", Runtime.getRuntime().totalMemory());
				data.put("server-id", serverId);
				
				HashMap<String, Object> playerList = new HashMap<String, Object>();
				env.getPlayersOnline().forEach((player) -> {
					
					HashMap<String, Object> info = new HashMap<String, Object>();
					info.put("name", player.getName());
					
					HashMap<String, Object> location = new HashMap<String, Object>();
					location.put("world", player.getLocationWorld());
					location.put("x", player.getLocationX());
					location.put("y", player.getLocationY());
					location.put("z", player.getLocationZ());
					
					info.put("location", location);
					info.put("ip", player.getAddress());
					info.put("placeholders", Collections.emptyMap());
					info.put("login-time", loginTime.getOrDefault(player.getUniqueId(), System.currentTimeMillis()));
					
					playerList.put(player.getUniqueId().toString().replace("-", ""), info);
					
				});
				
				data.put("players", playerList);
				
				// send the data
				
			});
		}, uploadRate);
	}
	
}
