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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.DefaultComboBoxModel;
import org.hs.pforzheim.ti.rlrc.GestureAgent.GESTURES;

public class AgentPanel extends JPanel {
	private JTextField execText;
	private JTextField commentText;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Create the panel.
	 */
	public AgentPanel() {
		setMinimumSize(new Dimension(1280, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 469, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel cubePanel = new JPanel();
		cubePanel.setMinimumSize(new Dimension(640, 10));
		GridBagConstraints gbc_cubePanel = new GridBagConstraints();
		gbc_cubePanel.insets = new Insets(0, 0, 0, 5);
		gbc_cubePanel.fill = GridBagConstraints.BOTH;
		gbc_cubePanel.gridx = 0;
		gbc_cubePanel.gridy = 0;
		add(cubePanel, gbc_cubePanel);
		GridBagLayout gbl_cubePanel = new GridBagLayout();
		gbl_cubePanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_cubePanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_cubePanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_cubePanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		cubePanel.setLayout(gbl_cubePanel);
		
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
		
		JLabel lblCommand_1 = new JLabel("Command");
		GridBagConstraints gbc_lblCommand_1 = new GridBagConstraints();
		gbc_lblCommand_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblCommand_1.gridx = 4;
		gbc_lblCommand_1.gridy = 1;
		cubePanel.add(lblCommand_1, gbc_lblCommand_1);
		
		JLabel lblComment_1 = new JLabel("Comment");
		GridBagConstraints gbc_lblComment_1 = new GridBagConstraints();
		gbc_lblComment_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblComment_1.gridx = 5;
		gbc_lblComment_1.gridy = 1;
		cubePanel.add(lblComment_1, gbc_lblComment_1);
		
		JCheckBox checkBox = new JCheckBox("");
		GridBagConstraints gbc_checkBox = new GridBagConstraints();
		gbc_checkBox.insets = new Insets(0, 0, 0, 5);
		gbc_checkBox.gridx = 0;
		gbc_checkBox.gridy = 2;
		cubePanel.add(checkBox, gbc_checkBox);
		
		JSpinner xSpinner = new JSpinner();
		xSpinner.setModel(new SpinnerNumberModel(1000, 500, 10000, 10));
		GridBagConstraints gbc_xSpinner = new GridBagConstraints();
		gbc_xSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_xSpinner.gridx = 1;
		gbc_xSpinner.gridy = 2;
		cubePanel.add(xSpinner, gbc_xSpinner);
		
		JSpinner ySpinner = new JSpinner();
		ySpinner.setModel(new SpinnerNumberModel(1000, 500, 10000, 10));
		GridBagConstraints gbc_ySpinner = new GridBagConstraints();
		gbc_ySpinner.insets = new Insets(0, 0, 0, 5);
		gbc_ySpinner.gridx = 2;
		gbc_ySpinner.gridy = 2;
		cubePanel.add(ySpinner, gbc_ySpinner);
		
		JSpinner zSpinner = new JSpinner();
		zSpinner.setModel(new SpinnerNumberModel(1000, 500, 10000, 10));
		GridBagConstraints gbc_zSpinner = new GridBagConstraints();
		gbc_zSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_zSpinner.gridx = 3;
		gbc_zSpinner.gridy = 2;
		cubePanel.add(zSpinner, gbc_zSpinner);
		
		execText = new JTextField();
		GridBagConstraints gbc_execText = new GridBagConstraints();
		gbc_execText.insets = new Insets(0, 0, 0, 5);
		gbc_execText.fill = GridBagConstraints.HORIZONTAL;
		gbc_execText.gridx = 4;
		gbc_execText.gridy = 2;
		cubePanel.add(execText, gbc_execText);
		execText.setColumns(10);
		
		commentText = new JTextField();
		GridBagConstraints gbc_commentText = new GridBagConstraints();
		gbc_commentText.fill = GridBagConstraints.HORIZONTAL;
		gbc_commentText.gridx = 5;
		gbc_commentText.gridy = 2;
		cubePanel.add(commentText, gbc_commentText);
		commentText.setColumns(10);
		
		JPanel gesturePanel = new JPanel();
		gesturePanel.setMinimumSize(new Dimension(640, 10));
		GridBagConstraints gbc_gesturePanel = new GridBagConstraints();
		gbc_gesturePanel.fill = GridBagConstraints.BOTH;
		gbc_gesturePanel.gridx = 1;
		gbc_gesturePanel.gridy = 0;
		add(gesturePanel, gbc_gesturePanel);
		GridBagLayout gbl_gesturePanel = new GridBagLayout();
		gbl_gesturePanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_gesturePanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_gesturePanel.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_gesturePanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gesturePanel.setLayout(gbl_gesturePanel);
		
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
		
		JCheckBox checkBox_1 = new JCheckBox("");
		GridBagConstraints gbc_checkBox_1 = new GridBagConstraints();
		gbc_checkBox_1.insets = new Insets(0, 0, 0, 5);
		gbc_checkBox_1.gridx = 0;
		gbc_checkBox_1.gridy = 2;
		gesturePanel.add(checkBox_1, gbc_checkBox_1);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(GESTURES.values()));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 2;
		gesturePanel.add(comboBox, gbc_comboBox);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 0, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 2;
		gesturePanel.add(textField, gbc_textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 3;
		gbc_textField_1.gridy = 2;
		gesturePanel.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);

	}

}
