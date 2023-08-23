package me.jellysquid.mods.sodium.client.world;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;

public final class WorldRendererExtended {
    private final SodiumWorldRenderer sodium$worldRenderer;

    public WorldRendererExtended(SodiumWorldRenderer sodium$worldRenderer) {
        this.sodium$worldRenderer = sodium$worldRenderer;
    }

    @inline
    public SodiumWorldRenderer sodium$getWorldRenderer() {
        return this.sodium$worldRenderer;
    }
}
