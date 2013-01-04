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
import org.OpenNI.NodeInfo;
import org.OpenNI.NodeInfoList;
import org.OpenNI.OutArg;
import org.OpenNI.ScriptNode;
import org.OpenNI.StatusException;

/**
 * @author schrob
 *
 */
public class NI {

	protected static Context context = null;;
	protected static boolean mirror = false;

	public static final int xRes = 640;//320;		// 640
	public static final int yRes = 480;//240;		// 480
	protected static final int FPS = 30;
	public static final int FREQ = 4;			// 1, 2, 4, 8,..., 40, 80, 160 Has to be a factor of xRes and yRes
	
	
	protected static DepthMetaData depthMetaData = null;
	protected static ImageMetaData imageMetaData = null;
	
	protected static DepthGenerator depthGenerator = null;
	protected static ImageGenerator imageGenerator = null;
	
	private static Semaphore lockUpdate = null;;
	
	private static volatile int instance = 0;

	private OutArg<ScriptNode> scriptNode;
	private static final String XML_FILE = "config.xml";
	/**
	 * 
	 * @throws GeneralException
	 */
	public NI() {
		instance++;
		if(context == null) {
			Logger.getLogger("rlrc").log(Level.INFO, "Initializing NI...");
			try {
				context = new Context();
				
				scriptNode = new OutArg<ScriptNode>();
				context = Context.createFromXmlFile(XML_FILE, scriptNode);
			}
			catch (GeneralException e) {
				String log = "Initializing NI failed! " + e.getMessage();
				log += "\nTerminating!";
				Logger.getLogger("rlrc").log(Level.SEVERE, log);
				System.exit(1);
			}
		}
		if(lockUpdate == null) {
			lockUpdate = new Semaphore(1, true);
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		instance--;
		
		if(instance == 0) {
			context.stopGeneratingAll();
			context.release();
		}
		super.finalize();
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
	
	protected static void termintate(Exception e) {
		String log = "Initializing NI failed! " + e.getMessage();
		log += "\nTerminating!";
		Logger.getLogger("rlrc").log(Level.SEVERE, log);
		try {
			context.stopGeneratingAll();
		}
		catch (StatusException se) { }
		context.release();
		System.exit(1);
	}

	/**
	 * Not needed????
	 */
	@Deprecated
	protected static void waitUpdateAllContextData() {
		try {
			if(lockUpdate.tryAcquire()) {					// if first thread, how tries to update context
				System.out.println("waitandupdate");
				context.waitAndUpdateAll();
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
