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

import com.google.inject.Inject;

import space.arim.universal.registry.Registry;
import space.arim.universal.registry.RegistryPriority;
import space.arim.universal.registry.UniversalRegistry;

import space.arim.api.platform.sponge.DecoupledCommand;
import space.arim.api.plugin.ArimApiPluginSponge;

import space.arim.nameless.api.LiteNameless;
import space.arim.nameless.core.LiteNamelessCore;

@Plugin(id = "${plugin.spongeid}", name = "${plugin.name}", version = "${plugin.version}", authors = {"${plugin.author}"}, description = "${plugin.description}", url = "${plugin.url}", dependencies = {@Dependency(id = "arimapiplugin")})
public class LiteNamelessSponge extends DecoupledCommand {

	@Inject
	@ConfigDir(sharedRoot=false)
	private File folder;
	
	@Inject
	private Logger logger;
	
	private LiteNamelessCore core;
	
	public LiteNamelessSponge() {
		ArimApiPluginSponge.registerDefaultUUIDResolutionIfAbsent(getRegistry());
		ArimApiPluginSponge.registerDefaultAsyncExecutionIfAbsent(getRegistry());
		ArimApiPluginSponge.registerDefaultSyncExecutionIfAbsent(getRegistry());
	}
	
	private Registry getRegistry() {
		return UniversalRegistry.get();
	}
	
	@Listener
	public void onEnable(@SuppressWarnings("unused") GamePreInitializationEvent evt) {
		core = new LiteNamelessCore(logger, folder, getRegistry());
		Sponge.getCommandManager().register(this, this, "litenameless");
		getRegistry().register(LiteNameless.class, RegistryPriority.LOWER, core, "LiteNameless-Sponge");
	}
	
	@Listener
    public void onDisable(@SuppressWarnings("unused") GameStoppingServerEvent evt) {
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
