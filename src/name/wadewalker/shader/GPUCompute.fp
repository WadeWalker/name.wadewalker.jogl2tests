#version 110

#ifdef GL_ES
  precision mediump float;
  precision mediump int;
#endif

#pragma optimize(off)

// macros to allow insertion of extra vec4 multiplies and adds in the kernel
#define ADD4MULT4 * (v4ColorA + v4ColorB) * (v4ColorA + v4ColorB) * (v4ColorA + v4ColorB) * (v4ColorA + v4ColorB)

#define ADD8MULT8 ADD4MULT4\
                  ADD4MULT4

#define ADD16MULT16 ADD8MULT8\
                    ADD8MULT8

#define ADD32MULT32 ADD16MULT16\
                    ADD16MULT16

#define ADD64MULT64 ADD32MULT32\
                    ADD32MULT32

#define ADD128MULT128 ADD64MULT64\
                      ADD64MULT64

#define ADD256MULT256 ADD128MULT128\
                      ADD128MULT128

#define ADD512MULT512 ADD256MULT256\
                      ADD256MULT256

#define ADD640MULT640 ADD512MULT512\
                      ADD128MULT128

#define MULT4  * v4ColorA * v4ColorB * v4ColorA * v4ColorB

#define MULT8  * v4ColorA * v4ColorB * v4ColorA * v4ColorB * v4ColorA * v4ColorB * v4ColorA * v4ColorB

#define MULT16  * v4ColorA * v4ColorB * v4ColorA * v4ColorB * v4ColorA * v4ColorB * v4ColorA * v4ColorB\
                * v4ColorA * v4ColorB * v4ColorA * v4ColorB * v4ColorA * v4ColorB * v4ColorA * v4ColorB

#define MULT32 MULT16\
               MULT16

#define MULT64 MULT32\
               MULT32

#define MULT128 MULT64\
                MULT64

#define MULT256 MULT128\
                MULT128

#define MULT512 MULT256\
                MULT256

#define MULT640 MULT512\
                MULT128


#define ADD4  + v4ColorA + v4ColorB + v4ColorA + v4ColorB\

#define ADD8  + v4ColorA + v4ColorB + v4ColorA + v4ColorB + v4ColorA + v4ColorB + v4ColorA + v4ColorB

#define ADD16  + v4ColorA + v4ColorB + v4ColorA + v4ColorB + v4ColorA + v4ColorB + v4ColorA + v4ColorB\
               + v4ColorA + v4ColorB + v4ColorA + v4ColorB + v4ColorA + v4ColorB + v4ColorA + v4ColorB

#define ADD32 ADD16\
              ADD16

#define ADD64 ADD32\
              ADD32

#define ADD128 ADD64\
               ADD64

// 1/3 adds, 2/3 multiplies
#define ADD2MULT4 * v4ColorA * v4ColorA + v4ColorB * v4ColorA * v4ColorA + v4ColorB

#define ADD4MULT8 * v4ColorA * v4ColorA + v4ColorB * v4ColorA * v4ColorA + v4ColorB * v4ColorA * v4ColorA + v4ColorB * v4ColorA * v4ColorA + v4ColorB

#define ADD8MULT16 * v4ColorA * v4ColorB + v4ColorA * v4ColorB * v4ColorA + v4ColorB * v4ColorA * v4ColorB + v4ColorA * v4ColorB * v4ColorA + v4ColorB\
                   * v4ColorA * v4ColorB + v4ColorA * v4ColorB * v4ColorA + v4ColorB * v4ColorA * v4ColorB + v4ColorA * v4ColorB * v4ColorA + v4ColorB

#define ADD16MULT32 ADD8MULT16\
                    ADD8MULT16

#define ADD32MULT64 ADD16MULT32\
                    ADD16MULT32

#define ADD64MULT128 ADD32MULT64\
                     ADD32MULT64

#define ADD128MULT256 ADD64MULT128\
                      ADD64MULT128

#define MAD1MUL1  vAccum1 = vAccum1 * v4ColorA + v4ColorB;\
                  vAccum2 = vAccum2 * v4ColorA;
#define MAD2MUL2  vAccum1 = vAccum1 * v4ColorA + v4ColorB;\
                  vAccum2 = vAccum2 * v4ColorA;\
                  vAccum1 = vAccum1 * v4ColorA + v4ColorB;\
                  vAccum2 = vAccum2 * v4ColorB;
#define MAD4MUL4  MAD2MUL2\
                  MAD2MUL2
#define MAD8MUL8  MAD4MUL4\
                  MAD4MUL4
#define MAD16MUL16  MAD8MUL8\
                    MAD8MUL8
#define MAD32MUL32  MAD16MUL16\
                    MAD16MUL16
#define MAD64MUL64  MAD32MUL32\
                    MAD32MUL32
#define MAD128MUL128  MAD64MUL64\
                      MAD64MUL64
#define MAD256MUL256  MAD128MUL128\
                      MAD128MUL128
#define MAD512MUL512  MAD256MUL256\
                      MAD256MUL256

#define MUL1MUL1  vAccum1 = vAccum1 * v4ColorA;\
                  vAccum2 = vAccum2 * v4ColorB;
#define MUL2MUL2  vAccum1 = vAccum1 * v4ColorA;\
                  vAccum2 = vAccum2 * v4ColorB;\
                  vAccum1 = vAccum1 * v4ColorA;\
                  vAccum2 = vAccum2 * v4ColorB;
#define MUL4MUL4  MUL2MUL2\
                  MUL2MUL2
#define MUL8MUL8  MUL4MUL4\
                  MUL4MUL4
#define MUL16MUL16  MUL8MUL8\
                    MUL8MUL8
#define MUL32MUL32  MUL16MUL16\
                    MUL16MUL16
#define MUL64MUL64  MUL32MUL32\
                    MUL32MUL32
#define MUL128MUL128  MUL64MUL64\
                      MUL64MUL64
#define MUL256MUL256  MUL128MUL128\
                      MUL128MUL128
#define MUL512MUL512  MUL256MUL256\
                      MUL256MUL256

#define MUL2MUL2MUL2  vAccum1 = vAccum1 * v4ColorA;\
                      vAccum2 = vAccum2 * v4ColorB;\
                      vAccum3 = vAccum3 * v4ColorA;\
                      vAccum1 = vAccum1 * v4ColorB;\
                      vAccum2 = vAccum2 * v4ColorA;\
                      vAccum3 = vAccum3 * v4ColorB;
#define MUL4MUL4MUL4  MUL2MUL2MUL2\
                      MUL2MUL2MUL2
#define MUL8MUL8MUL8  MUL4MUL4MUL4\
                      MUL4MUL4MUL4
#define MUL16MUL16MUL16  MUL8MUL8MUL8\
                         MUL8MUL8MUL8
#define MUL32MUL32MUL32  MUL16MUL16MUL16\
                         MUL16MUL16MUL16
#define MUL64MUL64MUL64  MUL32MUL32MUL32\
                         MUL32MUL32MUL32
#define MUL128MUL128MUL128  MUL64MUL64MUL64\
                            MUL64MUL64MUL64
#define MUL256MUL256MUL256  MUL128MUL128MUL128\
                            MUL128MUL128MUL128
#define MUL512MUL512MUL512  MUL256MUL256MUL256\
                            MUL256MUL256MUL256

#define MAD1  vAccum1 = v4ColorA * v4ColorB + vAccum1;
#define MAD2  vAccum1 = v4ColorA * v4ColorB + vAccum1;\
              vAccum1 = v4ColorA * v4ColorB + vAccum1;
#define MAD4  MAD2\
              MAD2
#define MAD8  MAD4\
              MAD4
#define MAD16  MAD8\
               MAD8
#define MAD32  MAD16\
               MAD16
#define MAD64  MAD32\
               MAD32
#define MAD128  MAD64\
                MAD64
#define MAD256  MAD128\
                MAD128
#define MAD512  MAD256\
                MAD256


varying vec2 texCoord;
uniform float texInc;        // increment for texture coordinate that will cross it in K steps
uniform sampler2D textureA;  // K x K array
uniform sampler2D textureB;  // K x K array

void main() {
//    vec4 vAccum = vec4(0, 0, 0, 0);
    vec4 vAccum1 = vec4(0, 0, 0, 0);
//    vec4 vAccum2 = vec4(0, 0, 0, 0);
//    vec4 vAccum3 = vec4(0, 0, 0, 0);
    for( float p = 0.0; p < 1.0; p += texInc ) {
        vec4 v4ColorA =  texture2D( textureA, vec2( p, texCoord.t ) );
        vec4 v4ColorB =  texture2D( textureB, vec2( texCoord.s, p ) );
        // matrix multiply
//        gl_FragColor += (v4ColorA * v4ColorB);

//        gl_FragColor += (v4ColorA * v4ColorB
//            ADD512MULT512
//        );

//        gl_FragColor += (v4ColorA
//            ADD640MULT640
//        );
//        vAccum += (v4ColorA
//            MULT640
//        );

        MAD512
    }
//    gl_FragColor += vAccum;
    gl_FragColor = vAccum1;
//    gl_FragColor = vAccum1 + vAccum2;
//    gl_FragColor = vAccum1 + vAccum2 + vAccum3;
}
