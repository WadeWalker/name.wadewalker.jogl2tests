package name.wadewalker.shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import org.junit.Assert;

//==============================================================================
/**
 * Draws a square with a random texture on it.
 * @author Wade Walker
 */
public class TextureSquare implements GLEventListener {

    /** Width of random texture. */
    private static final int siTexWidth = 32;

    /** Height of random texture. */
    private static final int siTexHeight = 32;
    
    /** Program object index. */
    private int iProgram;
    
    /** Vertex shader index. */
    private int iVertexShader;

    /** Fragment shader index. */
    private int iFragmentShader;

    /** Texture index. */
    private int iTexture;
    
    /** Index of the vertex attribute (input to the vertex shader). */
    private int iVertexAttributeLocation;

    /** Index of the texture coordinate attribute (input to the vertex shader). */
    private int iTexCoordAttributeLocation;

    /** Index of the texture sampler (input to the pixel shader). */
    private int iTextureSamplerLocation;

    //==============================================================================
    /**
     * Constructor.
     */
    public TextureSquare() {
    }

    //==============================================================================
    /**
     * Creates the shaders, vertex buffer, and texture.
     * @param glautodrawable Used to get the GL object.
     * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
     */
    public void init( GLAutoDrawable glautodrawable ) {
        GL2ES2 gl = glautodrawable.getGL().getGL2ES2();

        iProgram = gl.glCreateProgram();
        Assert.assertTrue( "glCreateProgram failed.", iProgram != 0 );

        iVertexShader = ShaderUtils.createAndAttachShader( gl, iProgram, "src/name/wadewalker/shader/TextureSquare.vp", GL2ES2.GL_VERTEX_SHADER );
        iFragmentShader = ShaderUtils.createAndAttachShader( gl, iProgram, "src/name/wadewalker/shader/TextureSquare.fp", GL2ES2.GL_FRAGMENT_SHADER );

        gl.glLinkProgram( iProgram );
        ShaderUtils.checkProgramValid( gl, iProgram );

        gl.glUseProgram( iProgram );

        int [] aiVertexBuffer = new int [1];
        int [] aiLocation = new int [2];
        ShaderUtils.createAndFillBuffer( gl, iProgram,
            new String [] {"vVertex", "vTexCoord"}, new int [] {3, 2}, new int [] {0, 3},
            aiVertexBuffer, aiLocation,
            new float [] {-0.5f,  0.5f,  0.0f,    // vVertex 0
                           0.0f,  1.0f,           // vTexCoord 0
                           0.5f,  0.5f,  0.0f,    // vVertex 1
                           1.0f,  1.0f,           // vTexCoord 1
                          -0.5f, -0.5f,  0.0f,    // vVertex 2
                           0.0f,  0.0f,           // vTexCoord 2
                           0.5f, -0.5f,  0.0f,    // vVertex 3
                           1.0f,  0.0f} );        // vTexCoord 3

        iVertexAttributeLocation = aiLocation[0];
        iTexCoordAttributeLocation = aiLocation[1];

        ByteBuffer bytebuffer = createRandomByteTexture( siTexWidth, siTexHeight );
        iTexture = ShaderUtils.createAndFillTexture( gl, iProgram, siTexWidth, siTexHeight, bytebuffer );
        
        iTextureSamplerLocation = gl.glGetUniformLocation ( iProgram, "texture" );
        Assert.assertTrue( "glGetUniformLocation failed.", iTextureSamplerLocation != -1 );
        ShaderUtils.checkGLError( gl, "glGetUniformLocation" );

        gl.glClearColor( 0, 0, 0, 1 );
        gl.glEnable( GL2ES2.GL_DEPTH_TEST );
        gl.glUseProgram( 0 );
    }

    //==============================================================================
    /**
     * Creates a buffer filled with random bytes of length width x height.
     * @param iWidth Width of buffer.
     * @param iHeight Height of buffer.
     * @return the new buffer.
     */
    private ByteBuffer createRandomByteTexture( int iWidth, int iHeight ) {

        Random random = new Random();
        ByteBuffer bytebuffer = ByteBuffer.allocateDirect( iWidth * iHeight * 4 );
        bytebuffer.order( ByteOrder.nativeOrder() );

        byte [] ab = new byte [4];
        for( int iH = 0; iH < iHeight; iH++ ) {
            for( int iW = 0; iW < iWidth; iW++ ) {
                random.nextBytes( ab );
                bytebuffer.put( ab );
            }
        }
        bytebuffer.flip();
        return( bytebuffer );
    }

    //==============================================================================
    /**
     * Draws the textured square.
     * @param glautodrawable Used to get the GL object.
     * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
     */
    public void display( GLAutoDrawable glautodrawable ) {

        GL2ES2 gl = glautodrawable.getGL().getGL2ES2();
        gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

        gl.glUseProgram( iProgram );
        gl.glEnableVertexAttribArray( iVertexAttributeLocation );
        gl.glEnableVertexAttribArray( iTexCoordAttributeLocation );

        gl.glActiveTexture( GL2ES2.GL_TEXTURE0 );
        gl.glBindTexture( GL2ES2.GL_TEXTURE_2D, iTexture );
        gl.glUniform1i( iTextureSamplerLocation, 0 );

        gl.glDrawArrays( GL.GL_TRIANGLE_STRIP, 0, 4 );

        gl.glDisableVertexAttribArray( iVertexAttributeLocation );
        gl.glDisableVertexAttribArray( iTexCoordAttributeLocation );
        gl.glUseProgram( 0 );
    }

    //==============================================================================
    /**
     * Not used, since we're drawing in window coordinates.
     * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
     */
    public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
    }

    //==============================================================================
    /**
     * Cleans up GL objects.
     * @param glautodrawable Used to get the GL object.
     * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
     */
    public void dispose( GLAutoDrawable glautodrawable ) {

        GL2ES2 gl = glautodrawable.getGL().getGL2ES2();
        gl.glDeleteTextures( 1, new int [] {iTexture}, 0 );
        gl.glDetachShader( iProgram, iVertexShader );
        gl.glDeleteShader( iVertexShader );
        gl.glDetachShader( iProgram, iFragmentShader );
        gl.glDeleteShader( iFragmentShader );
        gl.glDeleteProgram( iProgram );
    }
}
