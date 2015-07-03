package name.wadewalker.shader;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import org.junit.Assert;

//==============================================================================
/**
 * Draws a square with a different color at each vertex.
 *
 * @author Wade Walker
 */
public class OneSquare implements GLEventListener {

    /** Program object index. */
    private int iProgram;

    /** Vertex shader index. */
    private int iVertexShader;
    
    /** Fragment shader index. */
    private int iFragmentShader;

    /** Index of the vertex attribute (input to the vertex shader). */
    private int iVertexAttributeLocation;
    
    /** Index of the color attribute (input to the vertex shader). */
    private int iColorAttributeLocation;

    //==============================================================================
    /**
     * Constructor.
     */
    public OneSquare() {
    }

    //==============================================================================
    /**
     * Creates the shaders and vertex buffer.
     * @param glautodrawable Used to get the GL object.
     * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
     */
    public void init( GLAutoDrawable glautodrawable ) {
        GL2ES2 gl = glautodrawable.getGL().getGL2ES2();

        iProgram = gl.glCreateProgram();
        Assert.assertTrue( "glCreateProgram failed.", iProgram != 0 );

        iVertexShader = ShaderUtils.createAndAttachShader( gl, iProgram, "src/name/wadewalker/shader/OneSquare.vp", GL2ES2.GL_VERTEX_SHADER );
        iFragmentShader = ShaderUtils.createAndAttachShader( gl, iProgram, "src/name/wadewalker/shader/OneSquare.fp", GL2ES2.GL_FRAGMENT_SHADER );

        gl.glLinkProgram( iProgram );
        ShaderUtils.checkProgramValid( gl, iProgram );

        gl.glUseProgram( iProgram );

        int [] aiVertexBuffer = new int [1];
        int [] aiLocation = new int [1];
        ShaderUtils.createAndFillBuffer( gl, iProgram, new String [] {"vVertex"}, new int [] {3}, new int [] {0},
            aiVertexBuffer, aiLocation,
            new float [] {-0.5f,  0.5f,  0.0f,
                           0.5f,  0.5f,  0.0f,
                          -0.5f, -0.5f,  0.0f,
                           0.5f, -0.5f,  0.0f} );
        iVertexAttributeLocation = aiLocation[0];

        ShaderUtils.createAndFillBuffer( gl, iProgram, new String [] {"vColor"}, new int [] {3}, new int [] {0},
            aiVertexBuffer, aiLocation,
            new float [] {1, 0, 0,
                          0, 1, 0,
                          0, 0, 1,
                          1, 1, 1} );
        iColorAttributeLocation = aiLocation[0];

        gl.glClearColor( 0, 0, 0, 1 );
        gl.glEnable( GL2ES2.GL_DEPTH_TEST );
        gl.glUseProgram( 0 );
    }

    //==============================================================================
    /**
     * Draws the square.
     * @param glautodrawable Used to get the GL object.
     * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
     */
    public void display( GLAutoDrawable glautodrawable ) {

        GL2ES2 gl = glautodrawable.getGL().getGL2ES2();
        gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

        gl.glUseProgram( iProgram );
        gl.glEnableVertexAttribArray( iVertexAttributeLocation );
        gl.glEnableVertexAttribArray( iColorAttributeLocation );

        gl.glDrawArrays( GL.GL_TRIANGLE_STRIP, 0, 4 );

        gl.glDisableVertexAttribArray( iVertexAttributeLocation );
        gl.glDisableVertexAttribArray( iColorAttributeLocation );
        gl.glUseProgram( 0 );
    }

    //==============================================================================
    /**
     * Not used, since we're drawing in window coordinates.
     * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
     */
    public void reshape( GLAutoDrawable glautodrawable, int iX, int iY, int iWidth, int iHeight ) {
    }

    //==============================================================================
    /**
     * Cleans up GL objects.
     * @param glautodrawable Used to get the GL object.
     * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
     */
    public void dispose( GLAutoDrawable glautodrawable ) {

        GL2ES2 gl = glautodrawable.getGL().getGL2ES2();
        gl.glDetachShader( iProgram, iVertexShader );
        gl.glDeleteShader( iVertexShader );
        gl.glDetachShader( iProgram, iFragmentShader );
        gl.glDeleteShader( iFragmentShader );
        gl.glDeleteProgram( iProgram );
    }
}
