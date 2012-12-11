/**
 * 
 */
package org.hs.pforzheim.ti.ni;

/**
 * @author schrob
 *
 */
public class NIImage {

	protected int width;
	protected int height;

	public NIImage(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
