#version 430
#extension ARB_shading_language_packing : require

layout(local_size_x = 1, local_size_y = 1) in;

//in uvec3 gl_NumWorkGroups;
//in uvec3 gl_workGroupID;
//in uvec3 gl_LocalInvocationID;
//in uvec3 gl_GlobalInvocationID;
//in uint  gl_LocalInvocationIndex;

struct DrawParameters {
// Older AMD drivers can't handle vec3 in std140 layouts correctly
// The alignment requirement is 16 bytes (4 float components) anyways, so we're not wasting extra memory with this,
// only fixing broken drivers.
    vec4 Offset;
};

//Define packed vertex data
struct Packed {
    uint a_Pos1; //ushort[2] //x,y //The position of the vertex around the model origin
    uint a_Pos2; //ushort[2] //z,w
    uint a_Color; //The color of the vertex
    uint a_TexCoord; // The block texture coordinate of the vertex
    uint a_LightCoord; // The light texture coordinate of the vertex
};

struct Index {
    uint i12;
    uint i34;
    uint i56;
};

uniform mat4 u_ModelViewMatrix;
uniform float u_ModelScale;
uniform float u_ModelOffset;

layout(std140, binding = 0) uniform ubo_DrawParameters {
    DrawParameters Chunks[256];
};

layout(std430, binding = 1) buffer mesh_buffer_in {
    Packed packed_mesh[];
};

layout(std430, binding = 2) buffer index_buffer {
    Index ipairs[];
};

uvec4 unpackPos(Packed p) {
    uint x = p.a_Pos1 & uint(0xFFFF);
    uint y = (p.a_Pos1 >> 16);
    uint z = p.a_Pos2 & uint(0xFFFF);
    uint w = (p.a_Pos2 >> 16);
    return uvec4(x,y,z,w);
}

float getDistance(int index) {
    vec4 rawPosition = unpackPos(packed_mesh[index]);

    vec3 vertexPosition = rawPosition.xyz * u_ModelScale + u_ModelOffset;
    vec3 chunkOffset = Chunks[int(rawPosition.w)].Offset.xyz;
    vec4 pos = u_ModelViewMatrix * vec4(chunkOffset + vertexPosition, 1.0);

    return length(pos);
}

float getAverageDistance(Index pair) {
    return
       (getDistance(int(pair.i12 & uint(0xFFFF)))
      + getDistance(int(pair.i12 >> 16))
      + getDistance(int(pair.i34 & uint(0xFFFF)))
      + getDistance(int(pair.i34 >> 16))
      + getDistance(int(pair.i56 & uint(0xFFFF)))
      + getDistance(int(pair.i56 >> 16)))
      / 6;
}

void main() {
    int meshLength = packed_mesh.length();
    int indexLength = ipairs.length();

    //Debug
//    for(int i = 0; i < meshLength; i++) {
//        packed_mesh[i].a_TexCoord = int(getDistance(i));
//        i += int(getDistance(i));
//        packed_mesh[i].a_TexCoord = int(unpackPos(packed_mesh[i]).x);
//    }

    //Insertion sort of indicies based on vertex
    int i = 1;
    while(i < ipairs.length()) {
        Index temp = ipairs[i];
        float tempDist = getAverageDistance(ipairs[i]);
        int j = i - 1;
        while(j >= 0 && getAverageDistance(ipairs[j]) < tempDist) {
            ipairs[j+1] = ipairs[j];
            j = j - 1;
        }
        ipairs[j+1] = temp;
        i = i + 1;
    }
}