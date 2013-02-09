package name.wadewalker.jogl2tests;

import javax.media.opengl.GLProfile;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import com.jogamp.opengl.test.junit.jogl.awt.text.TextRendererGLEventListener01;
import com.jogamp.opengl.test.junit.util.UITestCase;

import java.awt.Frame;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Test;

/*
 * Unit tests for Bug464
 * Some ATI-Drivers crash the JVM if VBO-related glFunctions are called. This test checks 
 * if TextRenderer calls any of these functions while it's useVertexArray variable is set
 * to false.
 * 2D- and 3D-TextRendering is tested by creating a GLCanvas showing a simple line of text 
 * while filtering all glFunction calls by using a modified version of TraceGL2.
 * VBO-related function are logged to the disallowedMethodCalls String of the GLEventListener
 * instead of being executed (to prevent JVM crashes). Therefore, if the 
 * disallowedMethodCalls isn't an empty String after the test, the test fails.
 * 
 * Other classes related to this test:
 *   TestTextRendererGLEventListener01
 *   TestTextRendererTraceGL2Mock01
 */

public class TextRenderingOld extends UITestCase {
    static GLProfile glp;
    static GLCapabilities caps;
    
    private GLCanvas glCanvas;
    private Frame frame;

    @BeforeClass
    public static void initClass() {
        glp = GLProfile.get(GLProfile.GL2);
        Assert.assertNotNull(glp);
        caps = new GLCapabilities(glp);
        Assert.assertNotNull(caps);
    }

    @Before
    public void initTest() {
        glCanvas = new GLCanvas(caps);

        frame = new Frame("TextRenderer Test");
        Assert.assertNotNull(frame);
        frame.add(glCanvas);
        frame.setSize(512, 512);
        frame.setVisible(true);
        
    }

    @After
    public void cleanupTest() {
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    frame.setVisible(false);
                    frame.remove(glCanvas);
                    frame.dispose();
                }});
        } catch( Throwable throwable ) {
            throwable.printStackTrace();
            Assume.assumeNoException( throwable );
        }        
        glCanvas=null;
        frame=null;
    }

    @Test
    public void testTextRendererDraw2D() throws InterruptedException {

        TextRendererGLEventListener01 listener = new TextRendererGLEventListener01(1);
        Assert.assertNotNull(listener);
        glCanvas.addGLEventListener(listener);
        Animator animator = new Animator(glCanvas);

        animator.start();
    
        Thread.sleep(500); // 500 ms
    
        animator.stop();
        
        String disallowedMethods = listener.getDisallowedMethodCalls();
        if (!disallowedMethods.equals("")) {
            Assert.fail("Following VBO-related glMethods have been called: "+ disallowedMethods);
        }
    }
    
    @Test
    public void testTextRendererDraw3D() throws InterruptedException {

        TextRendererGLEventListener01 listener = new TextRendererGLEventListener01(2);
        Assert.assertNotNull(listener);
        glCanvas.addGLEventListener(listener);
        Animator animator = new Animator(glCanvas);

        animator.start();
    
        Thread.sleep(500); // 500 ms
    
        animator.stop();
        
        String disallowedMethods = listener.getDisallowedMethodCalls();
        if (!disallowedMethods.equals("")) {
            Assert.fail("Following VBO-related glMethods have been called: "+ disallowedMethods);
        }
    }

    public static void main(String args[]) throws IOException {
        String tstname = TextRenderingOld.class.getName();
        org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner.main(new String[] {
            tstname,
            "filtertrace=true",
            "haltOnError=false",
            "haltOnFailure=false",
            "showoutput=true",
            "outputtoformatters=true",
            "logfailedtests=true",
            "logtestlistenerevents=true",
            "formatter=org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter",
            "formatter=org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter,TEST-"+tstname+".xml" } );
    }    
}
