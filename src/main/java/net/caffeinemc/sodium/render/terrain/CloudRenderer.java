package net.caffeinemc.sodium.render.terrain;

import net.minecraft.client.render.BufferBuilder;

public class CloudRenderer {
    private static final float EPSILON = 0.001F;
    private static final float CLOUD_WIDTH = 8.0F;
    private static final float CLOUD_HEIGHT = 4.0F;
    private static final float CLOUD_START_OFFSET = 2.0F;
    private static final float CLOUD_START_OFFSET_CRINGE = -1.0F;
    private static final float TEXTURE_OFFSET = 0.00390625F;
    private static final float TEXTURE_MIDDLE = 0.5F;
    public static void renderCloudBottomFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l, float s, float t, float u) {
        builder.vertex(cloudX, cloudY, cloudZ + CLOUD_WIDTH)
                .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                .color(s, t, u, 0.8F)
                .normal(0.0F, -1.0F, 0.0F)
                .next();
        builder.vertex(cloudX + CLOUD_WIDTH, cloudY, cloudZ + CLOUD_WIDTH)
                .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                .color(s, t, u, 0.8F)
                .normal(0.0F, -1.0F, 0.0F)
                .next();
        builder.vertex(cloudX + CLOUD_WIDTH, cloudY, cloudZ)
                .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                .color(s, t, u, 0.8F)
                .normal(0.0F, -1.0F, 0.0F)
                .next();
        builder.vertex(cloudX, cloudY, cloudZ)
                .texture(cloudX * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                .color(s, t, u, 0.8F)
                .normal(0.0F, -1.0F, 0.0F)
                .next();
    }

    public static void renderCloudTopFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l, float m, float n, float o) {
        builder.vertex(cloudX, cloudY + CLOUD_HEIGHT - EPSILON, cloudZ + CLOUD_WIDTH)
                .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                .color(m, n, o, 0.8F)
                .normal(0.0F, 1.0F, 0.0F)
                .next();
        builder.vertex(cloudX + CLOUD_WIDTH, cloudY + CLOUD_HEIGHT - EPSILON, cloudZ + CLOUD_WIDTH)
                .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                .color(m, n, o, 0.8F)
                .normal(0.0F, 1.0F, 0.0F)
                .next();
        builder.vertex(cloudX + CLOUD_WIDTH, cloudY + CLOUD_HEIGHT - EPSILON, cloudZ)
                .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                .color(m, n, o, 0.8F)
                .normal(0.0F, 1.0F, 0.0F)
                .next();
        builder.vertex(cloudX, cloudY + CLOUD_HEIGHT - EPSILON, cloudZ)
                .texture(cloudX * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                .color(m, n, o, 0.8F)
                .normal(0.0F, 1.0F, 0.0F)
                .next();
    }
    public static void renderCloudWestFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l) {
        for(int cloudBlockIndex = 0; cloudBlockIndex < 8; ++cloudBlockIndex) {

            builder.vertex(cloudX + cloudBlockIndex, cloudY, cloudZ + CLOUD_WIDTH)
                    .texture((cloudX + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                    .color(1, 0, 0, 0.8F)
                    .normal(-1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudBlockIndex, cloudY + CLOUD_HEIGHT, cloudZ + CLOUD_WIDTH)
                    .texture((cloudX + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                    .color(1, 0, 0, 0.8F)
                    .normal(-1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudBlockIndex, cloudY + CLOUD_HEIGHT, cloudZ)
                    .texture((cloudX + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                    .color(1, 0, 0, 0.8F)
                    .normal(-1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudBlockIndex, cloudY, cloudZ)
                    .texture((cloudX + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                    .color(1, 0, 0, 0.8F)
                    .normal(-1.0F, 0.0F, 0.0F)
                    .next();


//            builder.vertex(cloudX + cloudBlockIndex, cloudY, cloudZ + CLOUD_WIDTH).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
//            builder.vertex(cloudX + cloudBlockIndex, cloudY + CLOUD_HEIGHT,cloudZ + CLOUD_WIDTH).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
//            builder.vertex(cloudX + cloudBlockIndex, cloudY + CLOUD_HEIGHT,cloudZ).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
//            builder.vertex(cloudX + cloudBlockIndex, cloudY, cloudZ).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
        }
    }

    public static void renderCloudEastFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l) {
        for(int cloudBlockIndex = 0; cloudBlockIndex < 8; ++cloudBlockIndex) {

            builder.vertex(cloudX + cloudBlockIndex + CLOUD_START_OFFSET - EPSILON, cloudY, cloudZ + CLOUD_WIDTH)
                    .texture((cloudX - CLOUD_START_OFFSET_CRINGE + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                    .color(0, 1, 0, 0.8F)
                    .normal(1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudBlockIndex + CLOUD_START_OFFSET - EPSILON, cloudY + CLOUD_HEIGHT, cloudZ + CLOUD_WIDTH)
                    .texture((cloudX - CLOUD_START_OFFSET_CRINGE + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + k, (cloudZ + CLOUD_WIDTH) * TEXTURE_OFFSET + l)
                    .color(0, 1, 0, 0.8F)
                    .normal(1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudBlockIndex + CLOUD_START_OFFSET - EPSILON, cloudY + CLOUD_HEIGHT, cloudZ)
                    .texture((cloudX - CLOUD_START_OFFSET_CRINGE + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                    .color(0, 1, 0, 0.8F)
                    .normal(1.0F, 0.0F, 0.0F)
                    .next();
            builder.vertex(cloudX + cloudBlockIndex + CLOUD_START_OFFSET - EPSILON, cloudY, cloudZ)
                    .texture((cloudX - CLOUD_START_OFFSET_CRINGE + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + k, cloudZ * TEXTURE_OFFSET + l)
                    .color(0, 1, 0, 0.8F)
                    .normal(1.0F, 0.0F, 0.0F)
                    .next();

//            builder.vertex((double)(ae + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 0.0F), (double)(af + 8.0F)).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
//            builder.vertex((double)(ae + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 4.0F), (double)(af + 8.0F)).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
//            builder.vertex((double)(ae + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 4.0F), (double)(af + 0.0F)).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
//            builder.vertex((double)(ae + (float)ag + 1.0F - 9.765625E-4F), (double)(ab + 0.0F), (double)(af + 0.0F)).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next()

        }
    }

    public static void renderCloudNorthFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l) {
        for(int cloudBlockIndex = 0; cloudBlockIndex < 8; ++cloudBlockIndex) {

            builder.vertex(cloudX, cloudY + CLOUD_HEIGHT, cloudZ + cloudBlockIndex)
                    .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + l)
                    .color(0, 0, 1, 0.8F)
                    .normal(0.0F, 0.0F, -1.0F)
                    .next();
            builder.vertex(cloudX + CLOUD_WIDTH, cloudY + CLOUD_HEIGHT, cloudZ + cloudBlockIndex)
                    .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + l)
                    .color(0, 0, 1, 0.8F)
                    .normal(0.0F, 0.0F, -1.0F)
                    .next();
            builder.vertex(cloudX + CLOUD_WIDTH, cloudY, cloudZ + cloudBlockIndex)
                    .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + l)
                    .color(0, 0, 1, 0.8F)
                    .normal(0.0F, 0.0F, -1.0F)
                    .next();
            builder.vertex(cloudX, cloudY, cloudZ + cloudBlockIndex)
                    .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + l)
                    .color(0, 0, 1, 0.8F)
                    .normal(0.0F, 0.0F, -1.0F)
                    .next();

//            builder.vertex((double)(ae + 0.0F), (double)(ab + 4.0F), (double)(af + (float)ag + 0.0F)).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
//            builder.vertex((double)(ae + 8.0F), (double)(ab + 4.0F), (double)(af + (float)ag + 0.0F)).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
//            builder.vertex((double)(ae + 8.0F), (double)(ab + 0.0F), (double)(af + (float)ag + 0.0F)).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
//            builder.vertex((double)(ae + 0.0F), (double)(ab + 0.0F), (double)(af + (float)ag + 0.0F)).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
        }
    }
    public static void renderCloudSouthFace(BufferBuilder builder, float cloudX, float cloudY, float cloudZ, float k, float l) {
        for(int cloudBlockIndex = 0; cloudBlockIndex < 8; ++cloudBlockIndex) {

            builder.vertex(cloudX, cloudY + CLOUD_HEIGHT, cloudZ + cloudBlockIndex + CLOUD_START_OFFSET - EPSILON)
                    .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ - CLOUD_START_OFFSET_CRINGE + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + l)
                    .color(1, 0, 1, 0.8F)
                    .normal(0.0F, 0.0F, 1.0F)
                    .next();
            builder.vertex(cloudX + CLOUD_WIDTH, cloudY + CLOUD_HEIGHT, cloudZ + cloudBlockIndex + CLOUD_START_OFFSET - EPSILON)
                    .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ - CLOUD_START_OFFSET_CRINGE + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + l)
                    .color(1, 0, 1, 0.8F)
                    .normal(0.0F, 0.0F, 1.0F)
                    .next();
            builder.vertex(cloudX + CLOUD_WIDTH, cloudY, cloudZ + cloudBlockIndex + CLOUD_START_OFFSET - EPSILON)
                    .texture((cloudX + CLOUD_WIDTH) * TEXTURE_OFFSET + k, (cloudZ - CLOUD_START_OFFSET_CRINGE + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + l)
                    .color(1, 0, 1, 0.8F)
                    .normal(0.0F, 0.0F, 1.0F)
                    .next();
            builder.vertex(cloudX, cloudY, cloudZ + cloudBlockIndex + CLOUD_START_OFFSET - EPSILON)
                    .texture(cloudX * TEXTURE_OFFSET + k, (cloudZ - CLOUD_START_OFFSET_CRINGE + cloudBlockIndex + TEXTURE_MIDDLE) * TEXTURE_OFFSET + l)
                    .color(1, 0, 1, 0.8F)
                    .normal(0.0F, 0.0F, 1.0F)
                    .next();

//            builder.vertex((double)(ae + 0.0F), (double)(ab + 4.0F), (double)(af + (float)ag + 1.0F - 9.765625E-4F)).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
//            builder.vertex((double)(ae + 8.0F), (double)(ab + 4.0F), (double)(af + (float)ag + 1.0F - 9.765625E-4F)).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
//            builder.vertex((double)(ae + 8.0F), (double)(ab + 0.0F), (double)(af + (float)ag + 1.0F - 9.765625E-4F)).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
//            builder.vertex((double)(ae + 0.0F), (double)(ab + 0.0F), (double)(af + (float)ag + 1.0F - 9.765625E-4F)).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();

        }
    }
}