package name.wadewalker.shader;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.junit.Assert;

public class GPUComputeSwingGLCanvas {

    public static void main(String args[]) {
//        GLProfile glprofile = GLProfile.getGL2ES2();
        GLProfile glprofile = GLProfile.getDefault();
        Assert.assertNotNull( glprofile );
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        glcapabilities.setAlphaBits( 8 );  // so I get four full channels in the frame buffer
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        glcanvas.addGLEventListener( new GPUCompute() );

        final JFrame jframe = new JFrame( "GPUCompute Swing GLCanvas" ); 
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose();
                System.exit( 0 );
            }
        });

        jframe.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jframe.setSize( 400, 400 );
        jframe.setVisible( true );

        for( int i = 0; i < 10; i++ )
            glcanvas.display();

        System.exit( 0 );
    }
}