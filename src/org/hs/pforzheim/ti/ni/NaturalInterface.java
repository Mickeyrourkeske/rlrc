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
import org.OpenNI.DepthMap;
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
	public static final int FREQ = 2;			// 40, 80, 160, ... Has to be a factor of xRes and yRes
	
	private DepthMetaData depthMetaData;
	private ImageMetaData imageMetaData;
	
	private DepthGenerator depthGenerator;
	private ImageGenerator imageGenerator;
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
			imageGenerator = ImageGenerator.create(context);
			
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
	
	public Point3D[] getRealWorldPoints() {
		try {
			context.waitAnyUpdateAll();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("============================");
		Point3D[] realPoints = new Point3D[(xRes / FREQ) * (yRes / FREQ)];
		
		DepthMap map = depthMetaData.getData();
		int index = 0;
		for(int y = FREQ/2; y < yRes; y += FREQ) {
			for(int x = FREQ/2; x < xRes; x += FREQ) {
				try {
					int z = (int)map.readPixel(x, y);
					Point3D point = depthGenerator.convertProjectiveToRealWorld(new Point3D(x, y, z));
					realPoints[index] = point;
					index++;
					
//					System.out.println("x: " + x + "->" + point.getX() + "\ty: " + y + "->" + point.getY() + "\tz: " + z + "->" + point.getZ() );
					
				} catch (StatusException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
//		Point3D[] projecticePoints = new Point3D[(xRes / FREQ) * (yRes / FREQ)];
//		Point3D[] realPoints = null;
//		DepthMap map = depthMetaData.getData();
////		System.out.println("=====================================");
////		System.out.println(projecticePoints.length);
//		int index = 0;
//		for(int y = 0; y < yRes; y += FREQ) {
//			for(int x = 0; x < xRes; x += FREQ) {
//				projecticePoints[index] = new Point3D(x, y, (int)map.readPixel(x, y));
//				index++;
//			}
//		}
//
//		try {
//			realPoints = new Point3D[(xRes / FREQ) * (yRes / FREQ)];
//			realPoints = depthGenerator.convertProjectiveToRealWorld(projecticePoints);
//			float maxWidth = 0;
//			float maxHeight = 0;
//			
//			for(Point3D point : realPoints) {
//				//System.out.println(point.getX() + " " + point.getY() + " " + point.getZ() + " ");
//				
//				if(point.getX() > maxWidth)
//					maxWidth = point.getX();
//				
//				if(point.getY() > maxHeight)
//					maxHeight = point.getY();
//				
//			}
////			System.out.println(maxWidth + " " + maxHeight);
////			System.out.println(realPoints[80].getX() + " " + realPoints[80].getY() + " " + realPoints[80].getZ() + " ");
//		}
//		catch(StatusException e) {
//			Logger.getLogger("rlrc").log(Level.SEVERE, "Could not convert data to real world! " + e.getMessage());
//			realPoints = projecticePoints;
//			
//		}
		
//		Point3D[] projecticePoints = new Point3D[buffer.capacity() / FREQ];
//		Point3D[] realPoints = new Point3D[buffer.capacity() / FREQ];
//		int i = 0;
//		for(int y = 0; y < yRes; y++) {
//			for(int x = 0; x < xRes; x++) {
//				i = y + x;
//				points[i] = new Point3D(xRes, yRes, 0);
//			}
//		}
//		
//		
//		int i = 0;
//		buffer.get();
//		while(buffer.remaining() > 0) {
//			int pos = buffer.position();
//			float z = (float)buffer.get();
//			
//			if((pos % FREQ) == 0) {
//				i++;
//				int x = pos % xRes;
//				int y = yRes - (pos / xRes);
//				projecticePoints[i] = new Point3D(x, y, z);
//			}
//		}
//		
//		
//		try {
//			if(projecticePoints != null) {
//				realPoints = depthGenerator.convertProjectiveToRealWorld(projecticePoints);
//				
//				System.out.println(realPoints[0].getX() + " " + realPoints[0].getY() + " " + realPoints[0].getZ() + " ");
//			}
//		} catch (StatusException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		Point3D[] points = new Point3D[1];
//		
//		DepthMap map = depthMetaData.getData();
//		
//		int x = imageMetaData.getXRes()/2;
//		int y = imageMetaData.getYRes()/2;
//		int z = map.createShortBuffer().get(y * imageMetaData.getXRes() + x);
//		
//		Point3D projectivePoint = new Point3D(x, y, z);
//		Point3D realPoint;
//		
//		try {
//			realPoint = depthGenerator.convertProjectiveToRealWorld(projectivePoint);
//			System.out.println(realPoint.getX() + " " + realPoint.getY() + " " + realPoint.getZ() + " ");
//			
//		} catch (StatusException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
		
//		try {
//			imageGenerator.getAlternativeViewpointCapability().setViewpoint(imageGenerator);
//			
//			Point3D point = depthGenerator.convertProjectiveToRealWorld(new Point3D(0, 0, z));
//
//			System.out.println(point.getX() + " " + point.getY() + " " + point.getZ() + " ");
//			
//		} catch (StatusException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		return realPoints;
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
