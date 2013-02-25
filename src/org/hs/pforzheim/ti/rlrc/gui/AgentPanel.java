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
import java.util.ArrayList;
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
	
	private class CubeBox {
		public JCheckBox checkEnabled;
		public JSpinner xSpinner;
		public JSpinner ySpinner;
		public JSpinner zSpinner;
		public JSpinner sizeSpinner;
		public JTextField textCommand;
		public JTextField textComment;
	}
	
	private class GestureBox {
		public JCheckBox checkEnabled;
		public JComboBox<GESTURES> comboGesture;
		public JTextField textCommand;
		public JTextField textComment;
	}
	
	ArrayList<CubeBox> cubeBoxes;
	ArrayList<GestureBox> gestureBoxes;
	
	

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
		
		
		initAgents();
		
		JPanel buttonPanel = new JPanel();
		
		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String out = "Apply new Agent list...\n";
				
				Collector.cubeAgents.clear();
				
				out += "\tC U B E S\n";
				for(CubeBox box : cubeBoxes) {
					boolean enabled = box.checkEnabled.isSelected();
					float x = ((Number)box.xSpinner.getValue()).floatValue();
					float y = ((Number)box.ySpinner.getValue()).floatValue();
					float z = ((Number)box.zSpinner.getValue()).floatValue();
					Point3D point = new Point3D(x, y, z);
					float size = ((Number)box.sizeSpinner.getValue()).floatValue();
					String command = box.textCommand.getText();
					String comment = box.textComment.getText();
					
					out += "\t\t" + enabled;
					out += "; x: " + x;
					out += "; y: " + y;
					out += "; z: " + z;
					out += "; Size: " + size;
					out += "; Command: " + command;
					out += "; Comment: " + comment;
					out += "\n";
					
					if(enabled) {
						CubeAgent agent = new CubeAgent(point, size, command, comment);
						Collector.cubeAgents.add(agent);
					}
					
				}
				
				Collector.gestureAgents.clear();
				
				out += "\tG E S T U R E S\n";
				for(GestureBox box : gestureBoxes) {
					boolean enabled = box.checkEnabled.isSelected();
					GESTURES gesture = box.comboGesture.getItemAt(box.comboGesture.getSelectedIndex());
					String command = box.textCommand.getText();
					String comment = box.textComment.getText();
					
					out += "\t\t" + enabled;
					out += "; Gesture: " + gesture;
					out += "; Command: " + command;
					out += "; Comment: " + comment;
					out += "\n";
					
					if(enabled) {
						GestureAgent agent = new GestureAgent(gesture.name(), command, comment);
						Collector.gestureAgents.add(agent);
					}
				}
				
				LOGGER.info(out);
				
				remove(cubePanel);
				remove(gesturePanel);
				
				initAgents();
				
			}
		});
		buttonPanel.add(applyButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LOGGER.info("Remove new Agent list...");
				
				remove(cubePanel);
				remove(gesturePanel);
				
				initAgents();
				
				
			}
		});
		buttonPanel.add(cancelButton);

		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.gridx = 1;
		gbc_buttonPanel.gridy = 1;
		gbc_buttonPanel.weighty = 1;
		gbc_buttonPanel.weightx = 1;
		gbc_buttonPanel.anchor = GridBagConstraints.EAST;
		add(buttonPanel, gbc_buttonPanel);

	}

	
	private void initAgents() {

		cubePanel = new JPanel();
		cubePanel.setMinimumSize(new Dimension(640, 10));
		GridBagConstraints gbc_cubePanel = new GridBagConstraints();
//		gbc_cubePanel.insets = new Insets(0, 0, 0, 5);
//		gbc_cubePanel.fill = GridBagConstraints.BOTH;
		gbc_cubePanel.gridx = 0;
		gbc_cubePanel.gridy = 0;
		gbc_cubePanel.weighty = 1;
		gbc_cubePanel.weightx = 1;
		gbc_cubePanel.anchor = GridBagConstraints.WEST;
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
		gbc_gesturePanel.anchor = GridBagConstraints.EAST;
		add(gesturePanel, gbc_gesturePanel);
		GridBagLayout gbl_gesturePanel = new GridBagLayout();
		gbl_gesturePanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_gesturePanel.rowHeights = new int[]{0};
		gbl_gesturePanel.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
//		gbl_gesturePanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gesturePanel.setLayout(gbl_gesturePanel);

		/* Create GUI interface for the cubes */
		initCubeRow();
		cubeBoxes = new ArrayList<CubeBox>();
		
		for(cubeRow = 0; cubeRow < Collector.cubeAgents.size(); cubeRow++) {
			CubeAgent agent = Collector.cubeAgents.get(cubeRow);
			cubeBoxes.add(createCubeLine(true, agent.getPosition(), agent.getSize(), agent.getCommand(), agent.getComment(), cubeRow + 2));
		}
		cubeRow++;
		cubeBoxes.add(createCubeLine(false, new Point3D(0, 0, 0), 0, "", "", cubeRow + 2));
		
		
		/* Create GUI interface for the gestures */
		initGestureRow();
		gestureBoxes = new ArrayList<GestureBox>();
		
		for(gestureRow = 0; gestureRow < Collector.gestureAgents.size(); gestureRow++) {
			GestureAgent agent = Collector.gestureAgents.get(gestureRow);
			gestureBoxes.add(createGestureLine(true, agent.getGesture(), agent.getCommand(), agent.getComment(), gestureRow + 2));
		}
		gestureRow++;
		gestureBoxes.add(createGestureLine(false, GESTURES.NONE, "", "", gestureRow + 2));
		
		

		revalidate();
		repaint();
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
		
		JLabel lblSize = new JLabel("Size");
		GridBagConstraints gbc_lblSize = new GridBagConstraints();
		gbc_lblSize.insets = new Insets(0, 0, 5, 5);
		gbc_lblSize.gridx = 4;
		gbc_lblSize.gridy = 1;
		cubePanel.add(lblSize, gbc_lblSize);
		
		JLabel lblCommand = new JLabel("Command");
		GridBagConstraints gbc_lblCommand = new GridBagConstraints();
		gbc_lblCommand.insets = new Insets(0, 0, 5, 5);
		gbc_lblCommand.gridx = 5;
		gbc_lblCommand.gridy = 1;
		cubePanel.add(lblCommand, gbc_lblCommand);
		
		JLabel lblComment = new JLabel("Comment");
		GridBagConstraints gbc_lblComment = new GridBagConstraints();
		gbc_lblComment.insets = new Insets(0, 0, 5, 0);
		gbc_lblComment.gridx = 6;
		gbc_lblComment.gridy = 1;
		cubePanel.add(lblComment, gbc_lblComment);
	}

	private GestureBox createGestureLine(boolean enabled, GESTURES gesture, String command, String comment, int row) {
		GestureBox box = new GestureBox();
		box.checkEnabled = new JCheckBox("");
		box.checkEnabled.setSelected(enabled);
		GridBagConstraints gbc_checkEnabled = new GridBagConstraints();
		gbc_checkEnabled.insets = new Insets(0, 0, 0, 5);
		gbc_checkEnabled.gridx = 0;
		gbc_checkEnabled.gridy = row;
		gesturePanel.add(box.checkEnabled, gbc_checkEnabled);
		
		box.comboGesture = new JComboBox<GESTURES>();
		box.comboGesture.setModel(new DefaultComboBoxModel<GESTURES>(GESTURES.values()));
		box.comboGesture.setSelectedItem(gesture);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = row;
		gesturePanel.add(box.comboGesture, gbc_comboBox);
		
		box.textCommand = new JTextField(command);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 0, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = row;
		gesturePanel.add(box.textCommand, gbc_textField);
		box.textCommand.setColumns(10);
		
		box.textComment = new JTextField(comment);
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 0, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 3;
		gbc_textField_1.gridy = row;
		gesturePanel.add(box.textComment, gbc_textField_1);
		box.textComment.setColumns(10);
		
		return box;
	}

	private CubeBox createCubeLine(boolean enabled, Point3D position, float size, String command, String comment, int row) {
		CubeBox box = new CubeBox();
		box.checkEnabled = new JCheckBox("");
		box.checkEnabled.setSelected(enabled);
		GridBagConstraints gbc_checkEnabled = new GridBagConstraints();
		gbc_checkEnabled.insets = new Insets(0, 0, 0, 5);
		gbc_checkEnabled.gridx = 0;
		gbc_checkEnabled.gridy = row;
		cubePanel.add(box.checkEnabled, gbc_checkEnabled);
		
		box.xSpinner = new JSpinner();
		box.xSpinner.setModel(new SpinnerNumberModel((int) position.getX(), -9000, 9000, 10));
		GridBagConstraints gbc_xSpinner = new GridBagConstraints();
		gbc_xSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_xSpinner.gridx = 1;
		gbc_xSpinner.gridy = row;
		cubePanel.add(box.xSpinner, gbc_xSpinner);
		
		box.ySpinner = new JSpinner();
		box.ySpinner.setModel(new SpinnerNumberModel((int) position.getY(), -9000, 9000, 10));
		GridBagConstraints gbc_ySpinner = new GridBagConstraints();
		gbc_ySpinner.insets = new Insets(0, 0, 0, 5);
		gbc_ySpinner.gridx = 2;
		gbc_ySpinner.gridy = row;
		cubePanel.add(box.ySpinner, gbc_ySpinner);
		
		box.zSpinner = new JSpinner();
		box.zSpinner.setModel(new SpinnerNumberModel((int) position.getZ(), 0, 10000, 10));
		GridBagConstraints gbc_zSpinner = new GridBagConstraints();
		gbc_zSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_zSpinner.gridx = 3;
		gbc_zSpinner.gridy = row;
		cubePanel.add(box.zSpinner, gbc_zSpinner);
		
		box.sizeSpinner = new JSpinner();
		box.sizeSpinner.setModel(new SpinnerNumberModel((int) size, 0, 2000, 10));
		GridBagConstraints gbc_zSize = new GridBagConstraints();
		gbc_zSize.insets = new Insets(0, 0, 0, 5);
		gbc_zSize.gridx = 4;
		gbc_zSize.gridy = row;
		cubePanel.add(box.sizeSpinner, gbc_zSize);
		
		box.textCommand = new JTextField(command);
		GridBagConstraints gbc_execText = new GridBagConstraints();
		gbc_execText.insets = new Insets(0, 0, 0, 5);
		gbc_execText.fill = GridBagConstraints.HORIZONTAL;
		gbc_execText.gridx = 5;
		gbc_execText.gridy = row;
		cubePanel.add(box.textCommand, gbc_execText);
		box.textCommand.setColumns(10);
		
		box.textComment = new JTextField(comment);
		GridBagConstraints gbc_commentText = new GridBagConstraints();
		gbc_commentText.insets = new Insets(0, 0, 0, 5);
		gbc_commentText.fill = GridBagConstraints.HORIZONTAL;
		gbc_commentText.gridx = 6;
		gbc_commentText.gridy = row;
		cubePanel.add(box.textComment, gbc_commentText);
		box.textComment.setColumns(10);
		
		return box;
	}

}
