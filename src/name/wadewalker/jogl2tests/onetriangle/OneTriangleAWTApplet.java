package name.wadewalker.jogl2tests.onetriangle;

import java.applet.*;
import java.awt.*;

import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * A minimal applet that draws with JOGL in a browser window.
 *
 * @author Wade Walker
 */
@SuppressWarnings("serial")
public class OneTriangleAWTApplet extends Applet {

    private GLAnimatorControl glanimatorcontrol;

    public void init() {
        GLProfile.initSingleton();
        setLayout( new BorderLayout() );

        final GLCanvas glcanvas = new GLCanvas();
        glcanvas.addGLEventListener( new GLEventListener() {
            
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                OneTriangle.setup( glautodrawable.getGL().getGL2(), width, height );
            }
            
            @Override
            public void init( GLAutoDrawable glautodrawable ) {
            }
            
            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {
            }
            
            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                OneTriangle.render( glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight() );
            }
        });
    
        glcanvas.setSize( getSize() );
        add( glcanvas, BorderLayout.CENTER );
        glanimatorcontrol = new FPSAnimator( glcanvas, 30 );
    }

    public void start() {
        glanimatorcontrol.start();
    }
    
    public void stop() {
        glanimatorcontrol.stop();
    }
    
    public void destroy() {
    }
}
