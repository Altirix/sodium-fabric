package me.jellysquid.mods.sodium.client.world;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;

public abstract class WorldRendererMixin extends WorldRenderer implements WorldRendererExtended {
    @Override
    public WorldRenderer getWorldRenderer() {
        return this;
    }
}
