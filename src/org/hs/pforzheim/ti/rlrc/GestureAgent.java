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
