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

import org.bukkit.command.CommandSender;

import space.arim.api.server.bukkit.SpigotUtil;

import space.arim.namelessplugin.api.SenderWrapper;

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
