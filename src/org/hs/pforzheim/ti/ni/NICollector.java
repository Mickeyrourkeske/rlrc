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
package org.hs.pforzheim.ti.ni;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.OpenNI.Point3D;
import org.hs.pforzheim.ti.rlrc.CubeAgent;
import org.hs.pforzheim.ti.rlrc.GestureAgent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author schrob
 *
 */
public final class NICollector {

//	public static NI ni = null;
	public static ArrayList<CubeAgent> cubeAgents = null;
	public static ArrayList<GestureAgent> gestureAgents = null;
	
	private static NIVisual niVisual = null;
	private static NI3d ni3d = null;
	private static NITracker niTracker = null;
	

	public NICollector() {
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
			File xmlFile = new File(xml);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			
			NodeList cubeAgentList = document.getElementsByTagName("CubeAgent");
			
			for(int i = 0; i < cubeAgentList.getLength(); i++) {
				Node node = cubeAgentList.item(i);
				System.out.println(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)node;
					float x = new Float(getValue("x", element));
					float y = new Float(getValue("y", element));
					float z = new Float(getValue("z", element));
					float size = new Float(getValue("size", element));
					String execString = getValue("execString", element);
					cubeAgents.add(new CubeAgent(new Point3D(x, y, z), size, execString));
				}
			}
			
			
			NodeList gestureAgentList = document.getElementsByTagName("GestureAgent");
			
			for(int i = 0; i < gestureAgentList.getLength(); i++) {
				Node node = gestureAgentList.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)node;
					String gesture = getValue("gesture", element);
					String execString = getValue("execString", element);
					gestureAgents.add(new GestureAgent(gesture, execString));
				}
			}
		}
		catch(Exception e) {
			
		}
		
	}
	
	private static String getValue(String tag, Element element) {
		return element.getElementsByTagName(tag).item(0).getChildNodes().item(0).getNodeValue();
	}
	
}
