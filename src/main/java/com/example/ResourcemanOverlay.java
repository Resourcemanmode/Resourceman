package com.example;

import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class ResourcemanOverlay extends Overlay
{
    @Inject
    public ResourcemanOverlay(ResourcemanPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        return null;
    }
}