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
package org.hs.pforzheim.ti.ni;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.OpenNI.DepthGenerator;
import org.OpenNI.DepthMap;
import org.OpenNI.GeneralException;
import org.OpenNI.MapOutputMode;
import org.OpenNI.Point3D;
import org.OpenNI.StatusException;
import org.hs.pforzheim.ti.rlrc.Agent;

public class NI3d extends NI implements Runnable {
	
	private volatile boolean running;
	private int collectInstance = 0;
	
	private Point3D[] realPoints = null;
	
	private Semaphore lockReal;
	
	Thread t;
	
	public NI3d() {
		super();
		try {
			Logger.getLogger("rlrc").log(Level.INFO, "Initializing NI 3D...");
			if(depthGenerator == null)
				depthGenerator = DepthGenerator.create(context);
			
//			MapOutputMode[] mapOutputMode = depthGenerator.getSupportedMapOutputModes();
//			for(MapOutputMode mode : mapOutputMode) {
//				System.out.println(mode.getXRes() + " x " + mode.getYRes() + " @ " + mode.getFPS());
//			}
			
			MapOutputMode mapMode = new MapOutputMode(xRes, yRes, FPS);
			depthGenerator.setMapOutputMode(mapMode);
			
			context.setGlobalMirror(mirror);
			
			depthMetaData = depthGenerator.getMetaData();
			
			if(!depthGenerator.isGenerating())
				depthGenerator.startGenerating();
			
			lockReal = new Semaphore(1, true);
			
			logNodes();
		}
		catch (GeneralException e) {
			String log = "Initializing NI failed! " + e.getMessage();
			log += "\nTerminating!";
			Logger.getLogger("rlrc").log(Level.SEVERE, log);
			System.exit(-1);
		}
	}



	public Point3D[] getAndAcquireRealWorldPoints() {
		try {
			lockReal.acquire();
			return realPoints;
		}
		catch(InterruptedException e) {
			return null;
		}
	}

	public void releaseRealWorldPoints() {
		lockReal.release();
	}

	@Override
	public void run() {
		while(running) {
			
			try {
				lockReal.acquire();
				
				if(realPoints == null || realPoints.length != (xRes / FREQ) * (yRes / FREQ))
					realPoints = new Point3D[(xRes / FREQ) * (yRes / FREQ)];
				
				
				/* Agents */
				for(Agent agent : NICollector.agents) {
					agent.clearHits();
				}
				
				DepthMap map = depthMetaData.getData();
				int index = 0;
				for(int y = FREQ/2; y < yRes; y += FREQ) {
					for(int x = FREQ/2; x < xRes; x += FREQ) {
						try {
							int z = (int)map.readPixel(x, y);
							Point3D point = depthGenerator.convertProjectiveToRealWorld(new Point3D(x, y, z));
							realPoints[index] = point;
							index++;
							
							/* Check if Agents are hit */
							for(Agent agent : NICollector.agents) {
								float x1 = agent.getPosition().getX();
								float y1 = agent.getPosition().getY();
								float z1 = agent.getPosition().getZ();
								float size = agent.getSize() / 2;
								if(point.getX() > (x1 - size)	&& (point.getX() < (x1 + size))
									&& (point.getY() > (y1 - size)) && (point.getY() < (y1 + size))
									&& (point.getZ() > (z1 - size)) && (point.getZ() < (z1 + size))) {
									
									agent.hit();
								}
							}
							
						}
						catch(StatusException e) {
							Logger.getLogger("rlrc").log(Level.SEVERE, "Could not convert to real world! " + e.getMessage());
						}
					}
				}
//				lockContext.release();
				lockReal.release();
				
				Thread.sleep(100);
				
			}
			catch (Exception e) {
//				lockContext.release();
				lockReal.release();
				Logger.getLogger("rlrc").log(Level.SEVERE, e.getMessage());
			}
			
			
		}
	}
	
	public void startCollectingRealPoints() {
		collectInstance++;
		if(!running) {
			running = true;
			t = new Thread(this);
			t.start();
		}
	}

	public void stopCollectingRealPoints() {
		collectInstance--;
		if(collectInstance < 1) {
			running = false;
		}
	}

}
