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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.OpenNI.ActiveHandEventArgs;
import org.OpenNI.GeneralException;
import org.OpenNI.GestureGenerator;
import org.OpenNI.GestureRecognizedEventArgs;
import org.OpenNI.HandsGenerator;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.InactiveHandEventArgs;
import org.OpenNI.Point3D;
import org.OpenNI.StatusException;

import com.primesense.NITE.NullEventArgs;
import com.primesense.NITE.PointEventArgs;
import com.primesense.NITE.SessionManager;
import com.primesense.NITE.StringPointValueEventArgs;
import com.primesense.NITE.SwipeDetector;
import com.primesense.NITE.VelocityAngleEventArgs;

public class NITracker extends NI implements Runnable {

	private HandsGenerator handsGenerator;
	private GestureGenerator gestureGenerator;
	private SessionManager sessionManager;
	
	Thread t;

	public NITracker() {
		super();
		try {
			Logger.getLogger("rlrc").log(Level.INFO, "Initializing NI Tracker...");
			
			handsGenerator = HandsGenerator.create(context);
			handsGenerator.SetSmoothing(0.1f);											// 0 no smoothing; 1 infinite
			initHandEvents(handsGenerator);
			
			gestureGenerator = GestureGenerator.create(context);
			initGestureEvents(gestureGenerator);
			
			handsGenerator.startGenerating();
			gestureGenerator.startGenerating();
			
			sessionManager = new SessionManager(context, "Click","RaiseHand");			// Main focus gesture, quick refocus gesture
			initSessionEvents(sessionManager);
			
			logNodes();
			
			
			/* Config Gestures */
			SwipeDetector swipeDetector = swipeDetector();
			sessionManager.addListener(swipeDetector);
			
			t = new Thread(this);
			t.start();
		}
		catch(Exception e) {
			termintate(e);
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
	
	private void initGestureEvents(GestureGenerator gestureGenerator) throws StatusException {
		gestureGenerator.getGestureRecognizedEvent().addObserver(new IObserver<GestureRecognizedEventArgs>() {

			@Override
			public void update(IObservable<GestureRecognizedEventArgs> observable, GestureRecognizedEventArgs args) {
				String name = args.getGesture();
				Point3D pointid = args.getIdPosition();
				Point3D pointend = args.getEndPosition();
				
				Logger.getLogger("rlrc").log(Level.INFO, "Gesture " + name + " started at ("
								+ pointid.getX() + "|" + pointid.getY() + "|" + pointid.getZ() + " ended at ("
								+ pointend.getX() + "|" + pointend.getY() + "|" + pointend.getZ() + ")");
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
	
	private SwipeDetector swipeDetector() {
		SwipeDetector swipeDetector = null;
		try {
			swipeDetector = new SwipeDetector();
			Logger.getLogger("rlrc").log(Level.INFO, "Swipe min motion time: " + swipeDetector.getMotionTime() + "ms");
			
			swipeDetector.getSwipeLeftEvent().addObserver(new IObserver<VelocityAngleEventArgs>() {
				@Override
				public void update(IObservable<VelocityAngleEventArgs> observable, VelocityAngleEventArgs args) {
					Logger.getLogger("rlrc").log(Level.INFO, "Swipe Left");
				}
			});
		}
		catch(GeneralException e) {
			Logger.getLogger("rlrc").log(Level.WARNING, "No Swipe Detector! " + e.getMessage());
		}
		return swipeDetector;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				context.waitAndUpdateAll();
				sessionManager.update(context);
			}
		}
		catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
