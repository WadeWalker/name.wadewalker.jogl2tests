package name.wadewalker.jogl2tests;

import javax.media.opengl.GLProfile;

public class ShutdownErrorTest {
	public static void main(String[] args) { 
        GLProfile.getDefault();
        GLProfile.shutdown();
        System.exit(0);
	}
}
