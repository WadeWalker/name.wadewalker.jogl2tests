package name.wadewalker.jogl2tests;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.gl2.GLUgl2;

import org.junit.Test;

public class GluScaleImage implements GLEventListener {

    @Override
    public void init(GLAutoDrawable drawable) {
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        int widthin = 559;
        int heightin = 425;
        
        int widthout = 1024;
        int heightout = 512;
        
        int textureInLength = widthin * heightin * 4;
        int textureOutLength = widthout * heightout * 4;
        
        byte[] datain = new byte[textureInLength];
        byte[] dataout = new byte[textureOutLength];
        
        ByteBuffer bufferIn  = ByteBuffer.wrap(datain);
        ByteBuffer bufferOut = ByteBuffer.wrap(dataout);      
        GLUgl2 glu = new GLUgl2();
        glu.gluScaleImage( GL.GL_RGBA,
                           widthin, heightin, GL.GL_UNSIGNED_BYTE, bufferIn,
                           widthout, heightout, GL.GL_UNSIGNED_BYTE, bufferOut );
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }


    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Test
    public void test01() {
        Frame frame = new Frame("Test");
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glCapabilities = new GLCapabilities(glprofile);
        final GLCanvas canvas = new GLCanvas(glCapabilities);
        frame.setSize(256, 256);
        frame.add(canvas);
        frame.setVisible( true );
        canvas.addGLEventListener( this );

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit(0);
            }
        });
        canvas.display();
   }

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(GluScaleImage.class.getName());
    }
}
