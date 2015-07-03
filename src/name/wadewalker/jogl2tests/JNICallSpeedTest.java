package name.wadewalker.jogl2tests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.glu.GLU;

public class JNICallSpeedTest {

    public static void main(String [] args) {
        Display display = new Display();
        final Shell shell = new Shell( display );
        shell.setLayout( new FillLayout() );
        final Composite composite = new Composite( shell, SWT.NONE );
        composite.setLayout( new FillLayout() );

        GLData gldata = new GLData();
        gldata.doubleBuffer = true;
        // need SWT.NO_BACKGROUND to prevent SWT from clearing the window
        // at the wrong times (we use glClear for this instead)
        final GLCanvas glcanvas = new GLCanvas( composite, SWT.NO_BACKGROUND, gldata );
        glcanvas.setCurrent();
        GLProfile glprofile = GLProfile.get( GLProfile.GL2 );
        final GLContext glcontext = GLDrawableFactory.getFactory( glprofile ).createExternalGLContext();

        // fix the viewport when the user resizes the window
        glcanvas.addListener( SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                setup( glcanvas, glcontext );
            }
        });

        // draw the triangle when the OS tells us that any part of the window needs drawing
        glcanvas.addPaintListener( new PaintListener() {
            public void paintControl( PaintEvent paintevent ) {
                render( glcanvas, glcontext );
            }
        });

        shell.setText( "OneTriangle" );
        shell.setSize( 640, 480 );
        shell.open();

        while( !shell.isDisposed() ) {
            if( !display.readAndDispatch() )
                display.sleep();
        }

        glcanvas.dispose();
        display.dispose();
    }

    private static void setup( GLCanvas glcanvas, GLContext glcontext ) {
        Rectangle rectangle = glcanvas.getClientArea();

        glcanvas.setCurrent();
        glcontext.makeCurrent();

        GL2 gl = glcontext.getGL().getGL2();
        
        long lStart = System.currentTimeMillis();
        long lAccum = 0;
        int iCalls = 1000000;
        for( int i = 0; i < iCalls; i++ ) {
            String s = gl.glGetString( GL.GL_VERSION );
            lAccum += s.charAt( 0 );
        }
        long lElapsed = System.currentTimeMillis() - lStart;
        System.err.printf( "Calls per second: %f\n", ((double)iCalls) / (lElapsed / 1000.0) );
        System.err.printf( "Accumulator: %d\n", lAccum );

        gl.glMatrixMode( GL2.GL_PROJECTION );
        gl.glLoadIdentity();

        // coordinate system origin at lower left with width and height same as the window
        GLU glu = new GLU();
        glu.gluOrtho2D( 0.0f, rectangle.width, 0.0f, rectangle.height );

        gl.glMatrixMode( GL2.GL_MODELVIEW );
        gl.glLoadIdentity();

        gl.glViewport( 0, 0, rectangle.width, rectangle.height );
        glcontext.release();        
    }

    private static void render( GLCanvas glcanvas, GLContext glcontext ) {
        Rectangle rectangle = glcanvas.getClientArea();

        glcanvas.setCurrent();
        glcontext.makeCurrent();

        GL2 gl = glcontext.getGL().getGL2();
        gl.glClear( GL.GL_COLOR_BUFFER_BIT );

        // draw a triangle filling the window
        gl.glLoadIdentity();
        gl.glBegin( GL.GL_TRIANGLES );
        gl.glColor3f( 1, 0, 0 );
        gl.glVertex2f( 0, 0 );
        gl.glColor3f( 0, 1, 0 );
        gl.glVertex2f( rectangle.width, 0 );
        gl.glColor3f( 0, 0, 1 );
        gl.glVertex2f( rectangle.width / 2, rectangle.height );
        gl.glEnd();

        glcanvas.swapBuffers();
        glcontext.release();        
    }
}

