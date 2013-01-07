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
package org.hs.pforzheim.ti.rlrc.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;


public class MainPanel {
	
	private JFrame frame;
	
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Create the application.
	 */
	public MainPanel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setSize(new Dimension(1280, 800));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		ObserverPanel observerPanel = new ObserverPanel();
		GridBagConstraints gbc_observerPanel = new GridBagConstraints();
		gbc_observerPanel.insets = new Insets(0, 0, 5, 5);
		gbc_observerPanel.gridx = 0;
		gbc_observerPanel.gridy = 0;
		frame.getContentPane().add(observerPanel, gbc_observerPanel);
		
		Observer3DPanel observer3DPanel = new Observer3DPanel();
		GridBagConstraints gbc_observer3DPanel = new GridBagConstraints();
		gbc_observer3DPanel.insets = new Insets(0, 0, 5, 0);
		gbc_observer3DPanel.gridx = 1;
		gbc_observer3DPanel.gridy = 0;
		frame.getContentPane().add(observer3DPanel, gbc_observer3DPanel);
		
		OutputPanel tptpnlAgentOutput = new OutputPanel();
		GridBagConstraints gbc_tptpnlAgentOutput = new GridBagConstraints();
		gbc_tptpnlAgentOutput.gridwidth = 2;
		gbc_tptpnlAgentOutput.insets = new Insets(0, 0, 5, 5);
		gbc_tptpnlAgentOutput.gridx = 0;
		gbc_tptpnlAgentOutput.gridy = 1;
		frame.getContentPane().add(tptpnlAgentOutput, gbc_tptpnlAgentOutput);
		
		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
