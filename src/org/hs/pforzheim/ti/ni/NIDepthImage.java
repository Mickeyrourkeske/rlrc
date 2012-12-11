/**
 * 
 */
package org.hs.pforzheim.ti.ni;

/**
 * @author schrob
 *
 */
public class NIDepthImage extends NIImage {

	private byte[] image;

	public NIDepthImage(byte[] image2, int width, int height) {
		super(width, height);
		this.image = image2;
	}
	
	public byte[] getImage() {
		return image;
	}
	
}
