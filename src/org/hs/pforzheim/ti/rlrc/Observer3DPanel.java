/**
 * 
 */
package org.hs.pforzheim.ti.rlrc;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import org.OpenNI.Point3D;
import org.hs.pforzheim.ti.ni.NICollector;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

/**
 * @author schrob
 *
 */
public class Observer3DPanel extends GLCanvas implements GLEventListener {

	private static final long serialVersionUID = 1L;
	
	private Animator animator;
	private GLU glu;
	private Dimension dimension;
	private int mouseX;
	private int position = 0;	
	public Observer3DPanel() {
		super(createGLCapabilities());
		Logger.getLogger("rlrc").log(Level.INFO, "Starting OpenGL Observer");
		dimension = new Dimension(640, 480);
		
		setSize(dimension);
		addGLEventListener(this);
		
		
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {GL gl = drawable.getGL();
		drawable.setGL(new DebugGL(gl));
		
        gl.glLoadIdentity();
        
		gl.glEnable(GL.GL_POINT_SPRITE_ARB);
		gl.glEnable(GL.GL_POINT_SMOOTH);
		gl.glEnable(GL.GL_VERTEX_PROGRAM_POINT_SIZE);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		float[] quadtratic = {500.0f, 0.0f, 0.00005f};					// point size = size * sqrt(1/(a+b*d+c*d^2))  (d = distance from eye)
		gl.glPointParameterfvARB(GL.GL_POINT_DISTANCE_ATTENUATION_ARB, quadtratic, 0);
		
		float max = 80.0f;
		gl.glPointSize(max);
		
		gl.glPointParameterfARB(GL.GL_POINT_SIZE_MAX_ARB, max);
		gl.glPointParameterfARB(GL.GL_POINT_SIZE_MIN_ARB, 1.0f);
		
		gl.glTexEnvf(GL.GL_POINT_SPRITE_ARB, GL.GL_COORD_REPLACE_ARB, GL.GL_TRUE);
		
		
        glu = new GLU();

		addMouseMotionListener(new MouseMotionAdapter() { });
		addMouseListener(new MouseAdapter() { });
		
        animator = new FPSAnimator(this, 30);
        animator.start();
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL gl = drawable.getGL();
		gl.glViewport(0, 0, width, height);
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		// TODO Auto-generated method stub
		display(drawable);
		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		if(animator.isAnimating()) {
			GL gl = drawable.getGL();
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			
			setCamera(gl, glu, 10000);
			
			
			gl.glBegin(GL.GL_POINTS);
			
	        Point3D[] points = NICollector.ni.getRealWorldPoints();
	        if(points != null) {
	        	for(int i = 0; i < points.length; i++) {
	        		
	        		if(points[i] != null) {
	        			gl.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
		        		gl.glVertex3f(points[i].getX(), points[i].getY(), points[i].getZ());
	        		}
	        	}
	        }
			
	        gl.glEnd();
		}
		
	}
	
	

	private void setCamera(GL gl, GLU glu, float distance) {
        // Change to projection matrix.
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        
        /*
         * procedure gluPerspective(fovy, aspect, zNear, zFar : glDouble);
         * Erstellt eine perspektivische Projektionsmatrix.
         * Parameter
         * fovy	 	Gibt das Betrachtungsfeld (FOV) in Grad, entlang der Y-Achse, an.
         * aspect	Gibt den Aspektratio, der das Betrachtungsfeld entlang der X-Achse setzt. Der Aspektratio ist das Verhaeltnis von Breite zu Hoehe.
         * zNear	Entfernung vom Betrachter zur nahen Z-Schnittflaeche (muss positiv sein).
         * zFar		Entfernung vom Betrachter zur fernen Z-Schnittflaeche (muss positiv sein).
         */
        glu.gluPerspective(58, 4/3, 500, 10000);
        
        /*
         * procedure gluLookAt(eyex, eyey, eyez, centerx, centery, centerz, upx, upy, upz : glDouble);
         * Definiert eine Betrachtertransformation.
         * Parameter
         * eyeX, eyeY, eyeZ	 Gibt die Position des Betrachters an.
         * centerX, centerY, centerZ	 Gibt die Position des Refernenzpunktes an, auf den "geblickt" wird.
         * upX, upY, upZ	 Gibt die Richtung des Vektors an, der nach oben zeigt.
         */
        glu.gluLookAt(0, 0, 0, 0, 0, 500, 0, 1, 0);

        
        gl.glTranslatef(0, 0, 3000);
        gl.glRotated(position, 0, 1, 0);
        gl.glTranslatef(0, 0, -3000);
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
       
    }

	private static GLCapabilities createGLCapabilities() {
        GLCapabilities capabilities = new GLCapabilities();
        capabilities.setRedBits(8);
        capabilities.setBlueBits(8);
        capabilities.setGreenBits(8);
        capabilities.setAlphaBits(8);
        return capabilities;
    }

	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		super.processMouseMotionEvent(e);
		
		if(e.getID() == MouseEvent.MOUSE_DRAGGED) {
			int x = e.getX();
			if(mouseX != x) {
				position += x - mouseX;
				mouseX = x;
			}
		}
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		
		if(e.getID() == MouseEvent.MOUSE_PRESSED) {
			mouseX = e.getX();
		}
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
