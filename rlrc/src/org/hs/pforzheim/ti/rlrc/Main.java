/**
 * 
 */
package org.hs.pforzheim.ti.rlrc;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.OpenNI.GeneralException;
import org.hs.pforzheim.ti.ni.NaturalInterface;



/**
 * @author schrob
 *
 */
public class Main extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6982802587023929042L;
	
	private ObserverPanel observer;
	//TODO more panels for more look?
	/**
	 * @throws GeneralException 
	 * 
	 */
	public Main() throws GeneralException {
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		observer = new ObserverPanel(new NaturalInterface());
		container.add(observer);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				observer.close();
				super.windowClosing(e);
			}
		});
		
		pack();
		setResizable(true);
		setVisible(true);
		
	}
	/**
	 * @param args
	 * @throws GeneralException 
	 */
	public static void main(String[] args) throws GeneralException {
		new Main();
	}
}
