package name.wadewalker.jogl2tests.onetriangle;

import java.applet.*;
import java.awt.*;

import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

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
                OneTriangle.render( glautodrawable.getGL().getGL2(), glautodrawable.getWidth(), glautodrawable.getHeight() );
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
