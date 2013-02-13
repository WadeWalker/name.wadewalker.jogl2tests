package name.wadewalker.shader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import junit.framework.Assert;

//==============================================================================
/**
 * Does GPU computing in a fragment shader. Renders a square with two textures
 * mapped to it, and uses those texture values as inputs to a matrix multiplication.
 *
 * @author Wade Walker
 */
public class GPUCompute implements GLEventListener {

    /** Number of times to render the square. */
    private static final int siIterations = 1;

    /** Width and height of input texture. */
    private static final int siTexSize = 128;

    private static final double dExtraVec4Adds = 512.0;
    private static final double dExtraVec4Mults = 512.0;
    //                                            index    mults                             adds
    //                                            add      (vec4)                            (vec4)
//    private static final double sdFPOpsPerPixel = (1.0 +   (4.0 * (1.0 + dExtraVec4Mults)) + (4.0 * (1.0 + dExtraVec4Adds))) * siTexSize;
    private static final double sdFPOpsPerPixel = (1.0 +   (4.0 * (0.0 + dExtraVec4Mults)) + (4.0 * (0.0 + dExtraVec4Adds))) * siTexSize;

    // (accesses per pixel) * components * bytes per component
    private static final double sdTextureReadsPerPixel = 2.0 * siTexSize;
    private static final double sdTextureBytesReadPerPixel = sdTextureReadsPerPixel * 4.0 * 4.0;

    /** Program object index. */
    private int iProgram;
    
    /** Vertex shader index. */
    private int iVertexShader;

    /** Fragment shader index. */
    private int iFragmentShader;

    /** Texture index A. */
    private int iTextureA;

    /** Texture index B. */
    private int iTextureB;

    /** Framebuffer index. */
    private int iFB;

    /** Index of the vertex attribute (input to the vertex shader). */
    private int iVertexAttributeLocation;

    /** Index of the texture coordinate attribute (input to the vertex shader). */
    private int iTexCoordAttributeLocation;

    /** Index of texture sampler A (input to the pixel shader). */
    private int iTextureSamplerLocationA;

    /** Index of texture sampler B (input to the pixel shader). */
    private int iTextureSamplerLocationB;

    //==============================================================================
    /**
     * Constructor.
     */
    public GPUCompute() {
    }

    //==============================================================================
    /**
     * Creates the shaders, vertex buffer, and texture.
     * @param glautodrawable Used to get the GL object.
     * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
     */
    public void init( GLAutoDrawable glautodrawable ) {
        GL2ES2 gl = glautodrawable.getGL().getGL2ES2();

        int [] aiMaxTextureSize = new int[1];
        gl.glGetIntegerv( GL2ES2.GL_MAX_TEXTURE_SIZE, aiMaxTextureSize, 0 );
        Assert.assertTrue( "Texture size " + siTexSize + " bigger than max allowable size of " + aiMaxTextureSize[0] + ".", siTexSize <= aiMaxTextureSize[0] );

        iProgram = gl.glCreateProgram();
        Assert.assertTrue( "glCreateProgram failed.", iProgram != 0 );

        iVertexShader = ShaderUtils.createAndAttachShader( gl, iProgram, "src/name/wadewalker/shader/GPUCompute.vp", GL2ES2.GL_VERTEX_SHADER );
        iFragmentShader = ShaderUtils.createAndAttachShader( gl, iProgram, "src/name/wadewalker/shader/GPUCompute.fp", GL2ES2.GL_FRAGMENT_SHADER );

        gl.glLinkProgram( iProgram );
        ShaderUtils.checkProgramValid( gl, iProgram );

        gl.glUseProgram( iProgram );

        writeShader( gl, iProgram, "src/name/wadewalker/shader/shader.txt" );

        int [] aiVertexBuffer = new int [1];
        int [] aiLocation = new int [2];
        ShaderUtils.createAndFillBuffer( gl, iProgram,
            new String [] {"vVertex", "vTexCoord"}, new int [] {3, 2}, new int [] {0, 3},
            aiVertexBuffer, aiLocation,
            new float [] {-1.0f,  1.0f,  0.0f,    // vVertex 0
                           0.0f,  1.0f,           // vTexCoord 0
                           1.0f,  1.0f,  0.0f,    // vVertex 1
                           1.0f,  1.0f,           // vTexCoord 1
                          -1.0f, -1.0f,  0.0f,    // vVertex 2
                           0.0f,  0.0f,           // vTexCoord 2
                           1.0f, -1.0f,  0.0f,    // vVertex 3
                           1.0f,  0.0f} );        // vTexCoord 3

        iVertexAttributeLocation = aiLocation[0];
        iTexCoordAttributeLocation = aiLocation[1];

        // create texture A
        FloatBuffer floatbufferA = createRandomFloatTexture( siTexSize, siTexSize );
//        FloatBuffer floatbufferA = createConstFloatTexture( siTexSize, siTexSize, 0.5f );
        iTextureA = ShaderUtils.createAndFillTexture( gl, iProgram, siTexSize, siTexSize, floatbufferA );
        iTextureSamplerLocationA = gl.glGetUniformLocation ( iProgram, "textureA" );
        Assert.assertTrue( "glGetUniformLocation failed.", iTextureSamplerLocationA != -1 );
        ShaderUtils.checkGLError( gl, "glGetUniformLocation" );

        // create texture B
//        FloatBuffer floatbufferB = createConstFloatTexture( siTexSize, siTexSize, 0.01f );
        FloatBuffer floatbufferB = createDiagonalFloatTexture( siTexSize, siTexSize, 0.5f );
        iTextureB = ShaderUtils.createAndFillTexture( gl, iProgram, siTexSize, siTexSize, floatbufferB );
        iTextureSamplerLocationB = gl.glGetUniformLocation ( iProgram, "textureB" );
        Assert.assertTrue( "glGetUniformLocation failed.", iTextureSamplerLocationB != -1 );
        ShaderUtils.checkGLError( gl, "glGetUniformLocation" );

        // increment for texture coordinate to take it across one pixel
        float fTexCoordInc = 1.0f / ((float)siTexSize);
        int iTextureIncrementLocation = gl.glGetUniformLocation ( iProgram, "texInc" );
        gl.glUniform1f( iTextureIncrementLocation, fTexCoordInc );

        iFB = createFrameBuffer( gl, siTexSize, siTexSize );

        gl.glClearColor( 0, 0, 0, 1 );
        gl.glEnable( GL2ES2.GL_DEPTH_TEST );
        gl.glUseProgram( 0 );
    }

    //==============================================================================
    /**
     * Writes out a binary shader program as a text file.
     *
     * @param gl Used to make GL calls.
     * @param iProgramParam Index of program to write out.
     * @param sOutputFile File to write shader out to.
     */
    private void writeShader( GL2ES2 gl, int iProgramParam, String sOutputFile ) {

        int [] aiNumFormats = new int [1];
        gl.glGetIntegerv( GL2ES2.GL_NUM_PROGRAM_BINARY_FORMATS, aiNumFormats, 0 );
        ShaderUtils.checkGLError( gl, "glGetIntegerv" );
        Assert.assertTrue( "No binary formats found.", aiNumFormats[0] >= 1 );

        int [] aiProgramLength = new int [1];
        gl.glGetProgramiv( iProgramParam, GL2ES2.GL_PROGRAM_BINARY_LENGTH, aiProgramLength, 0 );
        ShaderUtils.checkGLError( gl, "glGetProgramiv" );

        int [] aiBinaryFormat = new int[1];
        ByteBuffer bytebufferProgram = ByteBuffer.allocateDirect( aiProgramLength[0] );
        bytebufferProgram.order( ByteOrder.nativeOrder() );
        int [] aiProgramLengthWritten = new int [1];
        gl.glGetProgramBinary( iProgramParam, aiProgramLength[0], aiProgramLengthWritten, 0, aiBinaryFormat, 0, bytebufferProgram );
        ShaderUtils.checkGLError( gl, "glGetProgramBinary" );
        
        FileOutputStream fileoutputstream = null;
        try {
            fileoutputstream = new FileOutputStream( sOutputFile );
            byte [] ab = new byte [bytebufferProgram.limit()];
            bytebufferProgram.get( ab );
            fileoutputstream.write( ab );
            fileoutputstream.close();
        }
        catch( IOException ioexception ){
            Assert.assertTrue( ioexception.getMessage(), false );
        }
    }

    //==============================================================================
    /**
     * Creates an RGBA buffer filled with random floats of length width x height.
     * @param iWidth Width of buffer.
     * @param iHeight Height of buffer.
     * @return the new buffer.
     */
    private FloatBuffer createRandomFloatTexture( int iWidth, int iHeight ) {

        Random random = new Random();
        ByteBuffer bytebuffer = ByteBuffer.allocateDirect( iWidth * iHeight * 4 * ShaderUtils.siGLFloatBytes );
        bytebuffer.order( ByteOrder.nativeOrder() );
        FloatBuffer floatbuffer = bytebuffer.asFloatBuffer();

        for( int iH = 0; iH < iHeight; iH++ ) {
            for( int iW = 0; iW < iWidth; iW++ ) {
                floatbuffer.put( random.nextFloat() );
                floatbuffer.put( random.nextFloat() );
                floatbuffer.put( random.nextFloat() );
                floatbuffer.put( random.nextFloat() );
            }
        }
        floatbuffer.flip();
        return( floatbuffer );
    }

    //==============================================================================
    /**
     * Creates an RGBA buffer filled with a constant float value of length width x height.
     * @param iWidth Width of buffer.
     * @param iHeight Height of buffer.
     * @param fConst Constant value to assign to every element of the buffer.
     * @return the new buffer.
     */
    private FloatBuffer createConstFloatTexture( int iWidth, int iHeight, float fConst ) {

        ByteBuffer bytebuffer = ByteBuffer.allocateDirect( iWidth * iHeight * 4 * ShaderUtils.siGLFloatBytes );
        bytebuffer.order( ByteOrder.nativeOrder() );
        FloatBuffer floatbuffer = bytebuffer.asFloatBuffer();

        for( int iH = 0; iH < iHeight; iH++ ) {
            for( int iW = 0; iW < iWidth; iW++ ) {
                floatbuffer.put( fConst );
                floatbuffer.put( fConst );
                floatbuffer.put( fConst );
                floatbuffer.put( fConst );
            }
        }
        floatbuffer.flip();
        return( floatbuffer );
    }

    //==============================================================================
    /**
     * Creates an RGBA buffer of length width x height with constant float values on
     * the diagonal, and zeros everywhere else.
     * @param iWidth Width of buffer.
     * @param iHeight Height of buffer.
     * @param fConst Constant value to assign to every element of the buffer.
     * @return the new buffer.
     */
    private FloatBuffer createDiagonalFloatTexture( int iWidth, int iHeight, float fConst ) {

        ByteBuffer bytebuffer = ByteBuffer.allocateDirect( iWidth * iHeight * 4 * ShaderUtils.siGLFloatBytes );
        bytebuffer.order( ByteOrder.nativeOrder() );
        FloatBuffer floatbuffer = bytebuffer.asFloatBuffer();

        for( int iH = 0; iH < iHeight; iH++ ) {
            for( int iW = 0; iW < iWidth; iW++ ) {
                if( iH == iW ) {
                    floatbuffer.put( fConst );
                    floatbuffer.put( fConst );
                    floatbuffer.put( fConst );
                    floatbuffer.put( fConst );
                }
                else {
                    floatbuffer.put( 0.0f );
                    floatbuffer.put( 0.0f );
                    floatbuffer.put( 0.0f );
                    floatbuffer.put( 0.0f );
                }
            }
        }
        floatbuffer.flip();
        return( floatbuffer );
    }

    //==============================================================================
    /**
     * Creates a framebuffer than can be used to render offscreen.
     * @param gl Used to make GL calls.
     * @param iWidth Width of framebuffer to create.
     * @param iHeight Height of framebuffer to create.
     * @return index of the framebuffer object.
     */
    private int createFrameBuffer( GL2ES2 gl, int iWidth, int iHeight ) {

        int [] aiFrameBuffer = new int [1];
        gl.glGenFramebuffers( 1, aiFrameBuffer, 0 );
        ShaderUtils.checkGLError( gl, "glGenFramebuffers" );

        gl.glBindFramebuffer( GL2ES2.GL_FRAMEBUFFER, aiFrameBuffer[0] );
        ShaderUtils.checkGLError( gl, "glBindFramebuffer" );

        int [] aiColorRenderBuffer = new int [1];
        gl.glGenRenderbuffers( 1, aiColorRenderBuffer, 0 ); 
        ShaderUtils.checkGLError( gl, "glGenRenderbuffers" );

        gl.glBindRenderbuffer( GL2ES2.GL_RENDERBUFFER, aiColorRenderBuffer[0] );
        ShaderUtils.checkGLError( gl, "glBindRenderbuffer" );

        gl.glRenderbufferStorage( GL2ES2.GL_RENDERBUFFER, GL2ES2.GL_RGBA, iWidth, iHeight );
        ShaderUtils.checkGLError( gl, "glRenderbufferStorage" );

        gl.glFramebufferRenderbuffer( GL2ES2.GL_FRAMEBUFFER, GL2ES2.GL_COLOR_ATTACHMENT0, GL2ES2.GL_RENDERBUFFER, aiColorRenderBuffer[0] );
        ShaderUtils.checkGLError( gl, "glFramebufferRenderbuffer" );

        Assert.assertTrue( "Framebuffer not complete", gl.glCheckFramebufferStatus( GL2ES2.GL_FRAMEBUFFER ) == GL2ES2.GL_FRAMEBUFFER_COMPLETE );
        gl.glBindFramebuffer( GL2ES2.GL_FRAMEBUFFER, 0 );

        return( aiFrameBuffer[0] );
    }

    //==============================================================================
    /**
     * Draws the textured square.
     * @param glautodrawable Used to get the GL object.
     * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
     */
    public void display( GLAutoDrawable glautodrawable ) {

        GL2ES2 gl = glautodrawable.getGL().getGL2ES2();

        // needed so fragment shader is called at same resolution as texture (otherwise it goes by
        // the window size, not the bound framebuffer size)
        gl.glViewport( 0, 0, siTexSize, siTexSize );

        gl.glBindFramebuffer( GL2ES2.GL_FRAMEBUFFER, iFB );
//        gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

        gl.glUseProgram( iProgram );
        gl.glEnableVertexAttribArray( iVertexAttributeLocation );
        gl.glEnableVertexAttribArray( iTexCoordAttributeLocation );

        gl.glActiveTexture( GL2ES2.GL_TEXTURE0 );
        gl.glBindTexture( GL2ES2.GL_TEXTURE_2D, iTextureA );
        gl.glUniform1i( iTextureSamplerLocationA, 0 );

        gl.glActiveTexture( GL2ES2.GL_TEXTURE1 );
        gl.glBindTexture( GL2ES2.GL_TEXTURE_2D, iTextureB );
        gl.glUniform1i( iTextureSamplerLocationB, 1 );

        long lStartTimeNS = System.nanoTime();
        for( int i = 0; i < siIterations; ++i )
            gl.glDrawArrays( GL.GL_TRIANGLE_STRIP, 0, 4 );
        gl.glFinish();  // force completion, or we'll just be timing the glDrawArrays function call
        long lEndTimeNS = System.nanoTime();
        double dTimeNS = (double)(lEndTimeNS - lStartTimeNS);
        
        double dPixels = (siTexSize * siTexSize) * siIterations;
        double dGPixelsPerSec = dPixels / dTimeNS;

        double dFLOPs = sdFPOpsPerPixel * dPixels;
        double dGFLOPsPerSec = dFLOPs / dTimeNS;

        double dTexBytes = sdTextureBytesReadPerPixel * dPixels;
        double dTexGBPerSec = dTexBytes / dTimeNS;
        
        double dTexReads = sdTextureReadsPerPixel * dPixels;
        double dGTexReadsPerSec = dTexReads / dTimeNS;
        
        System.out.printf( "Results: %f ms (%d x %d MM, %d times) %f GFLOPs/s  %f GB/s texture  %f Gpixels/s  %f Gtexels/s\n",
                dTimeNS / 1.0e6, siTexSize, siTexSize, siIterations, dGFLOPsPerSec, dTexGBPerSec, dGPixelsPerSec, dGTexReadsPerSec );

//        outputFrameBuffer( gl, siTexSize, siTexSize );

        gl.glDisableVertexAttribArray( iVertexAttributeLocation );
        gl.glDisableVertexAttribArray( iTexCoordAttributeLocation );
        gl.glUseProgram( 0 );
        gl.glBindFramebuffer( GL2ES2.GL_FRAMEBUFFER, 0 );
    }

    private void outputFrameBuffer( GL2ES2 gl, int iWidth, int iHeight ) {

      ByteBuffer bytebufferFB = ByteBuffer.allocateDirect( iWidth * iHeight * 4 );
      bytebufferFB.order( ByteOrder.nativeOrder() );

      gl.glReadPixels( 0, 0, iWidth, iHeight, GL2ES2.GL_RGBA, GL2ES2.GL_UNSIGNED_BYTE, bytebufferFB );
      ShaderUtils.checkGLError( gl, "glReadPixels" );

      byte [] ab = new byte [4];
      for( int iH = 0; iH < iHeight; iH++ ) {
          System.out.printf( "row %3d: ", iH );
          for( int iW = 0; iW < iWidth; iW++ ) {
              bytebufferFB.get( ab );
              System.out.printf( "%02x%02x%02x%02x ", ab[0], ab[1], ab[2], ab[3] );
          }
          System.out.println();
      }
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
        gl.glDeleteTextures( 2, new int [] {iTextureA, iTextureB}, 0 );
        gl.glDetachShader( iProgram, iVertexShader );
        gl.glDeleteShader( iVertexShader );
        gl.glDetachShader( iProgram, iFragmentShader );
        gl.glDeleteShader( iFragmentShader );
        gl.glDeleteProgram( iProgram );
    }
}
