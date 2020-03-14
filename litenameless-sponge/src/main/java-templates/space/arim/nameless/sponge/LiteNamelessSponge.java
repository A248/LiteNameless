/* 
 * LiteNameless-sponge
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * LiteNameless-sponge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LiteNameless-sponge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LiteNameless-sponge. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.nameless.sponge;

import java.io.File;

import org.slf4j.Logger;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.AsynchronousExecutor;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.SynchronousExecutor;

import com.google.inject.Inject;

import space.arim.universal.registry.Registry;
import space.arim.universal.registry.UniversalRegistry;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.concurrent.Shutdownable;
import space.arim.api.concurrent.SyncExecution;
import space.arim.api.platform.sponge.DecoupledCommand;
import space.arim.api.platform.sponge.DefaultAsyncExecution;
import space.arim.api.platform.sponge.DefaultSyncExecution;
import space.arim.api.platform.sponge.DefaultUUIDResolver;
import space.arim.api.uuid.UUIDResolver;

import space.arim.namelessplugin.LiteNamelessCore;

@Plugin(id = "${plugin.spongeid}", name = "${plugin.name}", version = "${plugin.version}", authors = {"${plugin.author}"}, description = "${plugin.description}", url = "${plugin.url}", dependencies = {@Dependency(id = "arimapiplugin")})
public class LiteNamelessSponge extends DecoupledCommand {

	@Inject
	@ConfigDir(sharedRoot=false)
	private File folder;
	
	@Inject
	private Logger logger;
	
	private LiteNamelessCore core;
	
	@Inject
	public LiteNamelessSponge(@AsynchronousExecutor SpongeExecutorService async, @SynchronousExecutor SpongeExecutorService sync) {
		sync.execute(() -> {
			PluginContainer plugin = Sponge.getPluginManager().fromInstance(LiteNamelessSponge.this).get();
			getRegistry().computeIfAbsent(AsyncExecution.class, () -> new DefaultAsyncExecution(plugin, async));
			getRegistry().computeIfAbsent(SyncExecution.class, () -> new DefaultSyncExecution(plugin, sync));
			getRegistry().computeIfAbsent(UUIDResolver.class, () -> new DefaultUUIDResolver(plugin));
		});
	}
	
	private Registry getRegistry() {
		return UniversalRegistry.get();
	}
	
	@Listener
	public void onEnable(@SuppressWarnings("unused") GamePreInitializationEvent evt) {
		core = new LiteNamelessCore(logger, folder, getRegistry());
		Sponge.getCommandManager().register(this, this, "litenameless");		
	}
	
	@Listener
    public void onDisable(@SuppressWarnings("unused") GameStoppingServerEvent evt) {
		AsyncExecution async = getRegistry().getRegistration(AsyncExecution.class);
		if (async instanceof Shutdownable) {
			((Shutdownable) async).shutdownAndWait();
		}
        core.close();
        core = null;
    }
	
	@Override
	protected boolean execute(CommandSource sender, String[] args) {
		return core.executeCommand(new WrappedSender(sender), args);
	}
	
	@Listener
	public void onJoin(ClientConnectionEvent.Join evt) {
		core.updateGroup(new WrappedPlayer(evt.getTargetEntity()));
	}

}
