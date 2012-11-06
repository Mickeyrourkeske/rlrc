/**
 * 
 */
package org.hs.pforzheim.ti.ni;

import java.nio.ShortBuffer;

import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.DepthMetaData;
import org.OpenNI.GeneralException;
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
	
	/**
	 * 
	 * @throws GeneralException
	 */
	public NaturalInterface() throws GeneralException {
		context = new Context();
		
		//TODO License??? mit xml file? ganz weglassen. so nicht gut
		
		License license = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");
		context.addLicense(license);
		
		DepthGenerator depthGenerator = DepthGenerator.create(context);
		MapOutputMode mapMode = new MapOutputMode(xRes, yRes, FPS);
		depthGenerator.setMapOutputMode(mapMode);
		
		context.setGlobalMirror(mirror);
		
		depthMetaData = depthGenerator.getMetaData();
		//TODO generate more maps, scene, sceleton
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
			short pixel = buffer.get();
			image[pos * 3] = (byte) pixel;
			image[pos * 3 + 1] = (byte) pixel;
			image[pos * 3 + 2] = (byte) pixel;
		}
		return new NIImage(image, depthMetaData.getFullXRes(), depthMetaData.getFullYRes());
	}
}
