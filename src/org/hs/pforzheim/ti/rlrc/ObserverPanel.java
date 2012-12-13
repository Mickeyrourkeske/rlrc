/**
 * 
 */
package org.hs.pforzheim.ti.rlrc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Timer;

import org.hs.pforzheim.ti.ni.NICollector;
import org.hs.pforzheim.ti.ni.NIImage;
import org.hs.pforzheim.ti.ni.NaturalInterface;

/**
 * @author schrob
 *
 */
public class ObserverPanel extends Component {
	
	private static final long serialVersionUID = 1L;
	
	private NaturalInterface ni;

	private Dimension dimension;
	private boolean depth = true;
	
	private int delay = 50;
	private Timer timer = new Timer(delay, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			repaint();
		}
	});
	
	public ObserverPanel() {
		Logger.getLogger("rlrc").log(Level.INFO, "Starting Image Observer");
		if(NICollector.ni != null) {
			this.ni = NICollector.ni;
			NIImage image = ni.depthImage();
			dimension = new Dimension(image.getWidth(), image.getHeight());
			addMouseListener(new MouseAdapter() { });
			timer.start();
		}
		else {
			Logger.getLogger("rlrc").log(Level.SEVERE, "No NICollector found. Using default dimensions!");

			dimension = new Dimension(640, 480);
		}
	}

	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);

		if(e.getID() == MouseEvent.MOUSE_CLICKED) {
			depth = !depth;
		}
	}
	
	@Override
	public void paint(Graphics g) {
		if(ni != null) {
			NIImage image = null;
			if(depth)
				image = ni.depthImage();
			else
				image = ni.colorImage();
			
			if(image != null) {
				DataBuffer dataBuffer = new DataBufferByte(image.getImage(), image.getImage().length);
				
				SampleModel sampleModel = null;
				
				if(depth)
					sampleModel = new ComponentSampleModel(DataBufferShort.TYPE_BYTE,
							image.getWidth(), image.getHeight(), 1, image.getWidth(), new int[]{0});
				else
					sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE,
							image.getWidth(), image.getHeight(), 3, image.getWidth() * 3, new int[]{0,1,2});
				
				Raster raster = Raster.createRaster(sampleModel, dataBuffer, null);
				
				BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(),
							depth ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR);
				bufferedImage.setData(raster);
				
				g.drawImage(bufferedImage, 0, 0, null);
			}
			
			
			
			//TODO switch
//			NIColorImage image = ni.colorImage();
//			if(image != null) {
//				DataBuffer dataBuffer = new DataBufferByte(image.getImage(), image.getImage().length);
//				
//				SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE,
//						image.getWidth(), image.getHeight(), 3, image.getWidth() * 3, new int[]{0,1,2});
//				
//				Raster raster = Raster.createRaster(sampleModel, dataBuffer, null);
//				
//				BufferedImage bufferedImage = new BufferedImage(
//						image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//				bufferedImage.setData(raster);
//				
//				g.drawImage(bufferedImage, 0, 0, null);
//			}
		}
		super.paint(g);
	}
	
	
	@Override
	public int getWidth() {
		return dimension.width;
	}
	
	@Override
	public int getHeight() {
		return dimension.height;
	}
	
	@Override
	public Dimension getSize() {
		return dimension;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return dimension;
	}
	
	@Override
	public Dimension getMaximumSize() {
		return dimension;
	}
	
	@Override
	public Dimension getMinimumSize() {
		return dimension;
	}



}
