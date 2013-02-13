package name.wadewalker.shader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import junit.framework.Assert;

//==============================================================================
/**
 * A set of utility functions to help create shaders and check for errors. These
 * are functions instead of objects to help make it clear (for pedagogical reasons)
 * which GL functions are being called.
 *
 * @author Wade Walker
 */
public class ShaderUtils {

    /** Number of bytes in a GL_FLOAT. */
    public static int siGLFloatBytes = 4;

    //==============================================================================
    /**
     * Reads a shader from a file, creates it, compiles it, and attaches it to the program.
     * @param gl Used to make GL calls.
     * @param iProgram Program object index.
     * @param sShaderPath Path to shader file from current working directory (including file name).
     * @param iShaderType Shader type (GL_VERTEX_SHADER or GL_FRAGMENT_SHADER).
     * @return the shader index of the new shader.
     */
    public static int createAndAttachShader( GL2ES2 gl, int iProgram, String sShaderPath, int iShaderType ) {

        StringBuffer sbShader = new StringBuffer();
        try {
            BufferedReader bufferedreader = new BufferedReader( new FileReader( new File( sShaderPath ) ) );
            String sLine = null;
            while( (sLine = bufferedreader.readLine()) != null ) {
                sbShader.append( sLine + "\n" );
            }
        }
        catch( IOException ioexception ) {
            Assert.assertTrue( ioexception.getMessage(), false );
        }

        int iShader = gl.glCreateShader( iShaderType );
        checkGLError( gl, "glCreateShader" );

        gl.glShaderSource(iShader, 1, new String [] {sbShader.toString()}, new int [] {sbShader.length()}, 0 );
        checkGLError( gl, "glShaderSource" );

        gl.glCompileShader( iShader );
        checkGLError( gl, "glCompileShader" );

        int [] ai = new int [1];
        gl.glGetShaderiv( iShader, GL2ES2.GL_COMPILE_STATUS, ai, 0 );
        Assert.assertTrue( "Shader " + sShaderPath + " status invalid: "+ getInfoLog( gl, iShader, true ), ai[0] == 1 );

        gl.glAttachShader( iProgram, iShader );
        checkGLError( gl, "glAttachShader" );

        return( iShader );
    }

    //==============================================================================
    /**
     * Creates a buffer object, fills it with floats, and sets attribute pointers on it.
     *
     * @param gl Used to make GL calls.
     * @param iProgram Program object index.
     * @param asAttributeNames Names of attributes to set pointers for.
     * @param aiAttributeComponents Number of float components in each attribute.
     * @param aiStartOffsets Number of float components to skip before first component of each attribute.
     * @param aiVertexBuffer Returned index of buffer object.
     * @param aiAttributeLocations Returned attribute indexes.
     * @param af Floats to put in buffer.
     * @return the float buffer that was used to set the buffer object's data.
     */
    public static FloatBuffer createAndFillBuffer( GL2ES2 gl, int iProgram, String [] asAttributeNames,
                                                   int [] aiAttributeComponents, int [] aiStartOffsets,
                                                   int [] aiVertexBuffer, int [] aiAttributeLocations, float [] af ) {

        Assert.assertTrue( "Arrays of attribute names, components, and locations must be of equal length.",
                (asAttributeNames.length == aiAttributeComponents.length)
                && (asAttributeNames.length == aiAttributeLocations.length) );

        gl.glGenBuffers( 1, aiVertexBuffer, 0 );

        gl.glBindBuffer( GL.GL_ARRAY_BUFFER, aiVertexBuffer[0] );
        checkGLError( gl, "glBindBuffer" );

        int iStrideComponents = 0;
        for( int i : aiAttributeComponents )
            iStrideComponents += i;
        
        ByteBuffer bytebuffer = ByteBuffer.allocateDirect( af.length * iStrideComponents * siGLFloatBytes );
        bytebuffer.order( ByteOrder.nativeOrder() );
        FloatBuffer floatbuffer = bytebuffer.asFloatBuffer();
        floatbuffer.put( af ).flip();
        gl.glBufferData( GL.GL_ARRAY_BUFFER, floatbuffer.limit() * siGLFloatBytes, floatbuffer, GL.GL_STATIC_DRAW );
        checkGLError( gl, "glBufferData" );

        for( int i = 0; i < asAttributeNames.length; i++ ) {
            aiAttributeLocations[i] = gl.glGetAttribLocation( iProgram, asAttributeNames[i] );
            Assert.assertTrue( "glGetAttribLocation failed: " + asAttributeNames[i], aiAttributeLocations[i] != -1  );
    
            gl.glVertexAttribPointer( aiAttributeLocations[i],
                                      aiAttributeComponents[i],
                                      GL.GL_FLOAT,
                                      false,
                                      iStrideComponents * siGLFloatBytes,
                                      aiStartOffsets[i] * siGLFloatBytes );
            checkGLError( gl, "glVertexAttribPointer" );
        }

        gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0);
        checkGLError( gl, "glBindBuffer" );

        return( floatbuffer );
    }

    //==============================================================================
    /**
     * Creates a texture and fills it with data.
     *
     * @param gl Used to make GL calls.
     * @param iProgram Program object index.
     * @param iWidth Width of texture (in texels).
     * @param iHeight Height of texture (in texels).
     * @param buffer Buffer containing the texels. If it's a ByteBuffer, we create an
     * RGB texture of GL_UNSIGNED_BYTE; if it's a FloatBuffer, we create an RGB texture
     * of GL_FLOAT.
     * @return the index of the created texture.
     */
    public static int createAndFillTexture( GL2ES2 gl, int iProgram, int iWidth, int iHeight, Buffer buffer ) {
        
        Assert.assertTrue( "Buffer must be ByteBuffer or FloatBuffer.", (buffer instanceof ByteBuffer) || (buffer instanceof FloatBuffer) );
        int iTextureType = buffer instanceof ByteBuffer ? GL2ES2.GL_UNSIGNED_BYTE : GL2ES2.GL_FLOAT;

        int [] aiTexture = new int [1];
        gl.glGenTextures( 1, aiTexture, 0 );
        Assert.assertTrue( "glGenTextures failed.", aiTexture[0] != 0 );

        gl.glBindTexture( GL2ES2.GL_TEXTURE_2D, aiTexture[0] );
        ShaderUtils.checkGLError( gl, "glBindTexture" );

        gl.glTexImage2D( GL2ES2.GL_TEXTURE_2D, 0, GL2ES2.GL_RGBA, iWidth, iHeight, 0, GL2ES2.GL_RGBA, iTextureType, buffer );
        ShaderUtils.checkGLError( gl, "glTexImage2D" );

        gl.glTexParameteri( GL2ES2.GL_TEXTURE_2D, GL2ES2.GL_TEXTURE_MIN_FILTER, GL2ES2.GL_NEAREST );
        gl.glTexParameteri( GL2ES2.GL_TEXTURE_2D, GL2ES2.GL_TEXTURE_MAG_FILTER, GL2ES2.GL_NEAREST );

        gl.glBindTexture( GL2ES2.GL_TEXTURE_2D, 0 );
        ShaderUtils.checkGLError( gl, "glBindTexture" );

        return( aiTexture[0] );
    }

    //==============================================================================
    /**
     * Gets the info log for a shader or program.
     * @param gl Used to make GL calls.
     * @param iShaderOrProgram Shader or program object index.
     * @param bIsShader If true, index is for a shader; else it's for a program.
     * @return the info log as a string, or a message that there is no info log.
     */
    protected static String getInfoLog( GL2ES2 gl, int iShaderOrProgram, boolean bIsShader ) {

        int [] aiInfoLogLength = new int [1];
        gl.glGetShaderiv( iShaderOrProgram, GL2ES2.GL_INFO_LOG_LENGTH, aiInfoLogLength, 0 );

        if( aiInfoLogLength[0] == 0 )
            return "(no info log)";

        int [] aiCharsWritten = new int [1];
        byte [] abInfoLog = new byte [aiInfoLogLength[0]];
        if( bIsShader )
            gl.glGetShaderInfoLog( iShaderOrProgram, aiInfoLogLength[0], aiCharsWritten, 0, abInfoLog, 0 );
        else
            gl.glGetProgramInfoLog( iShaderOrProgram, aiInfoLogLength[0], aiCharsWritten, 0, abInfoLog, 0 );

        return( new String( abInfoLog, 0, aiCharsWritten[0] ) );
    }

    //==============================================================================
    /**
     * Checks that a program is valid. Throws an exception if it isn't.
     * @param gl Used to make GL calls.
     * @param iProgram Program object index.
     */
    public static void checkProgramValid( GL2ES2 gl, int iProgram ) {

        if( !gl.glIsProgram( iProgram ) )
            Assert.assertTrue( "Program name invalid: "+ iProgram, false );

        if( !isProgramStatusValid( gl, iProgram, GL2ES2.GL_LINK_STATUS ) )
            Assert.assertTrue( "Program link failed: " + iProgram + "\n\t" + getInfoLog( gl, iProgram, false ), false );

        gl.glValidateProgram( iProgram );
        if( !isProgramStatusValid( gl, iProgram, GL2ES2.GL_VALIDATE_STATUS ) )
            Assert.assertTrue( "Program validation failed: " + iProgram + "\n\t" + getInfoLog( gl, iProgram, false ), false );
    }

    //==============================================================================
    /**
     * @param gl Used to make GL calls.
     * @param iProgram Program object index.
     * @param iStatusType Type of status to check (GL_DELETE_STATUS, GL_LINK_STATUS, or GL_VALIDATE_STATUS).
     * @return true if the status is valid, false otherwise.
     */
    protected static boolean isProgramStatusValid( GL2ES2 gl, int iProgram, int iStatusType ) {

        int [] iresult = new int [1];
        gl.glGetProgramiv( iProgram, iStatusType, iresult, 0 );
        return( iresult[0] == 1 );
    }

    //==============================================================================
    /**
     * Checks if there have been any GL errors. Throws an exception with the
     * error codes in the message if there have been.
     * @param gl Used to make GL calls.
     * @param sFuncName Name of GL function we're checking for errors after.
     */
    public static void checkGLError( GL2ES2 gl, String sFuncName ) {

        int iError;
        StringBuilder sb = null;
        do {
            iError = gl.glGetError();
            if( iError != GL.GL_NO_ERROR ) {
                if( sb == null )
                    sb = new StringBuilder();
                sb.append( sFuncName + " failed, GL error: 0x" + Integer.toHexString( iError ) + "\n" );
            }
        }
        while( iError != GL.GL_NO_ERROR );

        if( sb != null )
            Assert.assertTrue( sb.toString(), false  );
    }
}
