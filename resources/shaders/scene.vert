#version 330 core
//glsl version

//specify input format, in this case: 
//starting from position 0, we expect to receive a vector of 3 attributes (xyz)
layout (location=0) in vec3 position;
layout (location=1) in vec3 color;

out vec3 outColor;

uniform mat4 projectionMatrix;

void main()
{
    gl_Position = projectionMatrix * vec4(position, 1.0);        //return received pos in an output variable, if wondering about why vec4, check documentation of lwjgl book
    outColor = color;
}