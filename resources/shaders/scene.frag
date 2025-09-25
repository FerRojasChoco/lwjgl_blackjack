#version 330 core
//glsl version

in vec2 outTextCoord;

out vec4 fragColor;

struct Material
{
    vec4 diffuse;
};

//use the text coords in order to set the pixel colors by sampling a texture
//thru this sampler2d uniform
uniform sampler2D txtSampler;

uniform Material material;

void main()
{
    fragColor = texture(txtSampler, outTextCoord) + material.diffuse;
}