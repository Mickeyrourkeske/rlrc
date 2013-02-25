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
package org.hs.pforzheim.ti.rlrc.agent;

import org.OpenNI.Point3D;
import org.hs.pforzheim.ti.ni.NI;

/**
 * @author schrob
 *
 */
public class CubeAgent extends Agent {
	private static final int THRESHOLD = 200 / NI.FREQ;
	
	private Point3D position;
	private float size;
	private int hits;
	private boolean hit;
	
	public CubeAgent(Point3D position, float size, String execString) {
		super(execString);
		this.position = position;
		this.size = size;
		this.logInfo = "Cube at (" + position.getX() + "|" + position.getY() + "|" + position.getZ() + ") hit: "; 
	}
	
	public CubeAgent(Point3D position, float size, String execString, String comment) {
		super(execString, comment);
		this.position = position;
		this.size = size;

	}
	
	public Point3D getPosition() {
		return position;
	}
	
	public float getSize() {
		return size;
	}
	
	public void clearHits() {
		if(hits < THRESHOLD) {
			hit = false;						// Allow new program to start only if area was cleared before
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
