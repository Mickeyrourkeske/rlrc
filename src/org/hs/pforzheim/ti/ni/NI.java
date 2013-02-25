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
import java.util.logging.Logger;

import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.DepthMetaData;
import org.OpenNI.GeneralException;
import org.OpenNI.GestureGenerator;
import org.OpenNI.HandsGenerator;
import org.OpenNI.ImageGenerator;
import org.OpenNI.ImageMetaData;
import org.OpenNI.NodeInfo;
import org.OpenNI.NodeInfoList;
import org.OpenNI.OutArg;
import org.OpenNI.ScriptNode;
import org.OpenNI.StatusException;
import org.OpenNI.UserGenerator;

/**
 * @author schrob
 *
 */
public class NI {

	protected static final Logger LOGGER = Logger.getLogger(NI.class.getName());
	
	protected static Context context = null;;
	protected static boolean mirror = false;

	public static final int xRes = 640;
	public static final int yRes = 480;
	protected static final int FPS = 30;
	public static final int FREQ = 4;			// 1, 2, 4, 8,..., 40, 80, 160 Has to be a factor of xRes and yRes
	
	protected static DepthGenerator depthGenerator = null;
	protected static ImageGenerator imageGenerator = null;
	protected static HandsGenerator handsGenerator = null;
	protected static GestureGenerator gestureGenerator = null;
	protected static UserGenerator userGenerator = null;
	
	protected static DepthMetaData depthMetaData = null;
	protected static ImageMetaData imageMetaData = null;
	
	
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
			LOGGER.info("Initializing NI...");
			try {
				context = new Context();
				
				scriptNode = new OutArg<ScriptNode>();
				context = Context.createFromXmlFile(XML_FILE, scriptNode);
			}
			catch (GeneralException e) {
				String log = "Initializing NI failed! " + e.getMessage();
				log += "\nTerminating!";
				LOGGER.severe(log);
				System.exit(1);
			}
		}
		if(lockUpdate == null) {
			lockUpdate = new Semaphore(1, true);
		}
	}
	
//	@Override
//	protected void finalize() throws Throwable {
//		LOGGER.info("finaliz");
//		instance--;
//		if(instance == 0) {
//			LOGGER.info("Releasing context...");
//			context.stopGeneratingAll();
//			context.release();
//		}
//		super.finalize();
//	}
	
	public void release() {
		instance--;
		if(instance == 0) {
			releaseAll();
		}
	}
	
	public static void releaseAll() {
		instance = 0;
		LOGGER.info("Releasing context...");
		try {
			context.stopGeneratingAll();
		}
		catch (StatusException e) {
			LOGGER.info("Stop generationg all failed...");
		}
		context.release();
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
			LOGGER.info(log);
		}
		catch (GeneralException e) {
			LOGGER.warning("No Nodes: " + e.getMessage());
		}
	}
	
	protected static void termintate(Exception e) {
		String log = "Initializing NI failed! " + e.getMessage();
		log += "\nTerminating!";
		LOGGER.severe(log);
		try {
			context.stopGeneratingAll();
		}
		catch (StatusException se) { }
		context.release();
		System.exit(1);
	}
}
