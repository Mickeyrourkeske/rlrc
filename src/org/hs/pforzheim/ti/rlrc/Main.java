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

import java.awt.EventQueue;
import java.util.logging.Logger;

import org.hs.pforzheim.ti.ni.NICollector;



/**
 * @author schrob
 *
 */
public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public Main() {
		LOGGER.info("rlrc starting...");

		new NICollector();

//		new NISkeleton();
		
		NICollector.startNITracker();
		
		NICollector.readAgentsFromXML("agents.xml");
		
		NICollector.getNI3d().startCollectingRealPoints();
		
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					LOGGER.info("Starting main window");
					MainPanel window = new MainPanel();
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new Main();
	}
}
