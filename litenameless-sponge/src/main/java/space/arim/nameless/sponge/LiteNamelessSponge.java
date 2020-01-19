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
import java.util.UUID;
import java.util.logging.Logger;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.google.inject.Inject;

import space.arim.api.server.sponge.SpongeUtil;

import space.arim.namelessplugin.LiteNameless;
import space.arim.namelessplugin.api.PlayerWrapper;
import space.arim.namelessplugin.api.SenderWrapper;
import space.arim.namelessplugin.api.ServerEnv;

public class LiteNamelessSponge implements CommandExecutor, ServerEnv {

	@Inject
	@ConfigDir(sharedRoot=false)
	private File folder;
	
	private LiteNameless core;
	
	@Listener
	private void onEnable(@SuppressWarnings("unused") GamePreInitializationEvent evt) {
		Logger logger = Logger.getLogger("LiteNameless");
		logger.setParent(Logger.getLogger(""));
		core = new LiteNameless(logger, folder, this);
		Sponge.getCommandManager().register(this, CommandSpec.builder().executor(this).build(), "litenameless");		
	}
	
	@Listener
    private void onDisable(@SuppressWarnings("unused") GameStoppingServerEvent evt) {
        core.close();
        core = null;
    }
	
	@Override
	public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {
		core.executeCommand(new WrappedSender(sender), args.getAll("").toArray(new String[] {}));
		return CommandResult.success();
	}
	
	@Listener
	private void onJoin(ClientConnectionEvent.Join evt) {
		core.login(new WrappedPlayer(evt.getTargetEntity()));
	}
	
	@Override
	public PlayerWrapper getIfOnline(String name) {
		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(name)) {
				return new WrappedPlayer(player);
			}
		}
		return null;
	}

}

class WrappedSender implements SenderWrapper {
	
	final CommandSource sender;
	
	WrappedSender(CommandSource sender) {
		this.sender = sender;
	}

	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public void sendMessage(String message) {
		sender.sendMessage(SpongeUtil.colour(message));
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
