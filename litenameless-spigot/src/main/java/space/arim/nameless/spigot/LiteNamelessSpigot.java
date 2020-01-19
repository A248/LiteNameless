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

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import space.arim.api.server.bukkit.SpigotUtil;

import space.arim.namelessplugin.LiteNameless;
import space.arim.namelessplugin.api.PlayerWrapper;
import space.arim.namelessplugin.api.SenderWrapper;
import space.arim.namelessplugin.api.ServerEnv;

public class LiteNamelessSpigot extends JavaPlugin implements Listener, ServerEnv {
	
	private LiteNameless core;
	
	@Override
	public void onEnable() {
		core = new LiteNameless(getLogger(), getDataFolder(), this);
		core.reload();
	}
	
	@Override
	public void onDisable() {
		core.close();
		core = null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		core.executeCommand(new WrappedSender(sender), args);
		return true;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onJoin(PlayerJoinEvent evt) {
		core.login(new WrappedPlayer(evt.getPlayer()));
	}

	@Override
	public PlayerWrapper getIfOnline(String name) {
		for (Player player : getServer().getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(name)) {
				return new WrappedPlayer(player);
			}
		}
		return null;
	}
	
}

class WrappedSender implements SenderWrapper {
	
	final CommandSender sender;
	
	WrappedSender(CommandSender sender) {
		this.sender = sender;
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}
	
	@Override
	public void sendMessage(String message) {
		sender.sendMessage(SpigotUtil.colour(message));
	}
	
}

class WrappedPlayer extends WrappedSender implements PlayerWrapper {

	WrappedPlayer(Player sender) {
		super(sender);
	}

	@Override
	public UUID getUniqueId() {
		return ((Player) sender).getUniqueId();
	}
	
	@Override
	public String getName() {
		return ((Player) sender).getName();
	}
	
}
