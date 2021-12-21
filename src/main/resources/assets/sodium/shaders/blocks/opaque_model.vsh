#import <sodium:blocks/base.vsh>

struct BlockVertex {
    float position_x;
    float position_y;
    float position_z;
    uint color;
    uint tex_diffuse;
    uint tex_light;
};

struct Uniforms {
    uint vertex_offset;
    vec4 offset;
};

layout(std430, binding = 0) buffer ssbo_Vertices {
    BlockVertex vertices[];
};

layout(std140, binding = 0) uniform ubo_InstanceUniforms {
    Uniforms instanceUniforms[MAX_DRAWS];
};

Vertex _get_vertex(Uniforms uniforms) {
    uint vi = _get_vertex_index();
    uint a = uniforms.vertex_offset + vi;
    BlockVertex vert = vertices[a];

    float vx = vert.position_x;
    float vy = vert.position_y;
    float vz = vert.position_z;
    uint vc = vert.color;
    uint vd = vert.tex_diffuse;
    uint vl = vert.tex_light;

    vec3 position = vec3(vx, vy, vz);
    vec4 color_and_shade = unpackUnorm4x8(vc);
    vec2 tex_diffuse_coord = unpackUnorm2x16(vd);
    vec2 tex_light_coord = unpackUnorm2x16(vl);

    Vertex vet = Vertex(position, tex_diffuse_coord, tex_light_coord, color_and_shade);

    return vet;
}

void main() {
    uint instance_local = _get_instance_index();
    Uniforms uniforms = instanceUniforms[instance_local];
    Vertex vertex = _get_vertex(uniforms);

    _emit_vertex(vertex, uniforms.offset.xyz);
}