package net.caffeinemc.sodium.mixin.features.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import net.caffeinemc.sodium.SodiumClientMod;
import net.caffeinemc.sodium.config.user.UserConfig;
import net.caffeinemc.sodium.render.terrain.CloudRenderer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.caffeinemc.sodium.render.terrain.CloudRenderer.TEXTURE_OFFSET;

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
     *  - regardless of render distance the cloud map will always be 64 x 64 for both fast (stride 32) and fancy (stride 8)
     *  - a cloud chunk is 64 blocks wide(?)
     * Possible Fixes
     *  - Option 1:
     *      - It is possible to reduce this to 8 cloud cubes (1 cloud chunk) away from the player easily with no visual impact
     *  - Option 2:
     *      - It is possible to reduce this to 1 cloud cube away from the player, but this is more involved as
     *      -  The South/East faces will be culled incorrectly when the player is inside the cloud
     *      - Check If Player Is In Cloud
     *          -- enable that clouds South/East Face
     */
    @Overwrite
    private BufferBuilder.BuiltBuffer renderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color) {
        float k = (float) MathHelper.floor(x) * TEXTURE_OFFSET;
        float l = (float) MathHelper.floor(z) * TEXTURE_OFFSET;

        float red = (float)color.x;
        float green = (float)color.y;
        float blue = (float)color.z;


        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        float cloudY = (float)Math.floor(y / 4.0) * 4.0F;

        if (this.lastCloudRenderMode == CloudRenderMode.FANCY) {

            if (cloudY < 1F && cloudY > -(CloudRenderer.CLOUD_HEIGHT + 1)) { // Inside Cloud;
                CloudRenderer.renderCloudInsideEastFace(builder,0,cloudY,0,k,l,red,green,blue);
                CloudRenderer.renderCloudInsideSouthFace(builder,0,cloudY,0,k,l,red,green,blue);
            }
            for(int cloudChunkX = -4; cloudChunkX < 4; ++cloudChunkX) {
                for(int cloudChunkZ = -4; cloudChunkZ < 4; ++cloudChunkZ) {

                    float cloudX = (float)(cloudChunkX * 8);
                    float cloudZ = (float)(cloudChunkZ * 8);

                    if (cloudY > -(CloudRenderer.CLOUD_HEIGHT + 1)) {  // Bottom Face
                        CloudRenderer.renderCloudBottomFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }
                    if (cloudY < 1F) { // Top Face
                        CloudRenderer.renderCloudTopFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }
                    if (cloudChunkX > -1) { // West Face
                        CloudRenderer.renderCloudWestFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }
                    else { // East Face
                        CloudRenderer.renderCloudEastFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }
                    if (cloudChunkZ > -1) { // North Face
                        CloudRenderer.renderCloudNorthFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }
                    else { // South Face
                        CloudRenderer.renderCloudSouthFace(builder,cloudX,cloudY,cloudZ,k,l,red,green,blue);
                    }
                }
            }
        } else {
            CloudRenderer.renderCloudsFast(builder,cloudY,k,l,red,green,blue);
        }

        return builder.end();
    }
}


