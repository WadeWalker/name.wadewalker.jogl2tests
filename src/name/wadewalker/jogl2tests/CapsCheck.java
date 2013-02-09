package name.wadewalker.jogl2tests;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.media.nativewindow.AbstractGraphicsDevice;
import javax.media.opengl.DefaultGLCapabilitiesChooser;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesChooser;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.JoglVersion;

@SuppressWarnings("deprecation")
public class CapsCheck {
	public static void main(String[] args) { 
        System.err.println(JoglVersion.getInstance());
        
        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2)); 
        GLCanvas glc = new GLCanvas(caps); 
        GLDrawableFactory usine = glc.getFactory(); 
        GLCapabilitiesImmutable glci = glc.getChosenGLCapabilities(); 
        GLCapabilitiesChooser glcc = new DefaultGLCapabilitiesChooser(); 
        AbstractGraphicsDevice agd = usine.getDefaultDevice(); 
        
        GLPbuffer pbuffer = usine.createGLPbuffer(agd, glci, glcc, 256, 256, null); 
        GLContext context = pbuffer.getContext(); 
        context.makeCurrent(); 
        GL2 gl = pbuffer.getContext().getGL().getGL2(); 
        
        String extensions = gl.glGetString(GL.GL_EXTENSIONS); 
        String[] tabExtensions = extensions.split(" "); 
        SortedSet<String> setExtensions = new TreeSet<String>(); 
        Collections.addAll(setExtensions, tabExtensions); 
        System.out.println(setExtensions); 
    }
}