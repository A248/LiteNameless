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

import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

import space.arim.nameless.api.PlayerWrapper;

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
