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

import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.DepthMetaData;
import org.OpenNI.GeneralException;
import org.OpenNI.ImageGenerator;
import org.OpenNI.ImageMetaData;
import org.OpenNI.License;
import org.OpenNI.NodeInfo;
import org.OpenNI.NodeInfoList;

/**
 * @author schrob
 *
 */
public class NI {

	protected static Context context = null;;
	protected static boolean mirror = false;

	public static final int xRes = 640;//320;		// 640
	public static final int yRes = 480;//240;		// 480
	protected static final int FPS = 25;
	public static final int FREQ = 4;			// 1, 2, 4, 8,..., 40, 80, 160 Has to be a factor of xRes and yRes
	
	
	protected static DepthMetaData depthMetaData = null;
	protected static ImageMetaData imageMetaData = null;
	
	protected static DepthGenerator depthGenerator = null;
	protected static ImageGenerator imageGenerator = null;
	
	private static Semaphore lockUpdate = null;;
	
	/**
	 * 
	 * @throws GeneralException
	 */
	public NI() {
		if(context == null) {
			Logger.getLogger("rlrc").log(Level.INFO, "Initializing NI...");
			try {
				context = new Context();
				
				//TODO License??? mit xml file? ganz weglassen. so nicht gut
				
				License license = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");
				context.addLicense(license);
			}
			catch (GeneralException e) {
				String log = "Initializing NI failed! " + e.getMessage();
				log += "\nTerminating!";
				Logger.getLogger("rlrc").log(Level.SEVERE, log);
				System.exit(-1);
			}
		}
		if(lockUpdate == null) {
			lockUpdate = new Semaphore(1, true);
		}
	}
	
	protected static void logNodes() {
		try {
			NodeInfoList a;
			a = context.enumerateExistingNodes();
			Iterator<NodeInfo> b = a.iterator();
			String log = "Existing Nodes:";
			while(b.hasNext()) {
				log += "\n\t" + b.next().getInstanceName();
			}
			Logger.getLogger("rlrc").log(Level.INFO, log);
		}
		catch (GeneralException e) {
			Logger.getLogger("rlrc").log(Level.WARNING, "No Nodes: " + e.getMessage());
		}
	}

	/**
	 * Not needed????
	 */
	@Deprecated
	protected static void updateAllContextData() {
		try {
			if(lockUpdate.tryAcquire()) {					// if first thread, how tries to update context
				//context.waitAndUpdateAll();
				lockUpdate.release();
			}
//			else {											// else just wait to finish
//				lockUpdate.acquire();
//				lockUpdate.release();
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
