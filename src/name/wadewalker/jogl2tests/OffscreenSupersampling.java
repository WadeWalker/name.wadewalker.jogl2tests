package name.wadewalker.jogl2tests;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.GLProfile;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings({ "serial", "deprecation" })
public class OffscreenSupersampling extends JFrame implements GLEventListener
{        
    GLPbuffer offScreenBuffer;
    int width;
    int height;
    
    OffscreenSupersampling(int width, int height) {

        this.setSize(width+100, height+100);
        this.setTitle("JOGL Sample Buffer Bug");
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        this.width = width;
        this.height = height;
        
       // GLProfile glp = GLProfile.getDefault();
        GLProfile glp = GLProfile.get(GLProfile.GL2);
        
        GLDrawableFactory fac = GLDrawableFactory.getFactory(glp);
        
//        boolean  d = fac.canCreateGLPbuffer(GLProfile.getDefaultDevice());
        
        GLCapabilities glCap = new GLCapabilities(glp);
        
        // COMMENTING OUT THIS LINE FIXES THE ISSUE. 
        // Setting this in JOGL1 works. Thus this is a JOGL2 issue.
        glCap.setSampleBuffers(true);
      
        // Without line below, there is an error on Windows.
        glCap.setDoubleBuffered(false);
        // Needed for drop shadows
        glCap.setStencilBits(1);
                                     
        //makes a new buffer
        offScreenBuffer = fac.createGLPbuffer(GLProfile.getDefaultDevice(), glCap, null, width, height, null);
        offScreenBuffer.addGLEventListener(this);        
        offScreenBuffer.display();
    }
    
    private void render(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);      

        // draw a triangle filling the window
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glColor3f(1, 0, 0);
        gl.glVertex2d(-1, -1);
        gl.glColor3f(0, 1, 0);
        gl.glVertex2d(0, 1);
        gl.glColor3f(0, 0, 1);
        gl.glVertex2d(1, -1);
        gl.glEnd();
    }
    

    @Override
    public void display(GLAutoDrawable arg0) {             
        render(offScreenBuffer);                               
        BufferedImage outputImage = com.jogamp.opengl.util.awt.Screenshot.readToBufferedImage(width, height, false);        
        ImageIcon imageIcon = new ImageIcon(outputImage);       
        JLabel imageLabel = new JLabel(imageIcon);        
        this.getContentPane().add(imageLabel);        
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {  
    }

    @Override
    public void init(GLAutoDrawable arg0) {                        
    }

    @Override
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
    }    
    
    public static void main(String args[]) {
        OffscreenSupersampling app = new OffscreenSupersampling(200,200);
        app.setVisible(true);
    }
}

