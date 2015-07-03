package name.wadewalker.jogl2tests.onetriangle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;

/**
 * A minimal program that draws with JOGL in an SWT Composite.
 *
 * @author Wade Walker
 */
public class OneTriangleSWTGDCDenial {

    static {
        // setting this true causes window events not to get sent on Linux if you run from inside Eclipse
        GLProfile.initSingleton();
    }

    public static void main( String [] args ) {
        Display display = new Display();
        final Shell shell = new Shell( display );
        shell.setText( "OneTriangle SWT" );
        shell.setLayout( new FillLayout() );
        shell.setSize( 640, 480 );

        final Composite composite = new Composite( shell, SWT.NONE );
        composite.setLayout( new FillLayout() );

        GLData gldata = new GLData();
        gldata.doubleBuffer = true;
        // need SWT.NO_BACKGROUND to prevent SWT from clearing the window
        // at the wrong times (we use glClear for this instead)
        final GLCanvas glcanvas = new GLCanvas( composite, SWT.NO_BACKGROUND, gldata );
        glcanvas.setCurrent();
        GLProfile glprofile = GLProfile.getDefault();
        
        Shell shell2 = new Shell( display );
        shell2.addPaintListener( new PaintListener() {
            public void paintControl( PaintEvent paintevent ) {
                Rectangle clientArea = shell.getClientArea();
                paintevent.gc.drawLine( 0, 0, clientArea.width, clientArea.height );
            } 
        });
        shell2.setText( "2" );
        shell2.setLayout( new FillLayout() );
        shell2.setSize( 640, 480 );
        shell2.open();
        shell2.redraw();

// comment these lines in for test -- only compiles on Win32
//        long hDC = OS.GetDC( glcanvas.handle ); 
        final GLContext glcontext = GLDrawableFactory.getFactory( glprofile ).createExternalGLContext();
//        OS.ReleaseDC(glcanvas.handle, hDC); 


        // fix the viewport when the user resizes the window
        glcanvas.addListener( SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                Rectangle rectangle = glcanvas.getClientArea();
                glcanvas.setCurrent();
                glcontext.makeCurrent();
                OneTriangle.setup( glcontext.getGL().getGL2(), rectangle.width, rectangle.height );
                glcontext.release();        
            }
        });

        // draw the triangle when the OS tells us that any part of the window needs drawing
        glcanvas.addPaintListener( new PaintListener() {
            public void paintControl( PaintEvent paintevent ) {
                Rectangle rectangle = glcanvas.getClientArea();
                glcanvas.setCurrent();
                glcontext.makeCurrent();
                OneTriangle.render(glcontext.getGL().getGL2(), rectangle.width, rectangle.height);
                glcanvas.swapBuffers();
                glcontext.release();        
            }
        });

        shell.open();

        while( !shell.isDisposed() ) {
            if( !display.readAndDispatch() )
                display.sleep();
        }

        glcanvas.dispose();
        display.dispose();
    }
}

