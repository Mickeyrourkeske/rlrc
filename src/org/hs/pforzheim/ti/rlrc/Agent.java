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
package org.hs.pforzheim.ti.rlrc;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.OpenNI.Point3D;
import org.hs.pforzheim.ti.ni.NI;

/**
 * @author schrob
 *
 */
public class Agent {
	private static final int THRESHOLD = 200 / NI.FREQ;
	
	private Point3D position;
	private float size;
	private String execString;
	private int executions;
	private int hits;
	private boolean hit;
	
	public Agent(Point3D position, float size, String execString) {
		this.position = position;
		this.size = size;
		this.execString = execString;
		this.executions = 0;
	}
	
	public Point3D getPosition() {
		return position;
	}
	
	public float getSize() {
		return size;
	}
	
	public int getExecutions() {
		return executions;
	}
	
	public void exec() {
		executions++;
		try {
			Logger.getLogger("rlrc").log(Level.INFO, "Program " + execString + " starting... ");
			Runtime.getRuntime().exec(execString);
		}
		catch (IOException e) {
			Logger.getLogger("rlrc").log(Level.WARNING, "Program " + execString + " could not be started! " + e.getMessage());
		}
	}
	
	public void clearHits() {
		if(hits < THRESHOLD) {
			hit = false;						// Allow new program start only if area was cleared before
		}
		hits = 0;
	}
	
	public void hit() {
		hits++;
		if(hits == THRESHOLD) {
			if(!hit) {							// Start program only if not started before
				hit = true;
				this.exec();
			}
		}
	}
	
	public boolean isHit() {
		return hit;
	}
}
