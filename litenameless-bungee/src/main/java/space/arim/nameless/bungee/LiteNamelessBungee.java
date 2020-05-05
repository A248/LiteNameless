/* 
 * LiteNameless-bungee
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * LiteNameless-bungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LiteNameless-bungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LiteNameless-bungee. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.nameless.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import space.arim.universal.registry.Registry;
import space.arim.universal.registry.RegistryPriority;
import space.arim.universal.registry.UniversalRegistry;

import space.arim.api.plugin.ArimApiPluginBungee;

import space.arim.nameless.api.LiteNameless;
import space.arim.nameless.core.LiteNamelessCore;

public class LiteNamelessBungee extends Plugin implements Listener {
	
	private LiteNamelessCore core;
	
	@Override
	public void onLoad() {
		ArimApiPluginBungee.registerDefaultUUIDResolutionIfAbsent(getRegistry());
		ArimApiPluginBungee.registerDefaultAsyncExecutionIfAbsent(getRegistry());
		ArimApiPluginBungee.registerDefaultSyncExecutionIfAbsent(getRegistry());
	}
	
	private Registry getRegistry() {
		return UniversalRegistry.get();
	}
	
	@Override
	public void onEnable() {
		core = new LiteNamelessCore(getDataFolder(), getRegistry());
		core.reload();
		getProxy().getPluginManager().registerCommand(this, new Command("litenameless") {
			
			@Override
			public void execute(CommandSender sender, String[] args) {
				core.executeCommand(new WrappedSender(sender), args);
			}
			
		});
		getProxy().getPluginManager().registerListener(this, this);
		getRegistry().register(LiteNameless.class, RegistryPriority.LOWER, core, "LiteNameless-Bungee");
	}
	
	@Override
	public void onDisable() {
		core.close();
		core = null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onJoin(PostLoginEvent evt) {
		core.updateGroup(new WrappedPlayer(evt.getPlayer()));
	}
	
}
