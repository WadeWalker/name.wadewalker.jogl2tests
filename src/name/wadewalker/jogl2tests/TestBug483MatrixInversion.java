package name.wadewalker.jogl2tests;

import jogamp.opengl.ProjectFloat;
import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests for bug 483, a failure of ProjectFloat.gluInvertMatrixf() to invert a specific matrix.
 *
 * @author Wade Walker (from with matrix data from forum user kolkoo)
 */
public class TestBug483MatrixInversion {

    @Test
    public void invertKolkooMatrix() {
//        String s = System.getProperty("user.home");
        float [] inverse = new float [4*4];
        boolean inverted = (new ProjectFloat()).gluInvertMatrixf(
            new float [] { -0.8184068703526464f, -0.7313346429703671f,     0.8265999715430894f,      0.8265727758407593f, 
                            0.0f,                 2.266778784171848f,      0.3441178170910746f,      0.3441064953804016f,
                            1.5188486310796492f, -0.3940677440777307f,     0.44539996103581814f,     0.44538530707359314f,
                          -29.837189131809282f,   7.880600278695056f,  31788.731450702064f,      31789.685546875f},
            0,
            inverse,
            0 );
        Assert.assertTrue( inverted );
    }

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main( TestBug483MatrixInversion.class.getName() );
    }
}
