#version 430 core

const uint MAX_DRAWS = 256;

struct Vertex {
    // The position of the vertex around the model origin
    vec3 position;

    // The block texture coordinate of the vertex
    vec2 tex_diffuse_coord;

    // The light texture coordinate of the vertex
    vec2 tex_light_coord;

    // The color (rgb) and shade (alpha) of the vertex
    vec4 color_and_shade;
};

// The projection matrix
uniform mat4 u_ProjectionMatrix;

// The model-view matrix
uniform mat4 u_ModelViewMatrix;

out vec4 v_ColorAndShade;
out vec2 v_TexCoord;
out vec2 v_LightCoord;

#ifdef USE_FOG
out float v_FragDistance;
#endif

uint _get_vertex_index() {
    uint mask123456 = uint(0x00FFFFFFu);
    uint vertIDU = uint(gl_VertexID);
    uint vertIDMask = vertIDU & mask123456;
    return vertIDMask;
}

uint _get_instance_index() {
    uint mask78 = uint(0xFF000000u) >> 1;
    //WHAT 
    uint vertIDU = uint(gl_VertexID);
    uint vertIDMask = vertIDU & mask78;
    uint vertIDMaskShift = vertIDMask >> 24u;
    // why does 24u corrupt everything
    return vertIDMaskShift;
}

#import <sodium:include/fog.glsl>

void _emit_vertex(Vertex vertex, vec3 offset) {;
    // Transform the chunk-local vertex position into world model space
    vec3 vertPostmp = vertex.position;
    vec3 position = offset + vertPostmp;

#ifdef USE_FOG
    v_FragDistance = length(position);
#endif

    // Transform the vertex position into model-view-projection space
    mat4 mvmtemp = u_ModelViewMatrix;
    mat4 pmtemp = u_ProjectionMatrix;
    vec4 posv4 = vec4(position, 1.0);
    vec4 temp2 = mvmtemp * posv4;
    vec4 glpos = pmtemp * temp2;
    gl_Position = glpos;

    // Pass the color and texture coordinates to the fragment shader
    vec4 vcs = vertex.color_and_shade;
    vec2 vlc = vertex.tex_light_coord;
    vec2 vdc = vertex.tex_diffuse_coord;

    v_ColorAndShade = vcs;
    v_LightCoord = vlc;
    v_TexCoord = vdc;
}