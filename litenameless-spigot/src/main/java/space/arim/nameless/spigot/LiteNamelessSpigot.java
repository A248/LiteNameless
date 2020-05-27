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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import space.arim.universal.registry.Registry;
import space.arim.universal.registry.RegistryPriority;
import space.arim.universal.registry.UniversalRegistry;

import space.arim.api.plugin.ArimApiPluginSpigot;
import space.arim.nameless.api.LiteNameless;
import space.arim.nameless.core.LiteNamelessCore;

public class LiteNamelessSpigot extends JavaPlugin implements Listener {
	
	private LiteNamelessCore core;
	
	@Override
	public void onLoad() {
		ArimApiPluginSpigot.registerDefaultAsyncExecutionIfAbsent(getRegistry());
		ArimApiPluginSpigot.registerDefaultSyncExecutionIfAbsent(getRegistry());
	}
	
	private Registry getRegistry() {
		return UniversalRegistry.get();
	}
	
	@Override
	public void onEnable() {
		core = new LiteNamelessCore(getDataFolder(), getRegistry());
		core.reload();
		getServer().getPluginManager().registerEvents(this, this);
		getRegistry().register(LiteNameless.class, RegistryPriority.LOWER, core, "LiteNameless-Spigot");
	}
	
	@Override
	public void onDisable() {
		core.close();
		core = null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return core.executeCommand(new WrappedSender(sender), args);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onJoin(PlayerJoinEvent evt) {
		core.updateGroup(new WrappedPlayer(evt.getPlayer()));
	}
	
}
