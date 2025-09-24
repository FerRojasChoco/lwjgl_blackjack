#version 330 core
//glsl version

//specify input format, in this case: 
//starting from position 0, we expect to receive a vector of 3 attributes (xyz)
layout (location=0) in vec3 inPosition; 

void main()
{
    gl_Position = vec4(inPosition, 1.0);        //return received pos in an output variable, if wondering about why vec4, check documentation of lwjgl book
}