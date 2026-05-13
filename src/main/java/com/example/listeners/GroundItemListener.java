package com.example.listeners;

import com.example.ItemRules;
import com.example.ResourcemanPlugin;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuEntry;
import net.runelite.api.TileItem;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.eventbus.Subscribe;

public class GroundItemListener
{
    @Inject
    private Client client;

    @Inject
    private ResourcemanPlugin plugin;

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        String option = event.getOption();

        if (!option.equals("Take") && !option.equals("Pick-up"))
        {
            return;
        }

        int itemId = event.getIdentifier();
        if (itemId == -1)
        {
            return;
        }

        ItemComposition itemComp = client.getItemDefinition(itemId);

        // Untradeables are always fine
        if (!itemComp.isTradeable())
        {
            return;
        }

        String itemName = itemComp.getName();

        // If item is allowed (equipment, blessing etc) anyone can pick it up
        if (ItemRules.isAllowedItem(itemName))
        {
            return;
        }

        // Item is a resource - check ownership
        int x = event.getActionParam0();
        int y = event.getActionParam1();
        int plane = client.getPlane();

        if (x >= 0 && x < 104 && y >= 0 && y < 104)
        {
            net.runelite.api.Tile tile = client.getScene().getTiles()[plane][x][y];
            if (tile != null)
            {
                java.util.List<TileItem> groundItems = tile.getGroundItems();
                if (groundItems != null)
                {
                    for (TileItem tileItem : groundItems)
                    {
                        if (tileItem.getId() == itemId)
                        {
                            // If it's yours allow it
                            if (tileItem.getOwnership() == TileItem.OWNERSHIP_SELF ||
                                    tileItem.getOwnership() == TileItem.OWNERSHIP_GROUP)
                            {
                                return;
                            }
                            break;
                        }
                    }
                }
            }
        }

        // Resource not owned by you - remove menu entry
        client.setMenuEntries(removeEntry(client.getMenuEntries(), event.getMenuEntry()));
        plugin.triggerViolation();
    }

    private MenuEntry[] removeEntry(MenuEntry[] entries, MenuEntry toRemove)
    {
        java.util.ArrayList<MenuEntry> list = new java.util.ArrayList<>();
        for (MenuEntry entry : entries)
        {
            if (entry != toRemove)
            {
                list.add(entry);
            }
        }
        return list.toArray(new MenuEntry[0]);
    }
}