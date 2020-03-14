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
package space.arim.namelessplugin.api;

/**
 * A command sender which is not specific to BungeeCord, Spigot, or Sponge. <br>
 * <br>
 * Implementations do not need to be castable to {@link PlayerWrapper}.
 * If the LiteNameless API accepts a SenderWrapper, only SenderWrapper need be implemented. <br>
 * For example, on Spigot, this is <i>unnecessary</i>: <br>
 * <code>
 * CommandSender spigotSender = //...; <br>
 * SenderWrapper sender = (spigotSender instanceof Player) ? new PlayerWrapperImpl((Player) spigotSender) : new SenderWrapperImpl(spigotSender);
 * </code>
 * 
 * @author A248
 *
 */
public interface SenderWrapper {
	
	/**
	 * Whether the command sender has a specific permission.
	 * 
	 * @param permission the permission
	 * @return true if and only if the sender has the permission
	 */
	boolean hasPermission(String permission);
	
	/**
	 * Sends a message to the command sender. <br>
	 * <br>
	 * Implementations <b>MUST</b> parse colouring and styling codes before sending the message.
	 * The messages' formatting follows the '{@literal &}' formatting code character.
	 * 
	 * @param message the message with '{@literal &}' formatting codes
	 */
	void sendMessage(String message);
	
}
