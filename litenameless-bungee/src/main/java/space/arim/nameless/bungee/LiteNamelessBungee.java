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

import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.concurrent.SyncExecution;
import space.arim.api.server.bungee.BungeeUtil;
import space.arim.api.server.bungee.DefaultAsyncExecution;
import space.arim.api.server.bungee.DefaultSyncExecution;
import space.arim.api.server.bungee.DefaultUUIDResolver;
import space.arim.api.uuid.UUIDResolver;

import space.arim.namelessplugin.LiteNameless;
import space.arim.namelessplugin.api.PlayerWrapper;
import space.arim.namelessplugin.api.SenderWrapper;
import space.arim.namelessplugin.api.ServerEnv;

public class LiteNamelessBungee extends Plugin implements Listener, ServerEnv {
	
	private LiteNameless core;
	
	@Override
	public void onLoad() {
		getRegistry().computeIfAbsent(AsyncExecution.class, () -> new DefaultAsyncExecution(this));
		getRegistry().computeIfAbsent(SyncExecution.class, () -> new DefaultSyncExecution(this));
		getRegistry().computeIfAbsent(UUIDResolver.class, () -> new DefaultUUIDResolver(this));
	}
	
	@Override
	public void onEnable() {
		core = new LiteNameless(getLogger(), getDataFolder(), this);
		core.reload();
		getProxy().getPluginManager().registerCommand(this, new Command("litenameless") {
			
			@Override
			public void execute(CommandSender sender, String[] args) {
				core.executeCommand(new WrappedSender(sender), args);
			}
			
		});
	}
	
	@Override
	public void onDisable() {
		core.close();
		core = null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onJoin(PostLoginEvent evt) {
		core.login(new WrappedPlayer(evt.getPlayer()));
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
		sender.sendMessage(BungeeUtil.colour(message));
	}
	
}

class WrappedPlayer extends WrappedSender implements PlayerWrapper {

	WrappedPlayer(ProxiedPlayer sender) {
		super(sender);
	}
	
	@Override
	public UUID getUniqueId() {
		return ((ProxiedPlayer) sender).getUniqueId();
	}
	
	@Override
	public String getName() {
		return ((ProxiedPlayer) sender).getName();
	}
	
}