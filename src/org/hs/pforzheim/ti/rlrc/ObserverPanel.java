/**
 * 
 */
package org.hs.pforzheim.ti.rlrc;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import org.hs.pforzheim.ti.ni.NIImage;
import org.hs.pforzheim.ti.ni.NaturalInterface;

/**
 * @author schrob
 *
 */
public class ObserverPanel extends Component {
	
	private static final long serialVersionUID = 1L;
	
	private NaturalInterface ni;

	private NIImage depthImage;
	
	public ObserverPanel(NaturalInterface ni) {
		this.ni = ni;
		
		//new Thread(this).start();
		
	}

	
	@Override
	public void paint(Graphics g) {

		depthImage = ni.colorImage();
		if(depthImage != null) {
			DataBuffer dataBuffer = new DataBufferByte(depthImage.getImage(), depthImage.getImage().length);
			
			SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE,
					depthImage.getWidth(), depthImage.getHeight(), 3, depthImage.getWidth() * 3, new int[]{0,1,2});
			
			Raster raster = Raster.createRaster(sampleModel, dataBuffer, null);
			
			BufferedImage bufferedImage = new BufferedImage(
					depthImage.getWidth(), depthImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			bufferedImage.setData(raster);
			
			g.drawImage(bufferedImage, 0, 0, null);
		}
		
		super.paint(g);
	}
	
	
	
	@Override
	public int getWidth() {
		//TODO
		return 640;
	}
	
	@Override
	public int getHeight() {
		//TODO
		return 480;
	}

}
