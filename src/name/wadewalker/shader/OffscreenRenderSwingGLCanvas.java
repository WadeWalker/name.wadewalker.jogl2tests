package name.wadewalker.shader;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.junit.Assert;

public class OffscreenRenderSwingGLCanvas {

    public static void main(String args[]) {
        GLProfile glprofile = GLProfile.getGL2ES2(); 
        Assert.assertNotNull( glprofile );
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        GLCanvas glcanvas = new GLCanvas( glcapabilities );

        glcanvas.addGLEventListener( new GPUCompute() );

        final JFrame jframe = new JFrame( "OffscreenRender Swing GLCanvas" ); 
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose();
                System.exit( 0 );
            }
        });

        jframe.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jframe.setSize( 640, 480 );
        jframe.setVisible( true );
    }
}