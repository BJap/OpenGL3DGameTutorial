#version 400 core

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform vec3 lightColor[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {
    vec4 textureColor = texture(modelTexture, pass_textureCoordinates);

    if (textureColor.a < 0.5) {
        discard;
    }

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0; i < 4; i++) {
        float distance = length(toLightVector[i]);
        float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
        vec3 unitlightVector = normalize(toLightVector[i]);
        float nDot1 = dot(unitNormal, unitlightVector);
        float brightness = max(nDot1, 0.0);
        vec3 lightDirection = -unitlightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attenuationFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attenuationFactor;
    }

    totalDiffuse = max(totalDiffuse, 0.2);

    out_Color = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
    out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
}