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

import java.util.HashMap;
import java.util.concurrent.Semaphore;

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
import org.hs.pforzheim.ti.rlrc.GestureAgent;

import com.primesense.NITE.CircleDetector;
import com.primesense.NITE.CircleEventArgs;
import com.primesense.NITE.DirectionVelocityAngleEventArgs;
import com.primesense.NITE.NoCircleEventArgs;
import com.primesense.NITE.NullEventArgs;
import com.primesense.NITE.PointEventArgs;
import com.primesense.NITE.PushDetector;
import com.primesense.NITE.SessionManager;
import com.primesense.NITE.StringPointValueEventArgs;
import com.primesense.NITE.SwipeDetector;
import com.primesense.NITE.VelocityAngleEventArgs;

public class NITracker extends NI implements Runnable {
	
	private SessionManager sessionManager;
	
	private SwipeDetector swipeDetector ;
	private CircleDetector circleDetector;
	private PushDetector pushDetector;
	
	Thread t;
	
	private HashMap<Integer, Point3D> hands;
	private Semaphore lockHand;
	
	private int circleTime = 0;

	public NITracker() {
		super();
		try {
			LOGGER.info("Initializing NI Tracker...");
			
			if(handsGenerator == null)
				handsGenerator = HandsGenerator.create(context);
			handsGenerator.SetSmoothing(0.1f);											// 0 no smoothing; 1 infinite
			initHandEvents(handsGenerator);
			
			if(gestureGenerator == null)
				gestureGenerator = GestureGenerator.create(context);
			initGestureEvents(gestureGenerator);
			
			if(!handsGenerator.isGenerating())
				handsGenerator.startGenerating();
			if(!gestureGenerator.isGenerating())
				gestureGenerator.startGenerating();
			
			sessionManager = new SessionManager(context, "Click","RaiseHand");			// Main focus gesture, quick refocus gesture
			initSessionEvents(sessionManager);
			
			hands = new HashMap<Integer, Point3D>();
			lockHand = new Semaphore(1, true);
			
			logNodes();
			
			
			/* Config Gestures */
			swipeDetector = swipeDetector();
			sessionManager.addListener(swipeDetector);
			
			circleDetector = circleDetector();
			sessionManager.addListener(circleDetector);
			
			pushDetector = pushDetector();
			sessionManager.addListener(pushDetector);
			
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
				lockHand.acquireUninterruptibly();
				hands.put(args.getId(), args.getPosition());
				lockHand.release();
			}
		});
		
		handsGenerator.getHandDestroyEvent().addObserver(new IObserver<InactiveHandEventArgs>() {
			
			@Override
			public void update(IObservable<InactiveHandEventArgs> observable, InactiveHandEventArgs args) {
				lockHand.acquireUninterruptibly();
				hands.remove(args.getId());
				lockHand.release();
			}
		});
		
		handsGenerator.getHandUpdateEvent().addObserver(new IObserver<ActiveHandEventArgs>() {
			
			@Override
			public void update(IObservable<ActiveHandEventArgs> observable, ActiveHandEventArgs args) {
				lockHand.acquireUninterruptibly();
				hands.put(args.getId(), args.getPosition());
				lockHand.release();
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
				
				LOGGER.info("Gesture " + name + " started at ("
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
				
				LOGGER.info("Session " + name + " focused at (" + point.getX() + "|" + point.getY() + "|" + point.getZ() + "), progress " + progress);
			}
		});
		
		sessionManager.getSessionStartEvent().addObserver(new IObserver<PointEventArgs>() {
			@Override
			public void update(IObservable<PointEventArgs> arg0observable, PointEventArgs args) {
				Point3D point = args.getPoint();
				
				LOGGER.info("Session started at (" + point.getX() + "|" + point.getY() + "|" + point.getZ() + ")");
			}
		});
		
		sessionManager.getSessionEndEvent().addObserver(new IObserver<NullEventArgs>() {

			@Override
			public void update(IObservable<NullEventArgs> observable, NullEventArgs args) {
				LOGGER.info("Session ended");
			}
		});
	}
	
	private SwipeDetector swipeDetector() {
		SwipeDetector swipeDetector = null;
		try {
			swipeDetector = new SwipeDetector();
			LOGGER.info("Swipe min motion time: " + swipeDetector.getMotionTime() + "ms");
			
			swipeDetector.getGeneralSwipeEvent().addObserver(new IObserver<DirectionVelocityAngleEventArgs>() {
				@Override
				public void update(IObservable<DirectionVelocityAngleEventArgs> obeservable, DirectionVelocityAngleEventArgs args) {
					LOGGER.info(args.getDirection() + ": v=" + args.getVelocity() + "m/s");
					for(GestureAgent agent : NICollector.gestureAgents) {
						String gesture = GestureAgent.SWIPE + args.getDirection();
						if(agent.getGesture().equals(gesture)) {
							agent.exec();
						}
					}
				}
			});
		}
		catch(GeneralException e) {
			LOGGER.warning("No swipe detector! " + e.getMessage());
		}
		return swipeDetector;
	}
	
	private CircleDetector circleDetector() {
		/* if time gets incremented, it means a clockwise turn; one turn means |time| = 1 */
		CircleDetector circleDetector = null;
		try {
			circleDetector = new CircleDetector();
			
			circleDetector.getCircleEvent().addObserver(new IObserver<CircleEventArgs>() {
				@Override
				public void update(IObservable<CircleEventArgs> obeservable, CircleEventArgs args) {
					
					int t = (int)(args.getTimes() * 100);
					if(t == 0) {
						circleTime = 0;
						LOGGER.info("Circle detected");
						/* Remove swipe detector */
						try {
							sessionManager.removeListener(swipeDetector);
						}
						catch (StatusException e) {
							LOGGER.warning("Could not remove swipe detector");
						}
					}
					else if(t % 25 == 0 && t != circleTime) {
						String gesture = "";
						if(t > circleTime) {
							gesture = GestureAgent.CIRCLE_CLOCKWISE;
						}
						else {
							gesture = GestureAgent.CIRCLE_COUNTERCLOCKWISE;
						}
						for(GestureAgent agent : NICollector.gestureAgents) {
							if(agent.getGesture().equals(gesture)) {
								agent.exec();
							}
						}
						circleTime = t;
					}
				}
			});
			
			circleDetector.getNoCircleEvent().addObserver(new IObserver<NoCircleEventArgs>() {
				
				@Override
				public void update(IObservable<NoCircleEventArgs> observable, NoCircleEventArgs args) {
					LOGGER.info("Circle detector done");
					/* Add swipe detector again */
					try {
						sessionManager.addListener(swipeDetector);
					}
					catch (StatusException e) {
						LOGGER.warning("Could not add swipe detector");
					}
				}
			});
		}
		catch(GeneralException e) {
			LOGGER.warning("No circle detector! " + e.getMessage());
			
		}
		return circleDetector;
	}
	
	private PushDetector pushDetector() {
		PushDetector pushDetector = null;
		try {
			pushDetector = new PushDetector();
			
			pushDetector.getPushEvent().addObserver(new IObserver<VelocityAngleEventArgs>() {
				
				@Override
				public void update(IObservable<VelocityAngleEventArgs> arg0, VelocityAngleEventArgs arg1) {
					for(GestureAgent agent : NICollector.gestureAgents) {
						if(agent.getGesture().equals(GestureAgent.PUSH)) {
							agent.exec();
						}
					}
				}
			});
		}
		catch(GeneralException e) {
			LOGGER.warning("No push detector! " + e.getMessage());
		}
		return pushDetector;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				context.waitOneUpdateAll(gestureGenerator);
				sessionManager.update(context);
			}
		}
		catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashMap<Integer, Point3D> getAndAcquireHands() {
		lockHand.acquireUninterruptibly();
		return hands;
	}
	
	public void releaseHands() {
		lockHand.release();
	}
}
