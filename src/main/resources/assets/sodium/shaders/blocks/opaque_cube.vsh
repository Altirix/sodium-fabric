#import <sodium:blocks/base.vsh>

struct Quad {
    // offset 0
    // Block position (XYZ) and face index (W) of the quad
    uint position_id;

    // offset 4
    // UV-coordinates of the light texture (Unorm4x8)
    uint tex_light_block;
    uint tex_light_sky;

    // offset 12
    // UV-coordinates of the diffuse texture (Unorm2x16)
    uint tex_diffuse_min;
    uint tex_diffuse_max;
};

struct BlockVertex {
    uint tint;
};

struct Uniforms {
    uint quad_offset;
    uint vertex_offset;
    vec4 offset;
};

layout(std430, binding = 0) buffer ssbo_Quads {
    Quad quads[];
};

layout(std430, binding = 1) buffer ssbo_Vertices {
    BlockVertex vertices[];
};

layout(std140, binding = 0) uniform ubo_InstanceUniforms {
    Uniforms instanceUniforms[MAX_DRAWS];
};

const uint FACE_COUNT = 6;
const uint VERTICES_PER_FACE = 4;

const vec3[FACE_COUNT][VERTICES_PER_FACE] CUBE_VERTICES = {
    { vec3(0, 0, 0), vec3(1, 0, 0), vec3(1, 0, 1), vec3(0, 0, 1) }, /* Down */
    { vec3(0, 1, 0), vec3(0, 1, 1), vec3(1, 1, 1), vec3(1, 1, 0) }, /* Up */

    { vec3(1, 1, 0), vec3(1, 0, 0), vec3(0, 0, 0), vec3(0, 1, 0) }, /* North */
    { vec3(0, 1, 1), vec3(0, 0, 1), vec3(1, 0, 1), vec3(1, 1, 1) }, /* South */

    { vec3(0, 1, 0), vec3(0, 0, 0), vec3(0, 0, 1), vec3(0, 1, 1) }, /* West */
    { vec3(1, 1, 1), vec3(1, 0, 1), vec3(1, 0, 0), vec3(1, 1, 0) }, /* East */
};

const vec2[VERTICES_PER_FACE] TEXTURE_MAP_MIN = {
    vec2(0, 1), vec2(0, 0),
    vec2(1, 0), vec2(1, 1),
};
const vec2[VERTICES_PER_FACE] TEXTURE_MAP_MAX = {
    vec2(1, 0), vec2(1, 1),
    vec2(0, 1), vec2(0, 0),
};

Vertex _get_vertex(Uniforms uniforms) {
    Uniforms localu = uniforms;

    uint u4 = uint(4u);
    uint u8 = uint(8u);
    uint u16 = uint(16u);
    uint u24 = uint(24u);

    uint vertex_index = _get_vertex_index();

    uint corner_index = vertex_index % u4;
    uint quad_index = vertex_index / u4;

    uint quadoffset = localu.quad_offset;
    uint quadRealIndex = quadoffset + quad_index;

    Quad quad = quads[quadRealIndex];
    uint mask78 = uint(0xFF000000u);
    uint mask56 = uint(0x00FF0000u);
    uint mask34 = uint(0x0000FF00u);
    uint mask12 = uint(0x000000FFu);
    uint quadposID = quad.position_id;
    uint quadpos78 = (quadposID & mask78);
    uint quadpos56 = (quadposID & mask56);
    uint quadpos34 = (quadposID & mask34);
    uint quadpos12 = (quadposID & mask12);

    uint qPosS24 = quadpos78>> u24;
    uint qPosS16 = quadpos56>> u16;
    uint qposS8 = quadpos34>> u8;

    vec3 block_position = vec3(qPosS24, qPosS16, qposS8);
    vec3 cubeVert = CUBE_VERTICES[quadpos12][corner_index];
    vec3 position = block_position + cubeVert;

    uint tlb = quad.tex_light_block;
    uint tls = quad.tex_light_sky;
    vec4 unb = unpackUnorm4x8(tlb);
    vec4 uns = unpackUnorm4x8(tls);
    float up48b = unb[corner_index];
    float up48s = uns[corner_index];
    vec2 tex_light_coord = vec2(up48b, up48s);

    uint tdmin = quad.tex_diffuse_min;
    uint tdmax = quad.tex_diffuse_max;
    vec2 up216min = unpackUnorm2x16(tdmin);
    vec2 up216max = unpackUnorm2x16(tdmax);
    vec2 tmmin = TEXTURE_MAP_MIN[corner_index];
    vec2 tmmax = TEXTURE_MAP_MAX[corner_index];
    vec2 tex_diffuse_coord = (up216min * tmmin) + (up216max * tmmax);

    uint vertOffset = localu.vertex_offset;
    uint realVertId = vertOffset + vertex_index;
    BlockVertex vertex = vertices[realVertId];

    uint tnt = vertex.tint;
    vec4 color_and_shade = unpackUnorm4x8(tnt);

    Vertex vrt = Vertex(position, tex_diffuse_coord, tex_light_coord, color_and_shade);
    return vrt;
}

void main() {
    uint instance_local = _get_instance_index();
    Uniforms uniforms = instanceUniforms[instance_local];
    Vertex vertex = _get_vertex(uniforms);
    vec3 xyz = uniforms.offset.xyz;

    _emit_vertex(vertex, xyz);
}