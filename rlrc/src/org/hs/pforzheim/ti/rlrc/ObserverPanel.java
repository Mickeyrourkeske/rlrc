/**
 * 
 */
package org.hs.pforzheim.ti.rlrc;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import javax.swing.JPanel;

import org.hs.pforzheim.ti.ni.NIImage;
import org.hs.pforzheim.ti.ni.NaturalInterface;

/**
 * @author schrob
 *
 */
public class ObserverPanel extends JPanel implements Runnable {
	
	private static final long serialVersionUID = 7848069502421856447L;

	private volatile boolean isRunning;
	
	private NaturalInterface ni;

	private NIImage depthImage;
	
	public ObserverPanel(NaturalInterface ni) {
		this.ni = ni;
		
		new Thread(this).start();
		
	}

	public void close() {
		isRunning = false;
	}

	@Override
	public void run() {
		isRunning = true;
		while(isRunning) {
			depthImage = ni.depthImage();
			
		}
		
	}
	
	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
		
		if(depthImage != null) {
			DataBuffer dataBuffer = new DataBufferByte(depthImage.getImage(), depthImage.getImage().length);
			
			SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE,
					depthImage.getWidth(), depthImage.getHeight(), 1, depthImage.getWidth(), new int[]{2,1,0});
			
			Raster raster = Raster.createRaster(sampleModel, dataBuffer, null);
			
			BufferedImage bufferedImage = new BufferedImage(
					depthImage.getWidth(), depthImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			bufferedImage.setData(raster);
			
			g.drawImage(bufferedImage, 0, 0, null);
		}
	}

}
