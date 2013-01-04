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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with RLRC.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.hs.pforzheim.ti.ni;

import java.util.ArrayList;

import org.hs.pforzheim.ti.rlrc.Agent;


/**
 * @author schrob
 *
 */
public final class NICollector {

//	public static NI ni = null;
	public static ArrayList<Agent> agents = null;
	
	private static NIVisual niVisual = null;
	private static NI3d ni3d = null;
	private static NITracker niTracker = null;
	

	public NICollector() {
		if(agents == null) {
			agents = new ArrayList<Agent>();
		}
	}
	
	public static NIVisual getNIVisual() {
		if(niVisual == null) {
			niVisual = new NIVisual();
		}
		return niVisual;
	}
	
	public static NI3d getNI3d() {
		if(ni3d == null) {
			ni3d = new NI3d();
		}
		return ni3d;
	}
	
	public static void startNITracker() {
		if(niTracker == null) {
			niTracker = new NITracker();
		}
	}
	
	public static NITracker getNiTracker() {
		return niTracker;
	}
	
	
}
