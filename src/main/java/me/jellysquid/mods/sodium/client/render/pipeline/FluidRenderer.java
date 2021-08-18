package me.jellysquid.mods.sodium.client.render.pipeline;

import me.jellysquid.mods.sodium.client.model.light.LightMode;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.LightPipelineProvider;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuad;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadViewMutable;
import me.jellysquid.mods.sodium.client.model.quad.blender.BiomeColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFlags;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadWinding;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadColorProvider;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.format.ModelVertexSink;
import me.jellysquid.mods.sodium.client.util.Norm3b;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import me.jellysquid.mods.sodium.common.util.DirectionUtil;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

public class FluidRenderer {
    // TODO: allow this to be changed by vertex format
    // TODO: move fluid rendering to a separate render pass and control glPolygonOffset and glDepthFunc to fix this properly
    private static final float EPSILON = 0.00f;

    private final BlockPos.Mutable scratchPos = new BlockPos.Mutable();

    private final Sprite waterOverlaySprite;

    private final ModelQuadViewMutable quad = new ModelQuad();

    private final LightPipelineProvider lighters;
    private final BiomeColorBlender biomeColorBlender;

    // Cached wrapper type that adapts FluidRenderHandler to support QuadColorProvider<FluidState>
    private final FabricFluidColorizerAdapter fabricColorProviderAdapter = new FabricFluidColorizerAdapter();

    private final QuadLightData quadLightData = new QuadLightData();
    private final int[] quadColors = new int[4];

    public FluidRenderer(LightPipelineProvider lighters, BiomeColorBlender biomeColorBlender) {
        this.waterOverlaySprite = ModelLoader.WATER_OVERLAY.getSprite();

        int normal = Norm3b.pack(0.0f, 1.0f, 0.0f);

        for (int i = 0; i < 4; i++) {
            this.quad.setNormal(i, normal);
        }

        this.lighters = lighters;
        this.biomeColorBlender = biomeColorBlender;
    }

    private boolean isFluidExposed(BlockRenderView world, int x, int y, int z, Direction dir, Fluid fluid) {
        // Up direction is hard to test since it doesnt fill the block
        if(dir != Direction.UP) {
            BlockPos pos = this.scratchPos.set(x, y, z);
            BlockState blockState = world.getBlockState(pos);
            VoxelShape shape = blockState.getCullingShape(world, pos);
            if (blockState.isOpaque() && VoxelShapes.isSideCovered(VoxelShapes.fullCube(), shape, dir.getOpposite())) {
                return false; // Fluid is in waterlogged block that self occludes
            }
        }

        BlockPos pos = this.scratchPos.set(x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ());
        return !world.getFluidState(pos).getFluid().matchesType(fluid);
    }

    private boolean isSideExposed(BlockRenderView world, int x, int y, int z, Direction dir, float height) {
        BlockPos pos = this.scratchPos.set(x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ());
        BlockState blockState = world.getBlockState(pos);

        if (blockState.isOpaque()) {
            VoxelShape shape = blockState.getCullingShape(world, pos);

            // Hoist these checks to avoid allocating the shape below
            if (shape == VoxelShapes.fullCube()) {
                // The top face always be inset, so if the shape above is a full cube it can't possibly occlude
                return dir == Direction.UP;
            } else if (shape.isEmpty()) {
                return true;
            }

            VoxelShape threshold = VoxelShapes.cuboid(0.0D, 0.0D, 0.0D, 1.0D, height, 1.0D);

            return !VoxelShapes.isSideCovered(threshold, shape, dir);
        }

        return true;
    }

    public boolean render(BlockRenderView world, FluidState fluidState, BlockPos pos, BlockPos offset, ChunkModelBuilder buffers) {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();

        Fluid fluid = fluidState.getFluid();
        List<Direction> isFaceExposed= new LinkedList<Direction>();
        for(Direction dir : DirectionUtil.ALL_DIRECTIONS) {
            if(this.isFluidExposed(world, posX, posY, posZ, dir, fluid)) {
                    isFaceExposed.add(dir);
            }
        }

        if (isFaceExposed.isEmpty()) {
            return false;
        }

        boolean isWater = fluidState.isIn(FluidTags.WATER);

        FluidRenderHandler handler = FluidRenderHandlerRegistryImpl.INSTANCE.getOverride(fluidState.getFluid());
        ModelQuadColorProvider<FluidState> colorizer = this.createColorProviderAdapter(handler);

        Sprite[] sprites = handler.getFluidSprites(world, pos, fluidState);

        boolean rendered = false;

        float h1 = this.getCornerHeight(world, posX, posY, posZ, fluidState.getFluid());
        float h2 = this.getCornerHeight(world, posX, posY, posZ + 1, fluidState.getFluid());
        float h3 = this.getCornerHeight(world, posX + 1, posY, posZ + 1, fluidState.getFluid());
        float h4 = this.getCornerHeight(world, posX + 1, posY, posZ, fluidState.getFluid());

        float yOffset = isFaceExposed.contains(Direction.DOWN) ? EPSILON : 0.0F;

        final ModelQuadViewMutable quad = this.quad;

        LightMode lightMode = isWater && MinecraftClient.isAmbientOcclusionEnabled() ? LightMode.SMOOTH : LightMode.FLAT;
        LightPipeline lighter = this.lighters.getLighter(lightMode);

        for (Direction dir : isFaceExposed.stream().toList()) {
            float x1, x2, x3, x4;
            float c1, c2, c3, c4;
            float z1, z2, z3, z4;
            float height;

            switch (dir) {
                case NORTH:
                    quad.setFlags(ModelQuadFlags.IS_ALIGNED);
                    x1 = x2 = 1.0F;
                    x3 = x4 = 0.0F;
                    c1 = h4;
                    c2 = c3 = yOffset;
                    c4 = h1;
                    z1 = z2 = z3 = z4 = EPSILON;
                    height = Math.max(c1,c4);
                    break;
                case SOUTH:
                    quad.setFlags(ModelQuadFlags.IS_ALIGNED);
                    x1 = x2 = 0.0F;
                    x3 = x4 = 1.0F;
                    c1 = h2;
                    c2 = c3 = yOffset;
                    c4 = h3;
                    z1 = z2 = z3 = z4 = 1.0F - EPSILON;
                    height = Math.max(c1,c4);
                    break;
                case WEST:
                    quad.setFlags(ModelQuadFlags.IS_ALIGNED);
                    x1 = x2 = x3 = x4 = EPSILON;
                    c1 = h1;
                    c2 = c3 = yOffset;
                    c4 = h2;
                    z1 = z2 = 0.0F;
                    z3 = z4 = 1.0F;
                    height = Math.max(c1,c4);
                    break;
                case EAST:
                    quad.setFlags(ModelQuadFlags.IS_ALIGNED);
                    x1 = x2 = x3 = x4 = 1.0F - EPSILON;
                    c1 = h3;
                    c2 = c3 = yOffset;
                    c4 = h4;
                    z1 = z2 = 1.0F;
                    z3 = z4 = 0.0F;
                    height = Math.max(c1,c4);
                    break;
                case UP:
                    quad.setFlags(0b00);
                    x1 = x2 = 0.0F;
                    x3 = x4 = 1.0F;
                    c1 = h1 - EPSILON;
                    c2 = h2 - EPSILON;
                    c3 = h3 - EPSILON;
                    c4 = h4 - EPSILON;
                    z1 = z4 = 0.0F;
                    z2 = z3 = 1.0F;
                    height = Math.min(Math.min(h1, h2), Math.min(h3, h4));
                    break;
                case DOWN:
                    quad.setFlags(0b00);
                    x1 = x2 = 0.0F;
                    x3 = x4 = 1.0F;
                    c1 = c2 = c3 = c4 = yOffset;
                    z1 = z4 = 1.0F;
                    z2 = z3 = 0.0F;
                    height = 0.8888889F;
                    break;
                default:
                    continue;
            }

            if (this.isSideExposed(world, posX, posY, posZ, dir, height)) {
                int adjX = posX + dir.getOffsetX();
                int adjY = posY + dir.getOffsetY();
                int adjZ = posZ + dir.getOffsetZ();

                Sprite sprite = sprites[1];

                if (isWater) {
                    BlockPos posAdj = this.scratchPos.set(adjX, adjY, adjZ);
                    Block block = world.getBlockState(posAdj).getBlock();

                    if (block == Blocks.GLASS || block instanceof StainedGlassBlock) {
                        sprite = this.waterOverlaySprite;
                    }
                }

                float u1 = sprite.getFrameU(0.0D);
                float u2 = sprite.getFrameU(8.0D);
                float v1 = sprite.getFrameV((1.0F - c4) * 16.0F * 0.5F);
                float v2 = sprite.getFrameV((1.0F - c1) * 16.0F * 0.5F);
                float v3 = sprite.getFrameV(8.0D);

                quad.setSprite(sprite);

                this.setVertex(quad, 0, x1, c1, z1, u2, v2);
                this.setVertex(quad, 1, x2, c2, z2, u2, v3);
                this.setVertex(quad, 2, x3, c3, z3, u1, v3);
                this.setVertex(quad, 3, x4, c4, z4, u1, v1);

                float br = dir.getAxis() == Direction.Axis.Z ? 0.8F : 0.6F;

                ModelQuadFacing facing = ModelQuadFacing.fromDirection(dir);

                this.calculateQuadColors(quad, world, pos, lighter, dir, br, colorizer, fluidState);

                int vertexStart = this.writeVertices(buffers, offset, quad);

                buffers.getIndexBufferBuilder(facing)
                        .add(vertexStart, ModelQuadWinding.CLOCKWISE);

                if (sprite != this.waterOverlaySprite) {
                    buffers.getIndexBufferBuilder(facing.getOpposite())
                            .add(vertexStart, ModelQuadWinding.COUNTERCLOCKWISE);
                }

                rendered = true;
            }
        }

        return rendered;
    }

    private ModelQuadColorProvider<FluidState> createColorProviderAdapter(FluidRenderHandler handler) {
        FabricFluidColorizerAdapter adapter = this.fabricColorProviderAdapter;
        adapter.setHandler(handler);

        return adapter;
    }

    private void calculateQuadColors(ModelQuadView quad, BlockRenderView world, BlockPos pos, LightPipeline lighter, Direction dir, float brightness,
                                     ModelQuadColorProvider<FluidState> handler, FluidState fluidState) {
        QuadLightData light = this.quadLightData;
        lighter.calculate(quad, pos, light, dir, false);

        int[] biomeColors = this.biomeColorBlender.getColors(world, pos, quad, handler, fluidState);

        for (int i = 0; i < 4; i++) {
            this.quadColors[i] = ColorABGR.mul(biomeColors != null ? biomeColors[i] : 0xFFFFFFFF, light.br[i] * brightness);
        }
    }

    private int writeVertices(ChunkModelBuilder builder, BlockPos offset, ModelQuadView quad) {
        ModelVertexSink vertices = builder.getVertexSink();
        vertices.ensureCapacity(4);

        int vertexStart = vertices.getVertexCount();

        for (int i = 0; i < 4; i++) {
            float x = quad.getX(i);
            float y = quad.getY(i);
            float z = quad.getZ(i);

            int color = this.quadColors[i];

            float u = quad.getTexU(i);
            float v = quad.getTexV(i);

            int light = this.quadLightData.lm[i];

            vertices.writeVertex(offset, x, y, z, color, u, v, light, builder.getChunkId());
        }

        vertices.flush();

        Sprite sprite = quad.getSprite();

        if (sprite != null) {
            builder.addSprite(sprite);
        }

        return vertexStart;
    }

    private void setVertex(ModelQuadViewMutable quad, int i, float x, float y, float z, float u, float v) {
        quad.setX(i, x);
        quad.setY(i, y);
        quad.setZ(i, z);
        quad.setTexU(i, u);
        quad.setTexV(i, v);
    }

    private float getCornerHeight(BlockRenderView world, int x, int y, int z, Fluid fluid) {
        int samples = 0;
        float totalHeight = 0.0F;

        for (int i = 0; i < 4; ++i) {
            int x2 = x - (i & 1);
            int z2 = z - (i >> 1 & 1);

            if (world.getFluidState(this.scratchPos.set(x2, y + 1, z2)).getFluid().matchesType(fluid)) {
                return 1.0F;
            }

            BlockPos pos = this.scratchPos.set(x2, y, z2);

            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = blockState.getFluidState();

            if (fluidState.getFluid().matchesType(fluid)) {
                float height = fluidState.getHeight(world, pos);

                if (height >= 0.8F) {
                    totalHeight += height * 10.0F;
                    samples += 10;
                } else {
                    totalHeight += height;
                    ++samples;
                }
            } else if (!blockState.getMaterial().isSolid()) {
                ++samples;
            }
        }

        return totalHeight / (float) samples;
    }

    private static class FabricFluidColorizerAdapter implements ModelQuadColorProvider<FluidState> {
        private FluidRenderHandler handler;

        public void setHandler(FluidRenderHandler handler) {
            this.handler = handler;
        }

        @Override
        public int getColor(FluidState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
            if (this.handler == null) {
                return -1;
            }

            return this.handler.getFluidColor(world, pos, state);
        }
    }
}
