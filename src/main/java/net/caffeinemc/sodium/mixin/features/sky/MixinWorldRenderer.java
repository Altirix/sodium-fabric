package net.caffeinemc.sodium.mixin.features.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import net.caffeinemc.sodium.render.terrain.CloudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Mutable
    @Shadow
    private CloudRenderMode lastCloudRenderMode;

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
     * @author Altirix
     * @reason overwrite vanilla cloud renderer to attempt to solve culling issues.
     * Vanilla
     *  - will cull cloud faces Negative X/Z (North, West) 1 cloud cube away from the player.
     *  - at extreme angles inside the cloud it may be possible for a false positive to cull in the Negative X/Y/Z from the player (North, West, Down)
     *  - <a href="https://bugs.mojang.com/browse/MC-205851">MC-205851</a>
     *  - will cull cloud faces Positive X/Z (South, East) 16 cloud cubes (2 cloud chunks) away from the player.
     * Possible Fixes
     *  - Option 1:
     *      - It is possible to reduce this to 8 cloud cubes (1 cloud chunk) away from the player easily with no visual impact
     *  - Option 2:
     *      - It is possible to reduce this to 1 cloud cube away from the player, but this is more involved as
     *      -  The South/East faces will be culled incorrectly when the player is inside the cloud
     *      -- Method 1:
     *          -- It is possible to solve this bug by offsetting the starting position of positive cloud faces by 1
     *          -- to make textures render in the correct position the offset is taken away from the U/V
     *          -- However this causes the last faces to not render at the edge of the cloud map as we iterate 1 less in Negative X/Z
     *          -- it is possible to fix by just manually including 1 face extra South/East face in the Negative X/Z directions.
     *      -- Method 2:
     *          -- it should be possible to check if the player is inside a cloud using the CloudY position
     *          -- then enable the specific clouds South/East Face
     *          -- should be less hacky
     */
    @Overwrite
    private BufferBuilder.BuiltBuffer renderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color) {
        float k = (float) MathHelper.floor(x) * 0.00390625F;
        float l = (float) MathHelper.floor(z) * 0.00390625F;

        float red = (float)color.x;
        float green = (float)color.y;
        float blue = (float)color.z;


        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        float cloudY = (float)Math.floor(y / 4.0) * 4.0F;
        float cloudX = 0;
        float cloudZ = 0;

        if (this.lastCloudRenderMode == CloudRenderMode.FANCY) {
            for(int cloudChunkX = -4; cloudChunkX < 4; ++cloudChunkX) {
                for(int cloudChunkZ = -4; cloudChunkZ < 4; ++cloudChunkZ) {

                    cloudX = (float)(cloudChunkX * 8);
                    cloudZ = (float)(cloudChunkZ * 8);

                    if (cloudY > -5.5F) {  // Bottom Face
                        CloudRenderer.renderCloudBottomFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }

                    if (cloudY <= 5.0F) { // Top Face
                        CloudRenderer.renderCloudTopFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }

                    if (cloudChunkX > -1) { // West Face
                        CloudRenderer.renderCloudWestFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }

                    if (cloudChunkX < 0) { // East Face
                        CloudRenderer.renderCloudEastFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }

                    if (cloudChunkZ > -1) { // North Face
                        CloudRenderer.renderCloudNorthFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }

                    if (cloudChunkZ < 0) { // South Face
                        CloudRenderer.renderCloudSouthFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }
                }
                CloudRenderer.renderCloudSingleEastFace(builder,-4 * 8 - 1,cloudY,cloudX,k,l,red,green,blue);// just need to iterate a single axis
                CloudRenderer.renderCloudSingleSouthFace(builder,cloudX,cloudY,-4 * 8 - 1,k,l,red,green,blue);
            }
        } else {
            CloudRenderer.renderCloudsFast(builder,cloudY,k,l,red,green,blue);
        }

        return builder.end();
    }
}


