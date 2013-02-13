#version 110

#ifdef GL_ES
  precision mediump float;
  precision mediump int;
#endif

varying vec4 fragColor;

void main( void ) {
    gl_FragColor = fragColor;
}
