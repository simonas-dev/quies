#ifdef GL_ES
precision highp float;
#endif

uniform float u_time;
uniform vec2 u_resolution;

#define distortion 0.2
#define speed 0.2

vec2 noise(vec2 position) {
    return vec2(
        (sin(position.x) + cos(position.y)) * distortion,
        (cos(position.x) + sin(position.y)) * distortion
    );
}

void main() {
    vec2 position = (gl_FragCoord.xy * 2.0 - u_resolution) / min(u_resolution.x, u_resolution.y);


    position.x += u_time * speed;

    position += noise(position);

    float color = 1.0 - (cos(position.x * position.y) + 3.7) / 4.0;

    gl_FragColor = vec4(vec3(color), 1.0);
}