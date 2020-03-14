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

import net.md_5.bungee.api.connection.ProxiedPlayer;

import space.arim.nameless.api.PlayerWrapper;

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
