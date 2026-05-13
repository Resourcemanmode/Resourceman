package com.example.listeners;

import com.example.ResourcemanConfig;
import com.example.ResourcemanPlugin;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;

public class ShopListener
{
    private static final int SHOP_GROUP_ID = 300;

    @Inject
    private Client client;

    @Inject
    private ResourcemanPlugin plugin;

    @Inject
    private ResourcemanConfig config;

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!config.blockShops())
        {
            return;
        }

        String menuOption = event.getMenuOption();
        if (!menuOption.equals("Buy 1") &&
                !menuOption.equals("Buy 5") &&
                !menuOption.equals("Buy 10") &&
                !menuOption.equals("Buy 50") &&
                !menuOption.equals("Buy"))
        {
            return;
        }

        int groupId = event.getParam1() >> 16;
        if (groupId != SHOP_GROUP_ID)
        {
            return;
        }

        event.consume();
        plugin.triggerViolation();
    }
}