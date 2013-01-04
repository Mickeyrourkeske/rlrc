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
package org.hs.pforzheim.ti.ni;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.OpenNI.DepthGenerator;
import org.OpenNI.GeneralException;
import org.OpenNI.ImageGenerator;
import org.OpenNI.MapOutputMode;

public class NIVisual extends NI 
{
	
	public NIVisual() {
		super();
		try {
			Logger.getLogger("rlrc").log(Level.INFO, "Initializing NI Visual...");
			if(depthGenerator == null)
				depthGenerator = DepthGenerator.create(context);
			if(imageGenerator == null)
				imageGenerator = ImageGenerator.create(context);
			
//			MapOutputMode[] mapOutputMode = depthGenerator.getSupportedMapOutputModes();
//			for(MapOutputMode mode : mapOutputMode) {
//				System.out.println(mode.getXRes() + " x " + mode.getYRes() + " @ " + mode.getFPS());
//			}
			
			MapOutputMode mapMode = new MapOutputMode(xRes, yRes, FPS);
			depthGenerator.setMapOutputMode(mapMode);
			imageGenerator.setMapOutputMode(mapMode);
			
			context.setGlobalMirror(mirror);
			
			depthMetaData = depthGenerator.getMetaData();
			imageMetaData = imageGenerator.getMetaData();
	
			imageMetaData.setFullXRes(xRes);
			imageMetaData.setFullYRes(yRes);
			
			if(!depthGenerator.isGenerating())
				depthGenerator.startGenerating();
			if(!imageGenerator.isGenerating())
				imageGenerator.startGenerating();
			
			logNodes();
		}
		catch (GeneralException e) {
			String log = "Initializing NI failed! " + e.getMessage();
			log += "\nTerminating!";
			Logger.getLogger("rlrc").log(Level.SEVERE, log);
			System.exit(-1);
		}
	}

	public NIImage depthImage() {
		try {
			byte[] image = new byte[depthMetaData.getFullXRes() * depthMetaData.getFullYRes()];
			
			ShortBuffer buffer = depthMetaData.getData().createShortBuffer();
			
			float[] histogram = calcHistogram(buffer);
			
			while(buffer.remaining() > 0) {
				int pos = buffer.position();
				short pixel = buffer.get();
				image[pos] = (byte)histogram[pixel];
			}
	
			return new NIImage(image, depthMetaData.getFullXRes(), depthMetaData.getFullYRes());
			
		}
		catch(Exception e) {
			Logger.getLogger("rlrc").log(Level.SEVERE, "Depthimage failed! " + e.getMessage());
			return null;
		}
	}

	public NIImage colorImage() {
		try {
			byte[] image = new byte[imageMetaData.getFullXRes() * imageMetaData.getFullYRes() * 3];
			
			ByteBuffer buffer = imageMetaData.getData().createByteBuffer();
			while(buffer.remaining() > 0) {
				int pos = buffer.position();
				byte pixel = buffer.get();
				image[pos] = pixel;
			}
			
			return new NIImage(image, imageMetaData.getFullXRes(), imageMetaData.getFullYRes());
			
		}
		catch(Exception e) {
			Logger.getLogger("rlrc").log(Level.SEVERE, "Colorimage failed! " + e.getMessage());
			return null;
		}
		
	}

	private float []calcHistogram(ShortBuffer depthBuffer) {
		int MAX_DEPTH_SIZE = 10000;
		float[] histogram = new float[MAX_DEPTH_SIZE];
	
		int numPoints = 0; 
		int maxDepth = 0;
		
		// a depth (an integer mm value) is used as an index  
		// into the array 
		while(depthBuffer.remaining() > 0) { 
			short depthVal = depthBuffer.get(); 
			if(depthVal > maxDepth) 
				maxDepth = depthVal; 
			if((depthVal != 0)  && (depthVal < MAX_DEPTH_SIZE)) { 
				histogram[depthVal]++; 
				numPoints++; 
			} 
		} 
		
		// convert into a cummulative depth count (skipping histogram[0]) 
		for (int i = 1; i <= maxDepth; i++)    // stage 3 
			histogram[i] += histogram[i - 1]; 
		
		// convert cummulative depth into integers (0-255)
		if (numPoints > 0) { 
			for (int i = 1; i <= maxDepth; i++)   // skip histogram[0] 
				histogram[i] = (int) (256 * (1.0f - (histogram[i] / (float) numPoints))); 
		}
		depthBuffer.rewind();
		
		return histogram;
	}

}
