/**
 * 
 */
package org.hs.pforzheim.ti.rlrc;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

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
	
	public Observer3DPanel(int width, int height, GLCapabilities capabilities) {
		super(capabilities);
		setSize(width, height);
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
		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		if(animator.isAnimating()) {
			GL gl = drawable.getGL();
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			
			setCamera(gl, glu, 100); 
			
	        gl.glColor3f(0.9f, 0.5f, 0.2f);
	        gl.glBegin(GL.GL_TRIANGLE_FAN);
	        gl.glVertex3f(-20, -20, 0);
	        gl.glVertex3f(+20, -20, 0);
	        gl.glVertex3f(0, 20, 0);
	        gl.glEnd();
		}
		
	}

	private void setCamera(GL gl, GLU glu, float distance) {
        // Change to projection matrix.
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);

        // Change back to model view matrix.
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
       
    }

	

}
