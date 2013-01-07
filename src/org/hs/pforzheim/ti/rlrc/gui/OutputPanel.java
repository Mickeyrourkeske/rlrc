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
package org.hs.pforzheim.ti.rlrc.gui;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.Timer;

import org.hs.pforzheim.ti.rlrc.Agent;

/**
 * @author schrob
 *
 */
public class OutputPanel extends JLabel {
	
	private static final long serialVersionUID = 1L;
	
	float alpha;
	
	Timer timer;
	
	public OutputPanel() {
		Logger.getLogger(Agent.class.getName()).addHandler(new OutputHandler(this));
		
		timer = new Timer(100, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				float a = alpha - 0.05f;
				
				if(a < 0.01f) {
					a = 0.0f;
					timer.stop();
				}
				firePropertyChange("alpha", alpha, a);
				alpha = a;
				repaint();
			}
		});
		
		timer.setRepeats(true);
		timer.setCoalesce(true);
	}
	
	public void showOutput(String text) {
		this.setText(text);
		
		firePropertyChange("alpha", alpha, 1.0f);
		alpha = 1.0f;
		repaint();
		timer.start();
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		super.paint(g2d);
		g2d.dispose();
	}
	
	private class OutputHandler extends Handler {
		private OutputPanel outputPanel;
		
		public OutputHandler(OutputPanel outputPanel) {
			this.outputPanel = outputPanel;
		}

		@Override
		public void close() throws SecurityException { }

		@Override
		public void flush() { }

		@Override
		public void publish(LogRecord record) {
			if(isLoggable(record)) {
				outputPanel.showOutput(record.getMessage());
			}
		}
		
	}
}
