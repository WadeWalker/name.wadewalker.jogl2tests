package name.wadewalker.jogl2tests;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.test.junit.jogl.demos.gl2.Gears;
import com.jogamp.opengl.util.Animator;

public class GearsApp {

    public static void main( String [] args ) {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        glcanvas.addGLEventListener( new Gears() );

        Animator animator = new Animator();
        animator.add( glcanvas );

        final Frame frame = new Frame( "Gears app" );
        frame.add( glcanvas );
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                frame.remove( glcanvas );
                frame.dispose();
                System.exit( 0 );
            }
        });

        frame.setSize( 640, 480 );
        frame.setVisible( true );
        
        animator.setUpdateFPSFrames(1, null);
        animator.start();
    }
}
