#version 330 core
//glsl version

//specify input format, in this case: 
//starting from position 0, we expect to receive a vector of 3 attributes (xyz)
layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

out vec2 outTextCoord;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;

void main()
{
    gl_Position = projectionMatrix * modelMatrix * vec4(position, 1.0);        //return received pos in an output variable, if wondering about why vec4, check documentation of lwjgl book
    outTextCoord = texCoord;
}