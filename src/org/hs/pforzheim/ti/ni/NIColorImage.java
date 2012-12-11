/**
 * 
 */
package org.hs.pforzheim.ti.ni;

/**
 * @author schrob
 *
 */
public class NIColorImage extends NIImage {

	private byte[] image;
	
	public NIColorImage(byte[] image, int width, int height) {
		super(width, height);
		this.image = image;
	}
	
	public byte[] getImage() {
		return image;
	}
}
