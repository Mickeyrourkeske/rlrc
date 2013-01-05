/**
 * This file is part of RLRC.
 * 
 * RLRC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RLRC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with RLRC. If not, see <http://www.gnu.org/licenses/>.
 */
package org.hs.pforzheim.ti.rlrc;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.OpenNI.Point3D;
import org.hs.pforzheim.ti.ni.NI3d;
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

	private NI3d ni;
	
	public Observer3DPanel() {
		super(createGLCapabilities());
		Logger.getLogger("rlrc").log(Level.INFO, "Starting OpenGL Observer");
		
		dimension = new Dimension(640, 480);
		setSize(dimension);
		
		addGLEventListener(this);
		addMouseMotionListener(new MouseMotionAdapter() { });
		addMouseListener(new MouseAdapter() { });
		
		ni = NICollector.getNI3d();
		ni.startCollectingRealPoints();
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {GL gl = drawable.getGL();
		Logger.getLogger("rlrc").log(Level.INFO, "Initializing OpenGL");
		drawable.setGL(new DebugGL(gl));
		
        gl.glLoadIdentity();
        
		gl.glEnable(GL.GL_POINT_SPRITE_ARB);
		gl.glEnable(GL.GL_POINT_SMOOTH);
		gl.glEnable(GL.GL_VERTEX_PROGRAM_POINT_SIZE);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendEquationSeparate(GL.GL_FUNC_ADD, GL.GL_FUNC_ADD);
		gl.glBlendFuncSeparate(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_ONE, GL.GL_ZERO);
		
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		float[] quadtratic = {500.0f, 0.0f, 0.00005f};					// point size = size * sqrt(1/(a+b*d+c*d^2))  (d = distance from eye)
		gl.glPointParameterfvARB(GL.GL_POINT_DISTANCE_ATTENUATION_ARB, quadtratic, 0);
		
		float max = 80.0f;
		gl.glPointSize(max);
		
		gl.glPointParameterfARB(GL.GL_POINT_SIZE_MAX_ARB, max);
		gl.glPointParameterfARB(GL.GL_POINT_SIZE_MIN_ARB, 1.0f);
		
		gl.glTexEnvf(GL.GL_POINT_SPRITE_ARB, GL.GL_COORD_REPLACE_ARB, GL.GL_TRUE);
		
		
        glu = new GLU();

		
        animator = new FPSAnimator(this, 15);
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
			
			setCamera(gl, glu);
			
			
			if(ni != null) {
				gl.glBegin(GL.GL_POINTS);
				gl.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
				
				Point3D[] points = ni.getAndAcquireRealWorldPoints();
		        if(points != null) {
		        	for(int i = 0; i < points.length; i++) {
		        		
		        		if(points[i] != null) {
			        		gl.glVertex3f(-points[i].getX(), points[i].getY(), points[i].getZ());			//x turned around
		        		}
		        	}
		        }
		        gl.glEnd();
		        
		        ni.releaseRealWorldPoints();
		        
		        /* Print Hands */
		        if(NICollector.getNiTracker() != null) {
			        HashMap<Integer, Point3D> hands = NICollector.getNiTracker().getAndAcquireHands();
			        Set<Integer> set = hands.keySet();
			        for(Integer id : set) {
			        	Point3D point = hands.get(id);
			        	
			        	gl.glTranslatef(-point.getX(), point.getY(), point.getZ());
			        	gl.glColor3f(1, 0, 0);
			        	GLUquadric hand = glu.gluNewQuadric();
			        	glu.gluQuadricDrawStyle(hand, GLU.GLU_FILL);
			        	glu.gluQuadricNormals(hand, GLU.GLU_FLAT);
			        	glu.gluQuadricOrientation(hand, GLU.GLU_OUTSIDE);
			        	glu.gluSphere(hand, 10, 20, 20);
			        	gl.glTranslatef(point.getX(), -point.getY(), -point.getZ());
			        }
			        NICollector.getNiTracker().releaseHands();
		        }
		        
		        /* Print Cubes */
		        gl.glBegin(GL.GL_QUADS);
		        
		        for(CubeAgent agent : NICollector.cubeAgents) {
		        	if(agent.isHit()) {
		        		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.2f);
		        	}
		        	else {
		        		gl.glColor4f(0.0f, 0.0f, 1.0f, 0.2f);
		        	}
		        	float x = -agent.getPosition().getX();			//x turned around
		        	float y = agent.getPosition().getY();
		        	float z = agent.getPosition().getZ();
		        	float size = agent.getSize() / 2;
		        	
		        	/*
		        	 * Drawing Cube
		        	 *       ______
		        	 *      /  T  /|
		        	 *     1-----4 |
		        	 *     |     |L|
		        	 *     |  F  |/
		        	 *     2-----3
		        	 */
		        	
		        	/* Front */
		        	gl.glVertex3f(x + size, y - size, z + size);
		        	gl.glVertex3f(x - size, y - size, z + size);
		        	gl.glVertex3f(x - size, y + size, z + size);
		        	gl.glVertex3f(x + size, y + size, z + size);
		        	
		        	/* Backside */
		        	gl.glVertex3f(x + size, y - size, z - size);
		        	gl.glVertex3f(x - size, y - size, z - size);
		        	gl.glVertex3f(x - size, y + size, z - size);
		        	gl.glVertex3f(x + size, y + size, z - size);
		        	
		        	/* Rightside */
		        	gl.glVertex3f(x - size, y - size, z + size);
		        	gl.glVertex3f(x - size, y - size, z - size);
		        	gl.glVertex3f(x - size, y + size, z - size);
		        	gl.glVertex3f(x - size, y + size, z + size);
		        	
		        	/* Leftside */
		        	gl.glVertex3f(x + size, y + size, z + size);
		        	gl.glVertex3f(x + size, y + size, z - size);
		        	gl.glVertex3f(x + size, y - size, z - size);
		        	gl.glVertex3f(x + size, y - size, z + size);
		        	
		        	/* Buttom */
		        	gl.glVertex3f(x - size, y - size, z + size);
		        	gl.glVertex3f(x - size, y - size, z - size);
		        	gl.glVertex3f(x + size, y - size, z - size);
		        	gl.glVertex3f(x + size, y - size, z + size);
		        	
		        	/* Top */
		        	gl.glVertex3f(x - size, y + size, z + size);
		        	gl.glVertex3f(x - size, y + size, z - size);
		        	gl.glVertex3f(x + size, y + size, z - size);
		        	gl.glVertex3f(x + size, y + size, z + size);
		        }

		        gl.glEnd();
			}
			else {
				gl.glBegin(GL.GL_QUAD_STRIP);
				
				gl.glVertex3f(100, 90, 1000);
				gl.glVertex3f(-100, -110, 1000);
				gl.glVertex3f(100, 110, 1000);
				gl.glVertex3f(-100, -90, 1000);

		        gl.glEnd();
			}
	        
			
		}
		
	}
	
	

	private void setCamera(GL gl, GLU glu) {
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

        /* Rotation of Camera */
        gl.glTranslatef(0, 0, 1000);
        gl.glRotated(position, 0, 1, 0);
        gl.glTranslatef(0, 0, -1000);
        
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
