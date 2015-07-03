package name.wadewalker.jogl2tests;

import javax.swing.*;
import java.awt.*;
import com.jogamp.opengl.util.gl2.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
/**
 * @author  Bill Clark
 */
@SuppressWarnings("serial")
public class TestBug494 extends JFrame implements GLEventListener {
    GLCanvas canvas; // OpenGL canvas
    GLCapabilities capabilities;
    String extensions; // Supported Opengl extensions
    String vendor;
    String version;
    String renderer;
    GLUT glut = new GLUT();
    GLU glu = new GLU();
    int spin = 0;

    public TestBug494() {
        GLProfile glp;
            if(GLProfile.isAvailable("GL3")){
                System.out.printf("GL3 is available");
                glp = GLProfile.get("GL2");
            }
            else{
                System.out.printf("GL3 is NOT available\n");
              glp = GLProfile.getDefault();     
          }
         System.out.printf("Profile: %s\n",glp.toString());
            GLCapabilities caps = new GLCapabilities(glp);
            canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);
      
        Container contentPane = getContentPane();
        contentPane.add(canvas); 
        setBounds(50,50,200,200);
        setTitle("JOGL2 Versions Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void Versions(GL2 gl, GLU glu)
    {
        // Get and print extensions
        extensions = gl.glGetString(GL.GL_EXTENSIONS);
        vendor = gl.glGetString(GL.GL_VENDOR);
        version = gl.glGetString(GL.GL_VERSION);
        renderer = gl.glGetString(GL.GL_RENDERER);

        System.out.println("Card Vendor: " + vendor);
        System.out.println("GL Version: " + version);
        System.out.println("Renderer: " + renderer+"\n");
    }


    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        Versions(gl, glu);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height){
    }

    public void display(GLAutoDrawable drawable){
    } 
   public void displayChanged (GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged){
   } 

    public void dispose(GLAutoDrawable drawable) {
    }
    
    public static void main(String[] args){
        new TestBug494();
    }
}
