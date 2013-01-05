/**
 * This file is part of RLRC.
 * 
 * RLRC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RLRC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with RLRC. If not, see <http://www.gnu.org/licenses/>.
 */
package org.hs.pforzheim.ti.rlrc;

public class GestureAgent extends Agent {

	public static final String SWIPE = "SWIPE_";
	public static final String SWIPE_LEFT = SWIPE + "LEFT";
	public static final String SWIPE_RIGHT = "SWIPE_RIGHT";
	public static final String SWIPE_DOWN = "SWIPE_DOWN";
	public static final String SWIPE_UP = "SWIPE_UP";
	
	private String gesture;
	
	public GestureAgent(String gesture, String execString) {
		super(execString);
		this.gesture = gesture;
		this.logInfo = "Gesture " + gesture + " detected: ";
	}
	
	public String getGesture() {
		return gesture;
	}
}
