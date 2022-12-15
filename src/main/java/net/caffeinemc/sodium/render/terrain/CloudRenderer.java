package net.caffeinemc.sodium.render.terrain;

import net.minecraft.client.render.BufferBuilder;

public class CloudRenderer {
    private static final float EPSILON = 0.001F;
    private static final float CLOUD_WIDTH = 8.0F;
    public static final float CLOUD_HEIGHT = 4.0F;
    private static final float CLOUD_START_OFFSET = 1.0F;
    public static final float TEXTURE_OFFSET = 0.00390625F;
    private static final float TEXTURE_OTHER_OFFSET = 0.5F;
    public static void renderCloudBottomFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l, float r, float g, float b) {
        float red = r * 0.7F;
        float green = g * 0.7F;
        float blue = b * 0.7F;

        builder.vertex(cloudX, cloudY, cloudZ + CLOUD_WIDTH)
                .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, -1.0F, 0.0F)
                .next();
        builder.vertex(cloudX + CLOUD_WIDTH, cloudY, cloudZ + CLOUD_WIDTH)
                .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, -1.0F, 0.0F)
                .next();
        builder.vertex(cloudX + CLOUD_WIDTH, cloudY, cloudZ)
                .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, -1.0F, 0.0F)
                .next();
        builder.vertex(cloudX, cloudY, cloudZ)
                .texture(cloudX * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, -1.0F, 0.0F)
                .next();
    }

    public static void renderCloudTopFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l, float r, float g, float b) {
        float red = r * 0.7F;
        float green = g * 0.7F;
        float blue = b * 0.7F;

        builder.vertex(cloudX, cloudY + CLOUD_HEIGHT - EPSILON, cloudZ + CLOUD_WIDTH)
                .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, 1.0F, 0.0F)
                .next();
        builder.vertex(cloudX + CLOUD_WIDTH, cloudY + CLOUD_HEIGHT - EPSILON, cloudZ + CLOUD_WIDTH)
                .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, 1.0F, 0.0F)
                .next();
        builder.vertex(cloudX + CLOUD_WIDTH, cloudY + CLOUD_HEIGHT - EPSILON, cloudZ)
                .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, 1.0F, 0.0F)
                .next();
        builder.vertex(cloudX, cloudY + CLOUD_HEIGHT - EPSILON, cloudZ)
                .texture(cloudX * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, 1.0F, 0.0F)
                .next();
    }
    public static void renderCloudWestFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l, float r, float g, float b) {
        float red = r * 0.9F;
        float green = g * 0.9F;
        float blue = b * 0.9F;

        for(int cloudPositionInChunk = 0; cloudPositionInChunk < 8; ++cloudPositionInChunk) {

            builder.vertex(cloudX + cloudPositionInChunk, cloudY, cloudZ + CLOUD_WIDTH)
                    .texture((cloudX + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(-1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudPositionInChunk, cloudY + CLOUD_HEIGHT, cloudZ + CLOUD_WIDTH)
                    .texture((cloudX + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(-1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudPositionInChunk, cloudY + CLOUD_HEIGHT, cloudZ)
                    .texture((cloudX + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(-1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudPositionInChunk, cloudY, cloudZ)
                    .texture((cloudX + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(-1.0F, 0.0F, 0.0F)
                    .next();

        }
    }

    public static void renderCloudEastFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l, float r, float g, float b) {
        float red = r * 0.9F;
        float green = g * 0.9F;
        float blue = b * 0.9F;

        for(int cloudPositionInChunk = 0; cloudPositionInChunk < 8; ++cloudPositionInChunk) {

            builder.vertex(cloudX + cloudPositionInChunk + CLOUD_START_OFFSET - EPSILON, cloudY, cloudZ + CLOUD_WIDTH)
                    .texture((cloudX + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudPositionInChunk + CLOUD_START_OFFSET - EPSILON, cloudY + CLOUD_HEIGHT, cloudZ + CLOUD_WIDTH)
                    .texture((cloudX + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudPositionInChunk + CLOUD_START_OFFSET - EPSILON, cloudY + CLOUD_HEIGHT, cloudZ)
                    .texture((cloudX + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudPositionInChunk + CLOUD_START_OFFSET - EPSILON, cloudY, cloudZ)
                    .texture((cloudX + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(1.0F, 0.0F, 0.0F)
                    .next();

        }
    }
    public static void renderCloudInsideEastFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l, float r, float g, float b) {
        float red = r * 0.9F;
        float green = g * 0.9F;
        float blue = b * 0.9F;

        builder.vertex(cloudX + CLOUD_START_OFFSET - EPSILON, cloudY, cloudZ + CLOUD_WIDTH)
                .texture((cloudX  + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(1.0F, 0.0F, 0.0F)
                .next();
        builder.vertex(cloudX + CLOUD_START_OFFSET - EPSILON, cloudY + CLOUD_HEIGHT, cloudZ + CLOUD_WIDTH)
                .texture((cloudX + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(1.0F, 0.0F, 0.0F)
                .next();
        builder.vertex(cloudX + CLOUD_START_OFFSET - EPSILON, cloudY + CLOUD_HEIGHT, cloudZ)
                .texture((cloudX + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(1.0F, 0.0F, 0.0F)
                .next();
        builder.vertex(cloudX  + CLOUD_START_OFFSET - EPSILON, cloudY, cloudZ)
                .texture((cloudX + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(1.0F, 0.0F, 0.0F)
                .next();

    }

    public static void renderCloudNorthFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l, float r, float g, float b) {
        float red = r * 0.8F;
        float green = g * 0.8F;
        float blue = b * 0.8F;

        for(int cloudPositionInChunk = 0; cloudPositionInChunk < 8; ++cloudPositionInChunk) {

            builder.vertex(cloudX, cloudY + CLOUD_HEIGHT, cloudZ + cloudPositionInChunk)
                    .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(0.0F, 0.0F, -1.0F)
                    .next();
            builder.vertex(cloudX + CLOUD_WIDTH, cloudY + CLOUD_HEIGHT, cloudZ + cloudPositionInChunk)
                    .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(0.0F, 0.0F, -1.0F)
                    .next();
            builder.vertex(cloudX + CLOUD_WIDTH, cloudY, cloudZ + cloudPositionInChunk)
                    .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(0.0F, 0.0F, -1.0F)
                    .next();
            builder.vertex(cloudX, cloudY, cloudZ + cloudPositionInChunk)
                    .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(0.0F, 0.0F, -1.0F)
                    .next();

     }
    }
    public static void renderCloudSouthFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l, float r, float g, float b) {
        float red = r * 0.8F;
        float green = g * 0.8F;
        float blue = b * 0.8F;

        for(int cloudPositionInChunk = 0; cloudPositionInChunk < 8; ++cloudPositionInChunk) {

            builder.vertex(cloudX, cloudY + CLOUD_HEIGHT, cloudZ + cloudPositionInChunk + CLOUD_START_OFFSET - EPSILON)
                    .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(0.0F, 0.0F, 1.0F)
                    .next();
            builder.vertex(cloudX + CLOUD_WIDTH, cloudY + CLOUD_HEIGHT, cloudZ + cloudPositionInChunk + CLOUD_START_OFFSET - EPSILON)
                    .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(0.0F, 0.0F, 1.0F)
                    .next();
            builder.vertex(cloudX + CLOUD_WIDTH, cloudY, cloudZ + cloudPositionInChunk + CLOUD_START_OFFSET - EPSILON)
                    .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(0.0F, 0.0F, 1.0F)
                    .next();
            builder.vertex(cloudX, cloudY, cloudZ + cloudPositionInChunk + CLOUD_START_OFFSET - EPSILON)
                    .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + cloudPositionInChunk + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                    .color(red, green, blue, 0.8F)
                    .normal(0.0F, 0.0F, 1.0F)
                    .next();
        }
    }

    public static void renderCloudInsideSouthFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l, float r, float g, float b) {
        float red = r * 0.8F;
        float green = g * 0.8F;
        float blue = b * 0.8F;

        builder.vertex(cloudX, cloudY + CLOUD_HEIGHT, cloudZ + CLOUD_START_OFFSET - EPSILON)
                .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, 0.0F, 1.0F)
                .next();
        builder.vertex(cloudX + CLOUD_WIDTH, cloudY + CLOUD_HEIGHT, cloudZ + CLOUD_START_OFFSET - EPSILON)
                .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, 0.0F, 1.0F)
                .next();
        builder.vertex(cloudX + CLOUD_WIDTH, cloudY, cloudZ + CLOUD_START_OFFSET - EPSILON)
                .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, 0.0F, 1.0F)
                .next();
        builder.vertex(cloudX, cloudY, cloudZ  + CLOUD_START_OFFSET - EPSILON)
                .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + TEXTURE_OTHER_OFFSET) * TEXTURE_OFFSET + l)
                .color(red, green, blue, 0.8F)
                .normal(0.0F, 0.0F, 1.0F)
                .next();
    }

    public static void renderCloudsFast(BufferBuilder builder, float cloudY, float k, float l, float red ,float green, float blue){
        for(int cloudX = -32; cloudX < 32; cloudX += 32) {
            for (int cloudZ = -32; cloudZ < 32; cloudZ += 32) {

                builder.vertex(cloudX, cloudY, cloudZ + 32)
                        .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + 32) * TEXTURE_OFFSET + l)
                        .color(red, green, blue, 0.8F)
                        .normal(0.0F, -1.0F, 0.0F)
                        .next();
                builder.vertex(cloudX + 32, cloudY, cloudZ + 32)
                        .texture((cloudX + 32) * TEXTURE_OFFSET + k, (cloudZ + 32) * TEXTURE_OFFSET + l)
                        .color(red, green, blue, 0.8F)
                        .normal(0.0F, -1.0F, 0.0F)
                        .next();
                builder.vertex(cloudX + 32, cloudY, cloudZ)
                        .texture((cloudX + 32) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                        .color(red, green, blue, 0.8F)
                        .normal(0.0F, -1.0F, 0.0F)
                        .next();
                builder.vertex(cloudX, cloudY, cloudZ)
                        .texture(cloudX * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                        .color(red, green, blue, 0.8F)
                        .normal(0.0F, -1.0F, 0.0F)
                        .next();
            }
        }
    }
}