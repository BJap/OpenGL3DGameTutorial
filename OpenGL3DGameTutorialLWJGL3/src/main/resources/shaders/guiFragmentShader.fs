#version 400

in vec2 textureCoordinates;

out vec4 out_Color;

uniform sampler2D guiTexture;

void main(void) {
	out_Color = texture(guiTexture, textureCoordinates);
}