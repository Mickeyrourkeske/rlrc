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
import org.OpenNI.Point3D;
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
	private static final int FREQ = 5;
	
	private DepthMetaData depthMetaData;
	private ImageMetaData imageMetaData;
	
	private DepthGenerator depthGenerator;
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
			
			
			depthGenerator = DepthGenerator.create(context);
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
	
	public NIImage depthImage() {
		byte[] image = new byte[depthMetaData.getFullXRes() * depthMetaData.getFullYRes()];
		
		try {
			context.waitAnyUpdateAll();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ShortBuffer buffer = depthMetaData.getData().createShortBuffer();
		float[] histogram = calcHistogram(buffer);
		
		while(buffer.remaining() > 0) {
			int pos = buffer.position();
			short pixel = buffer.get();
			image[pos] = (byte)histogram[pixel];
		}

		return new NIImage(image, depthMetaData.getFullXRes(), depthMetaData.getFullYRes());
	}
	
	public NIImage colorImage() {
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
		return new NIImage(image, imageMetaData.getFullXRes(), imageMetaData.getFullYRes());
		
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
	
	public Point3D[] getPoints() {
		try {
			context.waitAnyUpdateAll();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ShortBuffer buffer = depthMetaData.getData().createShortBuffer();
		
		
		
		
		Point3D[] points = new Point3D[buffer.capacity() / FREQ];
//		int i = 0;
//		for(int y = 0; y < yRes; y++) {
//			for(int x = 0; x < xRes; x++) {
//				i = y + x;
//				points[i] = new Point3D(xRes, yRes, 0);
//			}
//		}
		
		
		int i = 0;
		buffer.get();
		while(buffer.remaining() > 0) {
			int pos = buffer.position();
			float z = -(float)buffer.get();
			
			if((pos % FREQ) == 0) {
				i++;
				int x = pos % xRes;
				int y = yRes - (pos / xRes);
				points[i] = new Point3D(x, y, z);
			}
		}
		
		
//		try {
//			depthGenerator.getAlternativeViewpointCapability().setViewpoint(depthGenerator);
//		
//		
//		
//			points = depthGenerator.convertProjectiveToRealWorld(points);
//			
//			System.out.println(points[0].getX() + " " + points[0].getY() + " " + points[0].getZ() + " ");
//		} catch (StatusException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return points;
	}
	
	private int getMaxDepth(ShortBuffer depthBuffer) {

	    int maxDepth = 0;
	    while (depthBuffer.remaining() > 0) {
	      short depthVal = depthBuffer.get();
	      if (depthVal > maxDepth)
	        maxDepth = depthVal;
	    }
	    depthBuffer.rewind();

	    return maxDepth;
	}
}
