package com.example.listeners;

import com.example.ItemRules;
import com.example.ResourcemanPlugin;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.Text;

public class GrandExchangeListener
{
    private static final int GE_SEARCH_GROUP = 162;
    private static final int GE_SEARCH_RESULTS_CHILD = 52;

    @Inject
    private Client client;

    @Inject
    private ResourcemanPlugin plugin;

    @Inject
    private ItemManager itemManager;

    @Subscribe
    public void onGameTick(GameTick event)
    {
        Widget searchResults = client.getWidget(GE_SEARCH_GROUP, GE_SEARCH_RESULTS_CHILD);
        if (searchResults == null)
        {
            return;
        }

        Widget[] children = searchResults.getDynamicChildren();
        if (children == null)
        {
            return;
        }

        for (int i = 0; i + 2 < children.length; i += 3)
        {
            Widget textWidget = children[i];
            Widget spriteWidget = children[i + 1];
            Widget itemWidget = children[i + 2];

            if (textWidget == null || spriteWidget == null || itemWidget == null)
            {
                continue;
            }

            int itemId = itemWidget.getItemId();
            if (itemId == -1)
            {
                continue;
            }

            ItemComposition comp = itemManager.getItemComposition(itemId);
            String itemName = comp == null ? null : comp.getName();

            if (itemName == null || itemName.isEmpty() || itemName.equals("null"))
            {
                continue;
            }

            if (!ItemRules.isAllowedItem(itemName))
            {
                // Grey out sprite and item only, leave text visible for clicking
                spriteWidget.setOpacity(150);
                itemWidget.setOpacity(150);
                textWidget.setTextColor(0x808080);
            }
            else
            {
                spriteWidget.setOpacity(0);
                itemWidget.setOpacity(0);
                textWidget.setTextColor(0xffffff);
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!event.getMenuOption().equals("Select"))
        {
            return;
        }

        int groupId = event.getParam1() >> 16;
        if (groupId != GE_SEARCH_GROUP)
        {
            return;
        }

        String menuTarget = event.getMenuTarget();
        if (menuTarget == null || menuTarget.isEmpty())
        {
            return;
        }

        String itemName = Text.removeTags(menuTarget).trim();
        if (!ItemRules.isAllowedItem(itemName))
        {
            event.consume();
            plugin.triggerViolation();
        }
    }
}