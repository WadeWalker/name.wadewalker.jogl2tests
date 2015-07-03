#version 110

#ifdef GL_ES
  precision mediump float;
  precision mediump int;
#endif

varying vec2 texCoord;
uniform sampler2D texture;

void main() {
    gl_FragColor = texture2D( texture, texCoord );
}
