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

import com.primesense.NITE.Direction;

public class GestureAgent extends Agent {
	
	public enum GESTURES {
		SWIPE_LEFT, SWIPE_RIGHT, SWIPE_DOWN, SWIPE_UP,
		CIRCLE_CLOCKWISE, CIRCLE_COUNTERCLOCKWISE,
		PUSH,
		NONE;
		
		public static GESTURES toEnum(String gesture){
			if(gesture.equals("SWIPE_LEFT"))
				return SWIPE_LEFT;
			else if(gesture.equals("SWIPE_RIGHT"))
				return SWIPE_RIGHT;
			else if(gesture.equals("SWIPE_DOWN"))
				return SWIPE_DOWN;
			else if(gesture.equals("SWIPE_UP"))
				return SWIPE_UP;
			else if(gesture.equals("CIRCLE_CLOCKWISE"))
				return CIRCLE_CLOCKWISE;
			else if(gesture.equals("CIRCLE_COUNTERCLOCKWISE"))
				return CIRCLE_COUNTERCLOCKWISE;
			else if(gesture.equals("PUSH"))
				return PUSH;
			else
				return NONE;
		}
		
		public static GESTURES getSwipeEnum(Direction direction) {
			return toEnum("SWIPE_" + direction);
		}
	}
	
	private GESTURES gesture;
	
	public GestureAgent(String gesture, String execString) {
		super(execString);
		
		this.gesture = GESTURES.toEnum(gesture);
		this.logInfo = "Gesture " + gesture + " detected: ";
	}
	
	public GestureAgent(String gesture, String execString, String comment) {
		super(execString, comment);
		this.gesture = GESTURES.toEnum(gesture);
	}
	
	public GESTURES getGesture() {
		return gesture;
	}
	
	
	
}
