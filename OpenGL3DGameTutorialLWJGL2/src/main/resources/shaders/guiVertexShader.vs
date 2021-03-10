#version 140

in vec2 position;

out vec2 textureCoordinates;

uniform mat4 modelMatrix;

void main(void){
	gl_Position = modelMatrix * vec4(position, 0.0, 1.0);
	textureCoordinates = vec2((position.x + 1.0) / 2.0, 1 - (position.y + 1.0) / 2.0);
}