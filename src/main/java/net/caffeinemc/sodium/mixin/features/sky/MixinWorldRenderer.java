package net.caffeinemc.sodium.mixin.features.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.caffeinemc.sodium.render.terrain.CloudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow
    @Final
    private static Identifier CLOUDS;
    @Shadow
    @Final
    private MinecraftClient client;

    private static final Logger LOGGER = LogManager.getLogger("Sodium");
    @Mutable
    @Shadow
    private VertexBuffer cloudsBuffer;
    @Shadow
    private ClientWorld world;
    @Shadow
    private int ticks;
    @Mutable
    @Shadow
    private int lastCloudsBlockY;
    @Mutable
    @Shadow
    private int lastCloudsBlockZ;
    @Mutable
    @Shadow
    private CloudRenderMode lastCloudRenderMode;
    private int lastCloudsBlockX;
    @Mutable
    @Shadow
    private Vec3d lastCloudsColor;
    @Mutable
    @Shadow
    private boolean cloudsDirty;

    /**
     * <p>Prevents the sky layer from rendering when the fog distance is reduced
     * from the default. This helps prevent situations where the sky can be seen
     * through chunks culled by fog occlusion. This also fixes the vanilla issue
     * <a href="https://bugs.mojang.com/browse/MC-152504">MC-152504</a> since it
     * is also caused by being able to see the sky through invisible chunks.</p>
     *
     * <p>However, this fix comes with some caveats. When underwater, it becomes
     * impossible to see the sun, stars, and moon since the sky is not rendered.
     * While this does not exactly match the vanilla game, it is consistent with
     * what Bedrock Edition does, so it can be considered vanilla-style. This is
     * also more "correct" in the sense that underwater fog is applied to chunks
     * outside of water, so the fog should also be covering the sun and sky.</p>
     *
     * <p>When updating Sodium to new releases of the game, please check for new
     * ways the fog can be reduced in {@link BackgroundRenderer#applyFog(Camera, BackgroundRenderer.FogType, float, boolean, float)}.</p>
     */
    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void preRenderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        // Cancels sky rendering when the camera is submersed underwater.
        // This prevents the sky from being visible through chunks culled by Sodium's fog occlusion.
        // Fixes https://bugs.mojang.com/browse/MC-152504.
        // Credit to bytzo for noticing the change in 1.18.2.
        if (camera.getSubmersionType() == CameraSubmersionType.WATER) {
            ci.cancel();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double d, double e, double f) {
        float g = this.world.getDimensionEffects().getCloudsHeight();
        if (!Float.isNaN(g)) {
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(true);
            double k = ((float)this.ticks + tickDelta) * 0.03F;
            double l = (d + k) / 12.0;
            double m = g - (float)e + 0.33F;
            double n = f / 12.0 + 0.33000001311302185;
            l -= MathHelper.floor(l / 2048.0) * 2048;
            n -= MathHelper.floor(n / 2048.0) * 2048;
            float o = (float)(l - (double)MathHelper.floor(l));
            float p = (float)(m / 4.0 - (double)MathHelper.floor(m / 4.0)) * 4.0F;
            float q = (float)(n - (double)MathHelper.floor(n));
            Vec3d vec3d = this.world.getCloudsColor(tickDelta);
            int r = (int)Math.floor(l);
            int s = (int)Math.floor(m / 4.0);
            int t = (int)Math.floor(n);
            if (r != this.lastCloudsBlockX || s != this.lastCloudsBlockY || t != this.lastCloudsBlockZ || this.client.options.getCloudRenderModeValue() != this.lastCloudRenderMode || this.lastCloudsColor.squaredDistanceTo(vec3d) > 2.0E-4) {
                this.lastCloudsBlockX = r;
                this.lastCloudsBlockY = s;
                this.lastCloudsBlockZ = t;
                this.lastCloudsColor = vec3d;
                this.lastCloudRenderMode = this.client.options.getCloudRenderModeValue();
                this.cloudsDirty = true;
            }

            if (this.cloudsDirty) {
                this.cloudsDirty = false;
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                if (this.cloudsBuffer != null) {
                    this.cloudsBuffer.close();
                }

                this.cloudsBuffer = new VertexBuffer();
                BufferBuilder.BuiltBuffer builtBuffer = this.renderClouds(bufferBuilder, l, m, n, vec3d);
                this.cloudsBuffer.bind();
                this.cloudsBuffer.upload(builtBuffer);
                VertexBuffer.unbind();
            }

            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
            RenderSystem.setShaderTexture(0, CLOUDS);
            BackgroundRenderer.setFogBlack();
            matrices.push();
            matrices.scale(12.0F, 1.0F, 12.0F);
            matrices.translate(-o, p, -q);
            if (this.cloudsBuffer != null) {
                this.cloudsBuffer.bind();
                int u = this.lastCloudRenderMode == CloudRenderMode.FANCY ? 0 : 1;

                for(int v = u; v < 2; ++v) {
                    if (v == 0) {
                        RenderSystem.colorMask(false, false, false, false);
                    } else {
                        RenderSystem.colorMask(true, true, true, true);
                    }

                    Shader shader = RenderSystem.getShader();
                    this.cloudsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
                }

                VertexBuffer.unbind();
            }

            matrices.pop();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private BufferBuilder.BuiltBuffer renderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color) {
        float k = (float)MathHelper.floor(x) * 0.00390625F;
        float l = (float)MathHelper.floor(z) * 0.00390625F;
        float m = (float)color.x;
        float n = (float)color.y;
        float o = (float)color.z;

        float p = m * 0.9F;
        float q = n * 0.9F;
        float r = o * 0.9F;

        float s = m * 0.7F;
        float t = n * 0.7F;
        float u = o * 0.7F;

        float v = m * 0.8F;
        float w = n * 0.8F;
        float aa = o * 0.8F;

        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        float cloudY = (float)Math.floor(y / 4.0) * 4.0F;

        if (this.lastCloudRenderMode == CloudRenderMode.FANCY) {
            for(int cloudChunkX = -3; cloudChunkX <= 4; ++cloudChunkX) {
                for(int cloudChunkZ = -3; cloudChunkZ <= 4; ++cloudChunkZ) {

                    float cloudX = (float)(cloudChunkX * 8);
                    float cloudZ = (float)(cloudChunkZ * 8);

                    if (cloudY > -5.0F) {  // Bottom Face
                        CloudRenderer.renderCloudBottomFace(builder,cloudX,cloudY,cloudZ,k,l,s,t,u);
                    }

                    if (cloudY <= 5.0F) { // Top Face
                        CloudRenderer.renderCloudTopFace(builder,cloudX,cloudY,cloudZ,k,l,m,n,o);
                    }

                    if (cloudChunkX > -1) { // West Face
                        CloudRenderer.renderCloudWestFace(builder,cloudX,cloudY,cloudZ,k,l);
                    }

                    if (cloudChunkX < 0) { // East Face
                        CloudRenderer.renderCloudEastFace(builder,cloudX,cloudY,cloudZ,k,l);
                    }

                    if (cloudChunkZ > -1) { // North Face
                        CloudRenderer.renderCloudNorthFace(builder,cloudX,cloudY,cloudZ,k,l);
                    }

                    if (cloudChunkZ < 0) { // South Face
                        CloudRenderer.renderCloudSouthFace(builder,cloudX,cloudY,cloudZ,k,l);
                    }
                }
            }
        } else {
            for(int ah = -32; ah < 32; ah += 32) {
                for(int ai = -32; ai < 32; ai += 32) {
                    builder.vertex(ah, cloudY, ai + 32).texture((float)(ah) * 0.00390625F + k, (float)(ai + 32) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex(ah + 32, cloudY, ai + 32).texture((float)(ah + 32) * 0.00390625F + k, (float)(ai + 32) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex(ah + 32, cloudY, ai).texture((float)(ah + 32) * 0.00390625F + k, (float)(ai) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex(ah, cloudY, ai).texture((float)(ah) * 0.00390625F + k, (float)(ai) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                }
            }
        }

        return builder.end();
    }
}
