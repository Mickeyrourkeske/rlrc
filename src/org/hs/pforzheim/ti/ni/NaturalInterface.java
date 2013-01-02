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
 * along with RLRC. If not, see <http://www.gnu.org/licenses/>.
 */
package org.hs.pforzheim.ti.ni;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.OpenNI.ActiveHandEventArgs;
import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.DepthMap;
import org.OpenNI.DepthMetaData;
import org.OpenNI.GeneralException;
import org.OpenNI.GestureGenerator;
import org.OpenNI.HandsGenerator;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.ImageGenerator;
import org.OpenNI.ImageMetaData;
import org.OpenNI.InactiveHandEventArgs;
import org.OpenNI.License;
import org.OpenNI.MapOutputMode;
import org.OpenNI.Point3D;
import org.OpenNI.StatusException;
import org.hs.pforzheim.ti.rlrc.Agent;

import com.primesense.NITE.NullEventArgs;
import com.primesense.NITE.PointEventArgs;
import com.primesense.NITE.SessionManager;
import com.primesense.NITE.StringPointValueEventArgs;

/**
 * @author schrob
 *
 */
public class NaturalInterface extends Thread {

	private Context context;
	private boolean mirror = false;

	private static final int xRes = 640;
	private static final int yRes = 480;
	private static final int FPS = 30;
	public static final int FREQ = 4;			// ..., 40, 80, 160 Has to be a factor of xRes and yRes
	
	private DepthMetaData depthMetaData;
	private ImageMetaData imageMetaData;
//	private SceneMetaData sceneMetaData;
	
	private DepthGenerator depthGenerator;
	private ImageGenerator imageGenerator;
	private HandsGenerator handsGenerator;
	private GestureGenerator gestureGenerator;
//	private UserGenerator userGenerator;
	
	private SessionManager sessionManager;
	
	private volatile boolean running;
	private int collectInstance = 0;
	
	private Point3D[] realPoints = null;
	
	private Semaphore lockReal;
	private Semaphore lockContext;
	/**
	 * 
	 * @throws GeneralException
	 */
	public NaturalInterface() {
		Logger.getLogger("rlrc").log(Level.INFO, "Initializing NI...");
		try {
			context = new Context();
			
			//TODO License??? mit xml file? ganz weglassen. so nicht gut
			
			License license = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");
			context.addLicense(license);
			
			
			depthGenerator = DepthGenerator.create(context);
			imageGenerator = ImageGenerator.create(context);
			handsGenerator = HandsGenerator.create(context);
//			gestureGenerator = GestureGenerator.create(context);
//			userGenerator = UserGenerator.create(context);
			
			handsGenerator.SetSmoothing(0.1f);						// 0 no smoothing; 1 infinite
			initHandEvents(handsGenerator);
			
			
			MapOutputMode mapMode = new MapOutputMode(xRes, yRes, FPS);
			depthGenerator.setMapOutputMode(mapMode);
			imageGenerator.setMapOutputMode(mapMode);
			
			context.setGlobalMirror(mirror);
			
			depthMetaData = depthGenerator.getMetaData();
			imageMetaData = imageGenerator.getMetaData();
//			sceneMetaData = userGenerator.getUserPixels(0);
			
			//TODO generate more maps, scene, sceleton
			
			imageMetaData.setFullXRes(xRes);
			imageMetaData.setFullYRes(yRes);
			
			context.startGeneratingAll();
			
			sessionManager = new SessionManager(context, "Click","RaiseHand");
			initSessionEvents(sessionManager);
			
			Logger.getLogger("rlrc").log(Level.INFO, "NI succesfully initialized");
			
			lockReal = new Semaphore(1, true);
			lockContext = new Semaphore(1, true);
		}
		catch (GeneralException e) {
			Logger.getLogger("rlrc").log(Level.SEVERE, "Initializing NI failed! " + e.getMessage());
			Logger.getLogger("rlrc").log(Level.SEVERE, "Terminating!");
			System.exit(-1);
		}
	}
	
	private void initHandEvents(HandsGenerator handsGenerator) throws StatusException {
		handsGenerator.getHandCreateEvent().addObserver(new IObserver<ActiveHandEventArgs>() {
			
			@Override
			public void update(IObservable<ActiveHandEventArgs> observable, ActiveHandEventArgs args) {
				int id = args.getId();
				Point3D point = args.getPosition();
				float time = args.getTime();
				
				Logger.getLogger("rlrc").log(Level.INFO, "Hand " + id + " detected at (" + point.getX() + "|" + point.getY() + "|" + point.getZ() + "), at " + time + "s");
				
			}
		});
		
		
		handsGenerator.getHandDestroyEvent().addObserver(new IObserver<InactiveHandEventArgs>() {
			
			@Override
			public void update(IObservable<InactiveHandEventArgs> observable, InactiveHandEventArgs args) {
				int id = args.getId();
				float time = args.getTime();
				
				Logger.getLogger("rlrc").log(Level.INFO, "Hand " + id + " destroyed at " + time + "s");
			}
		});
	}
	
	private void initSessionEvents(SessionManager sessionManager) throws StatusException {
		sessionManager.getSessionFocusProgressEvent().addObserver(new IObserver<StringPointValueEventArgs>() {
			@Override
			public void update(IObservable<StringPointValueEventArgs> observable, StringPointValueEventArgs args) {
				Point3D point = args.getPoint();
				float progress = args.getValue();
				String name = args.getName();
				
				Logger.getLogger("rlrc").log(Level.INFO, "Session " + name + " focused at (" + point.getX() + "|" + point.getY() + "|" + point.getZ() + "), progress " + progress);
			}
		});
		
		sessionManager.getSessionStartEvent().addObserver(new IObserver<PointEventArgs>() {
			@Override
			public void update(IObservable<PointEventArgs> arg0observable, PointEventArgs args) {
				Point3D point = args.getPoint();

				Logger.getLogger("rlrc").log(Level.INFO, "Session started at (" + point.getX() + "|" + point.getY() + "|" + point.getZ() + ")");
			}
		});
		
		sessionManager.getSessionEndEvent().addObserver(new IObserver<NullEventArgs>() {

			@Override
			public void update(IObservable<NullEventArgs> observable, NullEventArgs args) {
				Logger.getLogger("rlrc").log(Level.INFO, "Session ended");
			}
		});
	}
	
	public NIImage depthImage() {
		try {
			lockContext.acquire();
			
			byte[] image = new byte[depthMetaData.getFullXRes() * depthMetaData.getFullYRes()];
			
//			context.waitAnyUpdateAll();
			depthGenerator.waitAndUpdateData();
			
			ShortBuffer buffer = depthMetaData.getData().createShortBuffer();
			
			lockContext.release();
			
			float[] histogram = calcHistogram(buffer);
			
			while(buffer.remaining() > 0) {
				int pos = buffer.position();
				short pixel = buffer.get();
				image[pos] = (byte)histogram[pixel];
			}
	
			return new NIImage(image, depthMetaData.getFullXRes(), depthMetaData.getFullYRes());
			
		}
		catch(Exception e) {
			lockContext.release();
			Logger.getLogger("rlrc").log(Level.SEVERE, "Depthimage failed! " + e.getMessage());
			return null;
		}
	}
	
	public NIImage colorImage() {
		try {
			lockContext.acquire();
			byte[] image = new byte[imageMetaData.getFullXRes() * imageMetaData.getFullYRes() * 3];
			
//			context.waitAnyUpdateAll();
			imageGenerator.waitAndUpdateData();
			
			ByteBuffer buffer = imageMetaData.getData().createByteBuffer();
			lockContext.release();
			while(buffer.remaining() > 0) {
				int pos = buffer.position();
				byte pixel = buffer.get();
				image[pos] = pixel;
			}
			
			return new NIImage(image, imageMetaData.getFullXRes(), imageMetaData.getFullYRes());
			
		}
		catch(Exception e) {
			lockContext.release();
			Logger.getLogger("rlrc").log(Level.SEVERE, "Colorimage failed! " + e.getMessage());
			return null;
		}
		
	}
	
	private float []calcHistogram(ShortBuffer depthBuffer) {
		int MAX_DEPTH_SIZE = 10000;
		float[] histogram = new float[MAX_DEPTH_SIZE];
	
		int numPoints = 0; 
		int maxDepth = 0;
		
		// a depth (an integer mm value) is used as an index  
		// into the array 
		while(depthBuffer.remaining() > 0) { 
			short depthVal = depthBuffer.get(); 
			if(depthVal > maxDepth) 
				maxDepth = depthVal; 
			if((depthVal != 0)  && (depthVal < MAX_DEPTH_SIZE)) { 
				histogram[depthVal]++; 
				numPoints++; 
			} 
		} 
		
		// convert into a cummulative depth count (skipping histogram[0]) 
		for (int i = 1; i <= maxDepth; i++)    // stage 3 
			histogram[i] += histogram[i - 1]; 
		
		// convert cummulative depth into integers (0-255)
		if (numPoints > 0) { 
			for (int i = 1; i <= maxDepth; i++)   // skip histogram[0] 
				histogram[i] = (int) (256 * (1.0f - (histogram[i] / (float) numPoints))); 
		}
		depthBuffer.rewind();
		
		return histogram;
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
		super.run();
		while(running) {
			
			try {
//				handsGenerator.waitAndUpdateData();
				sessionManager.update(context);
				
				
				lockReal.acquire();
				lockContext.acquire();
				
//				context.waitAnyUpdateAll();
				
//				context.waitOneUpdateAll(depthGenerator);
				depthGenerator.waitAndUpdateData();
				
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
				lockContext.release();
				lockReal.release();
				
				Thread.sleep(100);
				
			}
			catch (Exception e) {
				lockContext.release();
				lockReal.release();
				Logger.getLogger("rlrc").log(Level.SEVERE, e.getMessage());
			}
			
			
		}
	}
	
	public void startCollectingRealPoints() {
		collectInstance++;
		if(!running) {
			running = true;
			this.start();
		}
	}

	public void stopCollectingRealPoints() {
		collectInstance--;
		if(collectInstance < 1) {
			running = false;
		}
	}

	
}
