/**
 * 
 */
package org.hs.pforzheim.ti.rlrc;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.OpenNI.GeneralException;
import org.hs.pforzheim.ti.ni.NICollector;



/**
 * @author schrob
 *
 */
public class Main extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @throws GeneralException 
	 * 
	 */
	public Main() {
		Logger.getLogger("rlrc").log(Level.INFO, "rlrc starting...");
		
		new NICollector();
		
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					Logger.getLogger("rlrc").log(Level.INFO, "Starting main window");
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
	 * @throws GeneralException 
	 */
	public static void main(String[] args) throws GeneralException {
		new Main();
	}
}
