/**
 * 
 */
package org.hs.pforzheim.ti.rlrc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

import org.hs.pforzheim.ti.ni.NaturalInterface;

/**
 * @author schrob
 *
 */
public class rlrcPanel {
	private ObserverPanel observerPanel;
	private Observer3DPanel observer3dPanel;
	private JFrame frame;
	private NaturalInterface ni;

	private volatile boolean isRunning = false;
	
	private static GLCapabilities createGLCapabilities() {
        GLCapabilities capabilities = new GLCapabilities();
        capabilities.setRedBits(8);
        capabilities.setBlueBits(8);
        capabilities.setGreenBits(8);
        capabilities.setAlphaBits(8);
        return capabilities;
    }
	
	public rlrcPanel() {
		observerPanel = new ObserverPanel();
		//observer3dPanel = new Observer3DPanel(840, 600, createGLCapabilities());
		
		frame = new JFrame("Image Viewer");
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//TODO deamonize?
				System.out.println("window closing");
				close();
				System.exit(0);
			}
		});
		
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent key) { }
			
			@Override
			public void keyReleased(KeyEvent key) { }
			
			@Override
			public void keyPressed(KeyEvent key) {
				if(key.getKeyCode() == KeyEvent.VK_ESCAPE) {
					close();
				}
			}
		});
		
//		frame.getContentPane().add(observerPanel);
//		frame.getContentPane().add(observer3dPanel);
		
		//frame.setLayout(new FlowLayout());
		frame.add(this.observerPanel);
		//frame.add(this. );
		frame.pack();
		frame.setVisible(true);
		frame.setSize(this.observerPanel.getWidth(), this.observerPanel.getHeight());
		
		//observer3dPanel.requestFocus();
		
		
		this.run();
	}
	
	private void run() {
		isRunning = true;
		while(isRunning) {
			//observerPanel.updatePanel();
			observerPanel.repaint();
		}
		frame.dispose();
	}
	
	private void close() {
		isRunning = false;
	}
}
