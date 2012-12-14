/**
 * 
 */
package org.hs.pforzheim.ti.rlrc;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		drawable.setGL(new DebugGL(gl));
		
		gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glClearColor(0f, 0f, 0f, 1f);
        
        glu = new GLU();

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
			
			//setCamera(gl, glu, 1000); 

	        gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glTranslatef(getWidth()/2, getHeight()/2, -1000);
			gl.glRotatef(75, 1, 0, 0);
			

			gl.glTranslatef(0, 0, 1000);
			gl.glRotatef(position, 0, 1, 0);

			
			gl.glPointSize(1.0f);
			gl.glBegin(GL.GL_POINTS);
			
			
	        Point3D[] points = NICollector.ni.getPoints();
	        if(points != null) {
	        	for(int i = 0; i < points.length; i++) {
	        		
	        		if(points[i] != null) {
		        		gl.glVertex3f(points[i].getX(), points[i].getY(), points[i].getZ());
	        			
	        		}
	        	}
	        }
			
//	        gl.glColor3f(0.9f, 0.5f, 0.2f);
//	        gl.glBegin(GL.GL_TRIANGLE_FAN);
//	        gl.glVertex3f(-20, -20, 0);
//	        gl.glVertex3f(+20, -20, 0);
//	        gl.glVertex3f(0, 20, 0);
	        gl.glEnd();
	        
		}
		
	}
	
	

	private void setCamera(GL gl, GLU glu, float distance) {
        // Change to projection matrix.
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        
        /*
         * procedure gluPerspective(fovy, aspect, zNear, zFar : glDouble);
         * Erstellt eine perspektivische Projektionsmatrix.
         * Parameter
         * fovy	 	Gibt das Betrachtungsfeld (FOV) in Grad, entlang der Y-Achse, an.
         * aspect	Gibt den Aspektratio, der das Betrachtungsfeld entlang der X-Achse setzt. Der Aspektratio ist das Verhaeltnis von Breite zu Hoehe.
         * zNear	Entfernung vom Betrachter zur nahen Z-Schnittflaeche (muss positiv sein).
         * zFar		Entfernung vom Betrachter zur fernen Z-Schnittflaeche (muss positiv sein).
         */
        glu.gluPerspective(position, widthHeightRatio, 1, 1000);
        
        /*
         * procedure gluLookAt(eyex, eyey, eyez, centerx, centery, centerz, upx, upy, upz : glDouble);
         * Definiert eine Betrachtertransformation.
         * Parameter
         * eyeX, eyeY, eyeZ	 Gibt die Position des Betrachters an.
         * centerX, centerY, centerZ	 Gibt die Position des Refernenzpunktes an, auf den "geblickt" wird.
         * upX, upY, upZ	 Gibt die Richtung des Vektors an, der nach oben zeigt.
         */
        glu.gluLookAt(getWidth()/2, getHeight()/2, distance, getWidth()/2, getHeight()/2, 0, 0, 1, 0);
//        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
//        glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);
        // Change back to model view matrix.
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
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		
		
		
		if(e.getID() == MouseEvent.MOUSE_PRESSED) {
			
			mouseX = e.getX();
		}
		
		if(e.getID() == MouseEvent.MOUSE_RELEASED) {
			position += e.getX() - mouseX;
			
			System.out.println(position);
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
