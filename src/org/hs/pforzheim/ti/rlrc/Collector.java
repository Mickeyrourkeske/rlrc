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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.OpenNI.Point3D;
import org.hs.pforzheim.ti.ni.NI3d;
import org.hs.pforzheim.ti.ni.NITracker;
import org.hs.pforzheim.ti.ni.NIVisual;
import org.hs.pforzheim.ti.rlrc.agent.CubeAgent;
import org.hs.pforzheim.ti.rlrc.agent.GestureAgent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author schrob
 *
 */
public final class Collector {

	private static final Logger LOGGER = Logger.getLogger(Collector.class.getName());

	public static String agentsFile = "agents.xml";
	
	public static ArrayList<CubeAgent> cubeAgents = null;
	public static ArrayList<GestureAgent> gestureAgents = null;
	
	private static NIVisual niVisual = null;
	private static NI3d ni3d = null;
	private static NITracker niTracker = null;
	

	public Collector() {
		if(cubeAgents == null) {
			cubeAgents = new ArrayList<CubeAgent>();
		}
		if(gestureAgents == null) {
			gestureAgents = new ArrayList<GestureAgent>();
		}
	}
	
	public static NIVisual getNIVisual() {
		if(niVisual == null) {
			niVisual = new NIVisual();
		}
		return niVisual;
	}
	
	public static NI3d getNI3d() {
		if(ni3d == null) {
			ni3d = new NI3d();
		}
		return ni3d;
	}
	
	public static void startNITracker() {
		if(niTracker == null) {
			niTracker = new NITracker();
		}
	}
	
	public static NITracker getNiTracker() {
		return niTracker;
	}

	public static void readAgentsFromXML(String xml) {
		try {
			LOGGER.info("Get Agents from xml file...");
			File xmlFile = new File(xml);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			
			NodeList cubeAgentList = document.getElementsByTagName("CubeAgent");
			
			for(int i = 0; i < cubeAgentList.getLength(); i++) {
				Node node = cubeAgentList.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)node;
					float x = new Float(getValue("x", element));
					float y = new Float(getValue("y", element));
					float z = new Float(getValue("z", element));
					float size = new Float(getValue("size", element));
					String execString = getValue("execString", element);
					try {
						String comment = getValue("comment", element);
						cubeAgents.add(new CubeAgent(new Point3D(x, y, z), size, execString, comment));
					}
					catch(Exception e) {
						LOGGER.warning("No comment specified for " + execString);
						cubeAgents.add(new CubeAgent(new Point3D(x, y, z), size, execString));
					}
				}
			}
			
			
			NodeList gestureAgentList = document.getElementsByTagName("GestureAgent");
			
			for(int i = 0; i < gestureAgentList.getLength(); i++) {
				Node node = gestureAgentList.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)node;
					String gesture = getValue("gesture", element);
					String execString = getValue("execString", element);
					String comment = getValue("comment", element);
					gestureAgents.add(new GestureAgent(gesture, execString, comment));
				}
			}
		}
		catch(Exception e) {
			LOGGER.warning("Could not get Agents from xml file: " + e.getMessage());
		}
		
	}
	
	private static String getValue(String tag, Element element) {
		return element.getElementsByTagName(tag).item(0).getChildNodes().item(0).getNodeValue();
	}
	
	public static void writeAgentsToXML(String xmlFile) {
		/* Logger is not working while disposing */
		
		
		try {
			Files.copy(Paths.get(xmlFile), Paths.get(xmlFile + "~"), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Writing Agents to xml...");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			Element root = document.createElement("Agent");
			document.appendChild(root);
			
			for(CubeAgent agent : cubeAgents) {
				Element cubeAgent = document.createElement("CubeAgent");
				root.appendChild(cubeAgent);
				
				Element x = document.createElement("x");
				x.appendChild(document.createTextNode(String.valueOf(agent.getPosition().getX())));
				cubeAgent.appendChild(x);

				Element y = document.createElement("y");
				y.appendChild(document.createTextNode(String.valueOf(agent.getPosition().getY())));
				cubeAgent.appendChild(y);

				Element z = document.createElement("z");
				z.appendChild(document.createTextNode(String.valueOf(agent.getPosition().getZ())));
				cubeAgent.appendChild(z);
				
				Element size = document.createElement("size");
				size.appendChild(document.createTextNode(String.valueOf(agent.getSize())));
				cubeAgent.appendChild(size);

				Element execString = document.createElement("execString");
				execString.appendChild(document.createTextNode(agent.getCommand()));
				cubeAgent.appendChild(execString);
				
				if(agent.getComment() != null) {
					Element comment = document.createElement("comment");
					comment.appendChild(document.createTextNode(agent.getComment()));
					cubeAgent.appendChild(comment);
				}
				else {
					System.out.println("No comment specified for " + agent.getCommand());
				}
			}
			
			for(GestureAgent agent : gestureAgents) {
				Element cubeAgent = document.createElement("GestureAgent");
				root.appendChild(cubeAgent);

				Element gesture = document.createElement("gesture");
				gesture.appendChild(document.createTextNode(agent.getGesture().name()));
				cubeAgent.appendChild(gesture);

				Element execString = document.createElement("execString");
				execString.appendChild(document.createTextNode(agent.getCommand()));
				cubeAgent.appendChild(execString);
				
				if(agent.getComment() != null) {
					Element comment = document.createElement("comment");
					comment.appendChild(document.createTextNode(agent.getComment()));
					cubeAgent.appendChild(comment);
				}
				else {
					System.out.println("No comment specified for " + agent.getCommand());
				}
			}
			
			
			/* Save XML */
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			
			DOMSource source = new DOMSource(document);
			
			FileWriter fileWriter = new FileWriter(xmlFile);
			StreamResult streamResult = new StreamResult(fileWriter);
			transformer.transform(source, streamResult);
			
			System.out.println("Agents saved as " + xmlFile);
		}
		catch(Exception e) {
			System.out.println("Could not write Agents to xml file: " + e.getMessage());

			try {
				Files.copy(Paths.get(xmlFile + "~"), Paths.get(xmlFile), StandardCopyOption.REPLACE_EXISTING);
			}
			catch (IOException e1) { }
			e.printStackTrace();
		}
	}
	
	public static void disposeNI() {
		/* Logger is not working while disposing */
		System.out.println("Disposing NI...");
		if(niVisual != null) {
			niVisual.release();
		}

		if(niTracker != null) {
			niTracker.release();
		}
		
		if(ni3d != null) {
			ni3d.release();
		}
		
	}
}
