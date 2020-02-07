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

import java.util.UUID;

import space.arim.api.concurrent.AsyncExecution;
import space.arim.api.uuid.PlayerNotFoundException;
import space.arim.api.uuid.UUIDResolver;

import space.arim.namelessplugin.api.SenderWrapper;

class Commands {

	private final LiteNameless core;
	
	Commands(LiteNameless core) {
		this.core = core;
	}
	
	boolean executeCommand(SenderWrapper player, String[] args) {
		if (!player.hasPermission("litenameless.cmd.base")) {
			player.sendMessage(core.config().getString("messages.permission"));
			return true;
		} else if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (player.hasPermission("litenameless.cmd.reload")) {
					core.config().reload();
					player.sendMessage(core.config().getString("messages.cmds.reload"));
				} else {
					player.sendMessage(core.config().getString("messages.permission"));
				}
			} else if (args[0].equalsIgnoreCase("setgroup")) {
				if (player.hasPermission("litenameless.cmd.setgroup")) {
					if (args.length > 2) {
						try {
							UUID target = core.env().getRegistry().getRegistration(UUIDResolver.class).resolveName(args[1], false);
							core.env().getRegistry().getRegistration(AsyncExecution.class).execute(() -> {
								core.setGroup(target, args[2]);
								player.sendMessage(core.config().getString("messages.cmds.setgroup.complete").replace("%TARGET%", args[1]).replace("%GROUP%", args[2]));
							});
						} catch (PlayerNotFoundException ex) {
							player.sendMessage(core.config().getString("messages.cmds.setgroup.invalid").replace("%TARGET%", args[1]));
						}
					} else {
						player.sendMessage(core.config().getString("messages.cmds.setgroup.usage"));
					}
				} else {
					player.sendMessage(core.config().getString("messages.permission"));
				}
			} else {
				usage(player);
			}
		} else {
			usage(player);
		}
		return true;
	}
	
	private void usage(SenderWrapper player) {
		player.sendMessage(core.config().getString("messages.usage"));
	}
	
}
