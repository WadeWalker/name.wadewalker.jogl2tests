package name.wadewalker.jogl2tests.onetriangle;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.JWindow;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A minimal program that draws with JOGL in a Swing JWindow using the AWT GLCanvas.
 *
 * @author Wade Walker
 */
public class OneTriangleSwingGLCanvasJWindow {

    static {
        // setting this true causes window events not to get sent on Linux if you run from inside Eclipse
        GLProfile.initSingleton();
    }

    public static void main( String [] args ) {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

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

        // "One Triangle Swing GLCanvas JWindow" 
        final JWindow jwindow = new JWindow(); 
        jwindow.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jwindow.dispose();
                System.exit( 0 );
            }
        });

        jwindow.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jwindow.setSize( 640, 480 );
        jwindow.setVisible( true );
    }
}
