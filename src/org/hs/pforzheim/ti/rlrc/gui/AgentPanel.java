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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.OpenNI.Point3D;
import org.hs.pforzheim.ti.rlrc.Collector;
import org.hs.pforzheim.ti.rlrc.agent.CubeAgent;
import org.hs.pforzheim.ti.rlrc.agent.GestureAgent;
import org.hs.pforzheim.ti.rlrc.agent.GestureAgent.GESTURES;

public class AgentPanel extends JPanel {
	
	private static final Logger LOGGER = Logger.getLogger(AgentPanel.class.getName());

	
	private static final long serialVersionUID = 1L;
	
	private JPanel cubePanel;
	private JPanel gesturePanel;
	
	private int cubeRow;
	private int gestureRow;

	/**
	 * Create the panel.
	 */
	public AgentPanel() {
		setMinimumSize(new Dimension(1280, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
//		gridBagLayout.columnWidths = new int[]{640, 640};
//		gridBagLayout.rowHeights = new int[]{500, 1};
		gridBagLayout.columnWeights = new double[]{1,1};//{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0, 1};//{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		cubePanel = new JPanel();
		cubePanel.setMinimumSize(new Dimension(640, 10));
		GridBagConstraints gbc_cubePanel = new GridBagConstraints();
//		gbc_cubePanel.insets = new Insets(0, 0, 0, 5);
//		gbc_cubePanel.fill = GridBagConstraints.BOTH;
		gbc_cubePanel.gridx = 0;
		gbc_cubePanel.gridy = 0;
		gbc_cubePanel.weighty = 1;
		gbc_cubePanel.weightx = 1;
		gbc_cubePanel.anchor = GridBagConstraints.NORTH;
		add(cubePanel, gbc_cubePanel);
		GridBagLayout gbl_cubePanel = new GridBagLayout();
		gbl_cubePanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_cubePanel.rowHeights = new int[]{0};
		gbl_cubePanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
//		gbl_cubePanel.rowWeights = new double[]{0, 0, 0, 0, 0, 1};//0.0, 0.0, 0.0, Double.MIN_VALUE};
		cubePanel.setLayout(gbl_cubePanel);
		
		
		gesturePanel = new JPanel();
		gesturePanel.setMinimumSize(new Dimension(640, 10));
		GridBagConstraints gbc_gesturePanel = new GridBagConstraints();
//		gbc_gesturePanel.fill = GridBagConstraints.BOTH;
		gbc_gesturePanel.gridx = 1;
		gbc_gesturePanel.gridy = 0;
		gbc_gesturePanel.weighty = 1;
		gbc_gesturePanel.weightx = 1;
		gbc_gesturePanel.anchor = GridBagConstraints.NORTH;
		add(gesturePanel, gbc_gesturePanel);
		GridBagLayout gbl_gesturePanel = new GridBagLayout();
		gbl_gesturePanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_gesturePanel.rowHeights = new int[]{0};
		gbl_gesturePanel.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
//		gbl_gesturePanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gesturePanel.setLayout(gbl_gesturePanel);
		
		
		initCubeRow();
		
		for(cubeRow = 0; cubeRow < Collector.cubeAgents.size(); cubeRow++) {
			CubeAgent agent = Collector.cubeAgents.get(cubeRow);
			createCubeLine(true, agent.getPosition(), agent.getCommand(), agent.getComment(), cubeRow + 2);
		}
		cubeRow++;
		createCubeLine(false, new Point3D(0, 0, 0), "", "", cubeRow + 2);
		
		
		initGestureRow();
		
		
		for(gestureRow = 0; gestureRow < Collector.gestureAgents.size(); gestureRow++) {
			GestureAgent agent = Collector.gestureAgents.get(gestureRow);
			createGestureLine(true, agent.getGesture(), agent.getCommand(), agent.getComment(), gestureRow + 2);
		}
		gestureRow++;
		createGestureLine(false, GESTURES.NONE, "", "", gestureRow + 2);
		
		
//		JButton plusButton = new JButton("+");
//		plusButton.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		cubePanel.add(plusButton);
		
		
		JPanel buttonPanel = new JPanel();
		
		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LOGGER.info("Apply new Agent list...");
				//TODO
				
			}
		});
		buttonPanel.add(applyButton);
		
		JButton cancelButton = new JButton("Cancel");
		applyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LOGGER.info("Remove new Agent list...");
				// TODO
				
			}
		});
		buttonPanel.add(cancelButton);
		
		gbc_gesturePanel.gridx = 1;
		gbc_gesturePanel.gridy = 1;
		gbc_gesturePanel.weighty = 1;
		gbc_gesturePanel.weightx = 1;
		gbc_gesturePanel.anchor = GridBagConstraints.EAST;
		add(buttonPanel, gbc_gesturePanel);

	}

	private void initGestureRow() {
		
		JLabel lblGestures = new JLabel("Gestures");
		lblGestures.setFont(new Font("Dialog", Font.BOLD, 16));
		GridBagConstraints gbc_lblGestures = new GridBagConstraints();
		gbc_lblGestures.gridwidth = 4;
		gbc_lblGestures.insets = new Insets(0, 0, 5, 0);
		gbc_lblGestures.gridx = 0;
		gbc_lblGestures.gridy = 0;
		gesturePanel.add(lblGestures, gbc_lblGestures);
		
		JLabel lblGesture = new JLabel("Gesture");
		GridBagConstraints gbc_lblGesture = new GridBagConstraints();
		gbc_lblGesture.insets = new Insets(0, 0, 5, 5);
		gbc_lblGesture.gridx = 1;
		gbc_lblGesture.gridy = 1;
		gesturePanel.add(lblGesture, gbc_lblGesture);
		
		JLabel lblCommand = new JLabel("Command");
		GridBagConstraints gbc_lblCommand = new GridBagConstraints();
		gbc_lblCommand.insets = new Insets(0, 0, 5, 5);
		gbc_lblCommand.gridx = 2;
		gbc_lblCommand.gridy = 1;
		gesturePanel.add(lblCommand, gbc_lblCommand);
		
		JLabel lblComment = new JLabel("Comment");
		GridBagConstraints gbc_lblComment = new GridBagConstraints();
		gbc_lblComment.insets = new Insets(0, 0, 5, 0);
		gbc_lblComment.gridx = 3;
		gbc_lblComment.gridy = 1;
		gesturePanel.add(lblComment, gbc_lblComment);
	}

	private void initCubeRow() {
		JLabel lblCubes = new JLabel("Cubes");
		lblCubes.setFont(new Font("Dialog", Font.BOLD, 16));
		GridBagConstraints gbc_lblCubes = new GridBagConstraints();
		gbc_lblCubes.gridwidth = 6;
		gbc_lblCubes.insets = new Insets(0, 0, 5, 0);
		gbc_lblCubes.gridx = 0;
		gbc_lblCubes.gridy = 0;
		cubePanel.add(lblCubes, gbc_lblCubes);
		
		JLabel lblX = new JLabel("x");
		GridBagConstraints gbc_lblX = new GridBagConstraints();
		gbc_lblX.insets = new Insets(0, 0, 5, 5);
		gbc_lblX.gridx = 1;
		gbc_lblX.gridy = 1;
		cubePanel.add(lblX, gbc_lblX);
		
		JLabel lblY = new JLabel("y");
		GridBagConstraints gbc_lblY = new GridBagConstraints();
		gbc_lblY.insets = new Insets(0, 0, 5, 5);
		gbc_lblY.gridx = 2;
		gbc_lblY.gridy = 1;
		cubePanel.add(lblY, gbc_lblY);
		
		JLabel lblZ = new JLabel("z");
		GridBagConstraints gbc_lblZ = new GridBagConstraints();
		gbc_lblZ.insets = new Insets(0, 0, 5, 5);
		gbc_lblZ.gridx = 3;
		gbc_lblZ.gridy = 1;
		cubePanel.add(lblZ, gbc_lblZ);
		
		JLabel lblCommand = new JLabel("Command");
		GridBagConstraints gbc_lblCommand = new GridBagConstraints();
		gbc_lblCommand.insets = new Insets(0, 0, 5, 5);
		gbc_lblCommand.gridx = 4;
		gbc_lblCommand.gridy = 1;
		cubePanel.add(lblCommand, gbc_lblCommand);
		
		JLabel lblComment = new JLabel("Comment");
		GridBagConstraints gbc_lblComment = new GridBagConstraints();
		gbc_lblComment.insets = new Insets(0, 0, 5, 0);
		gbc_lblComment.gridx = 5;
		gbc_lblComment.gridy = 1;
		cubePanel.add(lblComment, gbc_lblComment);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createGestureLine(boolean enabled, GESTURES gesture, String command, String comment, int row) {
		JCheckBox checkEnabled = new JCheckBox("");
		checkEnabled.setSelected(enabled);
		GridBagConstraints gbc_checkEnabled = new GridBagConstraints();
		gbc_checkEnabled.insets = new Insets(0, 0, 0, 5);
		gbc_checkEnabled.gridx = 0;
		gbc_checkEnabled.gridy = row;
		gesturePanel.add(checkEnabled, gbc_checkEnabled);
		
		JComboBox comboGesture = new JComboBox();
		comboGesture.setModel(new DefaultComboBoxModel(GESTURES.values()));
		comboGesture.setSelectedItem(gesture);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = row;
		gesturePanel.add(comboGesture, gbc_comboBox);
		
		JTextField textCommand = new JTextField(command);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 0, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = row;
		gesturePanel.add(textCommand, gbc_textField);
		textCommand.setColumns(10);
		
		JTextField textComment = new JTextField(comment);
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 0, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 3;
		gbc_textField_1.gridy = row;
		gesturePanel.add(textComment, gbc_textField_1);
		textComment.setColumns(10);
	}

	private void createCubeLine(boolean enabled, Point3D position, String command, String comment, int row) {
		JCheckBox checkEnabled = new JCheckBox("");
		checkEnabled.setSelected(enabled);
		GridBagConstraints gbc_checkEnabled = new GridBagConstraints();
		gbc_checkEnabled.insets = new Insets(0, 0, 0, 5);
		gbc_checkEnabled.gridx = 0;
		gbc_checkEnabled.gridy = row;
		cubePanel.add(checkEnabled, gbc_checkEnabled);
		
		JSpinner xSpinner = new JSpinner();
		xSpinner.setModel(new SpinnerNumberModel((int) position.getX(), -9000, 9000, 10));
		GridBagConstraints gbc_xSpinner = new GridBagConstraints();
		gbc_xSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_xSpinner.gridx = 1;
		gbc_xSpinner.gridy = row;
		cubePanel.add(xSpinner, gbc_xSpinner);
		
		JSpinner ySpinner = new JSpinner();
		ySpinner.setModel(new SpinnerNumberModel((int) position.getY(), -9000, 9000, 10));
		GridBagConstraints gbc_ySpinner = new GridBagConstraints();
		gbc_ySpinner.insets = new Insets(0, 0, 0, 5);
		gbc_ySpinner.gridx = 2;
		gbc_ySpinner.gridy = row;
		cubePanel.add(ySpinner, gbc_ySpinner);
		
		JSpinner zSpinner = new JSpinner();
		zSpinner.setModel(new SpinnerNumberModel((int) position.getZ(), 0, 10000, 10));
		GridBagConstraints gbc_zSpinner = new GridBagConstraints();
		gbc_zSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_zSpinner.gridx = 3;
		gbc_zSpinner.gridy = row;
		cubePanel.add(zSpinner, gbc_zSpinner);
		
		JTextField textCommand = new JTextField(command);
		GridBagConstraints gbc_execText = new GridBagConstraints();
		gbc_execText.insets = new Insets(0, 0, 0, 5);
		gbc_execText.fill = GridBagConstraints.HORIZONTAL;
		gbc_execText.gridx = 4;
		gbc_execText.gridy = row;
		cubePanel.add(textCommand, gbc_execText);
		textCommand.setColumns(10);
		
		JTextField textComment = new JTextField(comment);
		GridBagConstraints gbc_commentText = new GridBagConstraints();
		gbc_commentText.insets = new Insets(0, 0, 0, 5);
		gbc_commentText.fill = GridBagConstraints.HORIZONTAL;
		gbc_commentText.gridx = 5;
		gbc_commentText.gridy = row;
		cubePanel.add(textComment, gbc_commentText);
		textComment.setColumns(10);
		
	}

}
