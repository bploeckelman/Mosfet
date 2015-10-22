#ifdef GL_ES 
precision mediump float;
#endif

varying vec2 v_texCoord0;


void main() {
    vec2 fogPos = (v_texCoord0 - .5) * 2.;
    float fog = 1. - length(fogPos);
    vec4 diffuse = vec4(.6,.6,.6, 1.);
    vec4 fogColor = vec4(0.,.2,.6,1.);
    fog = clamp(fog, 0., 1.);
    gl_FragColor = mix(fogColor, diffuse, fog);
}