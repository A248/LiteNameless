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
package space.arim.nameless.core;

import space.arim.nameless.api.SenderWrapper;

class Commands {

	private final LiteNamelessCore core;
	
	Commands(LiteNamelessCore core) {
		this.core = core;
	}
	
	boolean executeCommand(SenderWrapper player, String[] args) {
		if (!player.hasPermission("litenameless.reload")) {
			permission(player);
		} else if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (player.hasPermission("litenameless.reload")) {
					core.config().reload();
					player.sendMessage(core.config().getString("messages.reload"));
				} else {
					permission(player);
				}
			} else {
				usage(player);
			}
		} else {
			usage(player);
		}
		return true;
	}
	
	private void permission(SenderWrapper player) {
		player.sendMessage(core.config().getString("messages.permission"));
	}
	
	private void usage(SenderWrapper player) {
		player.sendMessage(core.config().getString("messages.usage"));
	}
	
}
