package com.example.listeners;

import com.example.ItemRules;
import com.example.ResourcemanPlugin;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class TradeListener
{
    private static final int[] TRADE_GROUPS = {334, 335};

    @Inject
    private Client client;

    @Inject
    private ResourcemanPlugin plugin;

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!event.getMenuOption().equals("Accept"))
        {
            return;
        }

        int groupId = event.getParam1() >> 16;
        boolean isTrade = false;
        for (int g : TRADE_GROUPS)
        {
            if (groupId == g)
            {
                isTrade = true;
                break;
            }
        }

        if (!isTrade)
        {
            return;
        }

        if (hasViolation())
        {
            event.consume();
            plugin.triggerViolation();
        }
    }

    private boolean hasViolation()
    {
        for (int groupId : TRADE_GROUPS)
        {
            for (int child = 0; child < 60; child++)
            {
                Widget w = client.getWidget(groupId, child);
                if (w == null)
                {
                    continue;
                }

                Widget[] dynamics = w.getDynamicChildren();
                if (dynamics != null)
                {
                    for (Widget d : dynamics)
                    {
                        if (d != null && d.getItemId() != -1 && isViolationItem(d.getItemId()))
                        {
                            return true;
                        }
                    }
                }

                if (w.getItemId() != -1 && isViolationItem(w.getItemId()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isViolationItem(int itemId)
    {
        ItemComposition itemComp = client.getItemDefinition(itemId);
        if (!itemComp.isTradeable())
        {
            return false;
        }
        String itemName = itemComp.getName();
        return !ItemRules.isAllowedItem(itemName);
    }
}