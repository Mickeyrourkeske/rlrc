/**
 * 
 */
package org.hs.pforzheim.ti.ni;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.DepthMetaData;
import org.OpenNI.GeneralException;
import org.OpenNI.ImageGenerator;
import org.OpenNI.ImageMetaData;
import org.OpenNI.License;
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
	public NaturalInterface() throws GeneralException {
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
	}
	
	
	public NIImage depthImage() {
		
		byte[] image = new byte[depthMetaData.getFullXRes() * depthMetaData.getFullYRes() * 3];
		
		try {
			context.waitAnyUpdateAll();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ShortBuffer buffer = depthMetaData.getData().createShortBuffer();
		
		buffer.rewind();
		
		while(buffer.remaining() > 0) {
			int pos = buffer.position();
			byte pixel = (byte) (buffer.get() >> 4);
			image[pos * 3] = pixel;
			image[pos * 3 + 1] = pixel;
			image[pos * 3 + 2] = pixel;
			//System.out.printf("%#X\n", pixel);
			//System.out.println(image[pos * 3] + image[pos * 3 + 1] + image[pos * 3 + 2]);
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
}
