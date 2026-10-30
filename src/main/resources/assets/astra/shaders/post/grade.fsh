#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform GradeConfig {
    float TintR;
    float TintG;
    float TintB;
    float Saturation;
    float Contrast;
    float Brightness;
    float Vignette;
};

out vec4 fragColor;

void main() {
    vec3 c = texture(InSampler, texCoord).rgb;
    float luma = dot(c, vec3(0.2126, 0.7152, 0.0722));
    c = mix(vec3(luma), c, Saturation);
    c = (c - 0.5) * Contrast + 0.5;
    c *= vec3(TintR, TintG, TintB) * Brightness;
    vec2 d = texCoord - 0.5;
    c *= 1.0 - Vignette * dot(d, d) * 2.0;
    fragColor = vec4(clamp(c, 0.0, 1.0), 1.0);
}
