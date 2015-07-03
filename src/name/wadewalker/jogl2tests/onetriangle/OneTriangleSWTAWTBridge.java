package name.wadewalker.jogl2tests.onetriangle;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

/**
 * A minimal program that draws with JOGL in an SWT Composite using an
 * embedded AWT Frame and the AWT GLCanvas via the SWT-AWT bridge.
 *
 * @author Wade Walker
 */
public class OneTriangleSWTAWTBridge {

    static {
        GLProfile.initSingleton();
    }

    public static void main( String [] args ) {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        Display display = new Display();
        final Shell shell = new Shell( display );
        shell.setText( "OneTriangle SWT AWT Bridge" );
        shell.setLayout( new FillLayout() );
        shell.setSize( 640, 480 );

        final Composite composite = new Composite( shell, SWT.EMBEDDED );
        composite.setLayout( new FillLayout() );

        Frame frame = SWT_AWT.new_Frame( composite );
        frame.add( glcanvas );

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

        shell.open();

        while( !shell.isDisposed() ) {
            if( !display.readAndDispatch() )
                display.sleep();
        }

        display.dispose();
    }
}

