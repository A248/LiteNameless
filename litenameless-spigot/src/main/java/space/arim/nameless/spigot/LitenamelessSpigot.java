/* 
 * LiteNameless-spigot
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * LiteNameless-spigot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LiteNameless-spigot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LiteNameless-spigot. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.nameless.spigot;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import space.arim.namelessplugin.LiteNameless;
import space.arim.namelessplugin.api.PlayerWrapper;
import space.arim.namelessplugin.api.ServerEnv;
import space.arim.namelessplugin.api.StaticPlayerData;

public class LitenamelessSpigot extends JavaPlugin implements Listener, ServerEnv {
	
	private LiteNameless core;
	
	@Override
	public void onEnable() {
		core = new LiteNameless(getLogger(), getDataFolder(), this);
		core.reload();
	}
	
	@Override
	public int getCurrentTps() {
		// TODO Get current TPS
		return 20;
	}
	
	@Override
	public Collection<StaticPlayerData> getPlayersOnline() {
		Set<StaticPlayerData> players = new HashSet<StaticPlayerData>();
		getServer().getOnlinePlayers().forEach((player) -> players.add(convertStatic(player)));
		return players;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		core.executeCommand(convertWrapper(sender), args);
		return true;
	}
	
	static StaticPlayerData convertStatic(Player player) {
		return new StaticPlayerData(player.getUniqueId(), player.getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getWorld().getName(), player.getAddress().getAddress().getHostAddress());
	}
	
	static PlayerWrapper convertWrapper(CommandSender player) {
		return new PlayerWrapper() {

			@Override
			public boolean hasPermission(String permission) {
				return player.hasPermission(permission);
			}

			@Override
			public void sendMessage(String message) {
				player.sendMessage(message);
			}
			
		};
	}
	
}
