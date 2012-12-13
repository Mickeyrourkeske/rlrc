/**
 * 
 */
package org.hs.pforzheim.ti.ni;

/**
 * @author schrob
 *
 */
public class NIImage {

	private byte[] image;
	private int width;
	private int height;

	public NIImage(byte[] image, int width, int height) {
		this.image = image;
		this.width = width;
		this.height = height;
	}
	
	public byte[] getImage() {
		return image;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
