/**
 * 
 */
package org.hs.pforzheim.ti.ni;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.DepthMetaData;
import org.OpenNI.GeneralException;
import org.OpenNI.ImageGenerator;
import org.OpenNI.ImageMetaData;
import org.OpenNI.MapOutputMode;
import org.OpenNI.StatusException;

/**
 * @author schrob
 *
 */
public class NaturalInterface {

	private Context context;
	private boolean mirror = true;

	private static final int xRes = 640;
	private static final int yRes = 480;
	private static final int FPS = 30;
	
	private DepthMetaData depthMetaData;
	private ImageMetaData imageMetaData;
	/**
	 * 
	 * @throws GeneralException
	 */
	public NaturalInterface() {
		Logger.getLogger("rlrc").log(Level.INFO, "Initializing NI...");
		try {
			context = new Context();
			
			//TODO License??? mit xml file? ganz weglassen. so nicht gut
			
	//		License license = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");
	//		context.addLicense(license);
			
			
			DepthGenerator depthGenerator = DepthGenerator.create(context);
			ImageGenerator imageGenerator = ImageGenerator.create(context);
			
			MapOutputMode mapMode = new MapOutputMode(xRes, yRes, FPS);
			depthGenerator.setMapOutputMode(mapMode);
			imageGenerator.setMapOutputMode(mapMode);
			
			context.setGlobalMirror(mirror);
			
			depthMetaData = depthGenerator.getMetaData();
			imageMetaData = imageGenerator.getMetaData();
			//TODO generate more maps, scene, sceleton
			
			imageMetaData.setFullXRes(xRes);
			imageMetaData.setFullYRes(yRes);
			
			context.startGeneratingAll();
			Logger.getLogger("rlrc").log(Level.INFO, "NI succesfully initialized");
		}
		catch (GeneralException e) {
			Logger.getLogger("rlrc").log(Level.SEVERE, "Initializing NI failed! " + e.getMessage());
			Logger.getLogger("rlrc").log(Level.SEVERE, "Terminating!");
			System.exit(-1);
		}
	}
	
	
	public NIImage image() {
		return new NIImage(xRes, yRes);
	}
	
	public NIDepthImage depthImage() {
		byte[] image = new byte[depthMetaData.getFullXRes() * depthMetaData.getFullYRes()];
		
		try {
			context.waitAnyUpdateAll();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ShortBuffer buffer = depthMetaData.getData().createShortBuffer();
		float[] histogram = calcHistogram(buffer);
		buffer.rewind();
		
		while(buffer.remaining() > 0) {
			int pos = buffer.position();
			short pixel = buffer.get();
			image[pos] = (byte)histogram[pixel];
		}

		return new NIDepthImage(image, depthMetaData.getFullXRes(), depthMetaData.getFullYRes());
	}
	
	public NIColorImage colorImage() {
		byte[] image = new byte[imageMetaData.getFullXRes() * imageMetaData.getFullYRes() * 3];
		
		try {
			context.waitAnyUpdateAll();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ByteBuffer buffer = imageMetaData.getData().createByteBuffer();
		while(buffer.remaining() > 0) {
			int pos = buffer.position();
			byte pixel = buffer.get();
			image[pos] = pixel;

			//System.out.printf("%#X\n", pixel);
		}
		return new NIColorImage(image, imageMetaData.getFullXRes(), imageMetaData.getFullYRes());
		
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
		
		return histogram;
	}
}
