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

import org.spongepowered.api.command.CommandSource;

import space.arim.api.platform.sponge.SpongeMessages;

import space.arim.namelessplugin.api.SenderWrapper;

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
		sender.sendMessage(SpongeMessages.get().colour(message));
	}
	
}
