#version 330 core
//glsl version

in vec3 outColor;
out vec4 fragColor;

void main(){
    fragColor = vec4(outColor, 1.0);   //set a fixed color for each fragment
}