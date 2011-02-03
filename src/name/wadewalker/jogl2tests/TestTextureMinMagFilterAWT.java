package name.wadewalker.jogl2tests;

import com.jogamp.opengl.test.junit.util.UITestCase;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.Frame;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for user problem in forum.
 * @author Wade Walker
 */
public class TestTextureMinMagFilterAWT extends UITestCase {
    static GLProfile glprofile;
    static GLCapabilities glcapabilities;
    InputStream inputstreamTexture;

    @BeforeClass
    public static void initClass() {
        GLProfile.initSingleton(true);
        glprofile = GLProfile.get(GLProfile.GL2GL3);
        Assert.assertNotNull(glprofile);
        glcapabilities = new GLCapabilities(glprofile);
        Assert.assertNotNull(glcapabilities);
    }

    @Before
    public void initTest() {
        inputstreamTexture = TestTextureMinMagFilterAWT.class.getResourceAsStream( "grayscale_texture.png" );
        Assert.assertNotNull(inputstreamTexture);
    }

    @After
    public void cleanupTest() {
        inputstreamTexture=null;
    }

    @Test
    public void test1() throws InterruptedException {
        GLCanvas glcanvas = new GLCanvas(glcapabilities);

        Frame frame = new Frame("Texture Test");
        Assert.assertNotNull(frame);
        frame.add(glcanvas);
        frame.setSize( 256, 128 );

        // load texture from file inside current GL context to match the way
        // the bug submitter was doing it
        glcanvas.addGLEventListener(new GLEventListener() {
            private GLU glu = new GLU();
            private Texture texture;

            @Override
            public void init(GLAutoDrawable drawable) {
                try {
                    texture = TextureIO.newTexture( inputstreamTexture, true, TextureIO.PNG );
                    texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                    texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                }
                catch(GLException glexception) {
                    glexception.printStackTrace();
                    Assume.assumeNoException(glexception);
                }
                catch(IOException ioexception) {
                    ioexception.printStackTrace();
                    Assume.assumeNoException(ioexception);
                }
            }

            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                GL2 gl = drawable.getGL().getGL2();
                gl.glMatrixMode(GL2ES1.GL_PROJECTION);
                gl.glLoadIdentity();
                glu.gluOrtho2D(0, 1, 0, 1);
                gl.glMatrixMode(GL2ES1.GL_MODELVIEW);
                gl.glLoadIdentity();
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {
                GL2 gl = drawable.getGL().getGL2();
                if(null!=texture) {
                    texture.disable();
                    texture.destroy(gl);
                }
            }

            @Override
            public void display(GLAutoDrawable drawable) {
                GL2 gl = drawable.getGL().getGL2();
            
                // Now draw one quad with the texture
                if(null!=texture) {
                    texture.enable();
                    texture.bind();
                    gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
                    TextureCoords coords = texture.getImageTexCoords();
                    gl.glBegin(GL2.GL_QUADS);
                    gl.glTexCoord2f(coords.left(), coords.bottom());
                    gl.glVertex3f(0, 0, 0);
                    gl.glTexCoord2f(coords.right(), coords.bottom());
                    gl.glVertex3f(1, 0, 0);
                    gl.glTexCoord2f(coords.right(), coords.top());
                    gl.glVertex3f(1, 1, 0);
                    gl.glTexCoord2f(coords.left(), coords.top());
                    gl.glVertex3f(0, 1, 0);
                    gl.glEnd();
                    texture.disable();
                }
            }
        });

        frame.setVisible(true);
        Thread.sleep(5000); // 500 ms
        frame.setVisible(false);
        frame.remove(glcanvas);
        glcanvas=null;
        Assert.assertNotNull(frame);
        frame.dispose();
        frame=null;
    }

    public static void main(String args[]) throws IOException {
        org.junit.runner.JUnitCore.main(TestTextureMinMagFilterAWT.class.getName());
    }
}
