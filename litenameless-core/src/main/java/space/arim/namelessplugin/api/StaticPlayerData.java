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

import java.util.UUID;

public class StaticPlayerData {

	private final UUID uuid;
	private final String name;
	private final double x;
	private final double y;
	private final double z;
	private final String world;
	private final String addr;
	
	public StaticPlayerData(UUID uuid, String name, double x, double y, double z, String world, String addr) {
		this.uuid = uuid;
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.addr = addr;
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public double getLocationX() {
		return x;
	}
	
	public double getLocationY() {
		return y;
	}
	
	public double getLocationZ() {
		return z;
	}
	
	public String getLocationWorld() {
		return world;
	}
	
	public String getAddress() {
		return addr;
	}
	
}
