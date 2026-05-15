package com.example.listeners;

import com.example.ItemRules;
import com.example.ResourcemanPlugin;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import java.util.HashMap;
import java.util.Map;

public class SkillResourceListener
{
    private static final int COINS_ID = 995;
    private static final int PLATINUM_TOKEN_ID = 13204;
    private static final int BANK_GROUP_ID = 12;

    @Inject
    private Client client;

    @Inject
    private ResourcemanPlugin plugin;

    @Inject
    private ItemManager itemManager;

    private Map<Integer, Integer> previousInventory = new HashMap<>();
    private Map<Integer, Integer> droppedItems = new HashMap<>();
    private boolean initialized = false;
    private boolean bankOpen = false;

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event)
    {
        if (event.getGroupId() == BANK_GROUP_ID)
        {
            bankOpen = true;
        }
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed event)
    {
        if (event.getGroupId() == BANK_GROUP_ID)
        {
            bankOpen = false;
            ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
            if (inventory != null)
            {
                Map<Integer, Integer> current = new HashMap<>();
                for (Item item : inventory.getItems())
                {
                    if (item.getId() != -1)
                    {
                        current.merge(item.getId(), item.getQuantity(), Integer::sum);
                    }
                }
                previousInventory = current;
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        String option = event.getMenuOption();
        if (!option.equals("Drop"))
        {
            return;
        }

        int itemId = event.getItemId();
        if (itemId == -1)
        {
            return;
        }

        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null)
        {
            return;
        }

        int slotIndex = event.getParam0();
        Item[] items = inventory.getItems();
        if (slotIndex >= 0 && slotIndex < items.length)
        {
            int qty = items[slotIndex].getQuantity();
            droppedItems.merge(itemId, qty, Integer::sum);
        }
        else
        {
            droppedItems.merge(itemId, 1, Integer::sum);
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (event.getContainerId() != InventoryID.INVENTORY.getId())
        {
            return;
        }

        ItemContainer container = event.getItemContainer();
        if (container == null)
        {
            return;
        }

        Map<Integer, Integer> currentInventory = new HashMap<>();
        for (Item item : container.getItems())
        {
            if (item.getId() == -1)
            {
                continue;
            }
            currentInventory.merge(item.getId(), item.getQuantity(), Integer::sum);
        }

        if (!initialized)
        {
            previousInventory = currentInventory;
            initialized = true;
            return;
        }

        if (bankOpen)
        {
            previousInventory = currentInventory;
            return;
        }

        for (Map.Entry<Integer, Integer> entry : currentInventory.entrySet())
        {
            int itemId = entry.getKey();
            int currentQty = entry.getValue();
            int previousQty = previousInventory.getOrDefault(itemId, 0);

            if (currentQty <= previousQty)
            {
                continue;
            }

            if (itemId == COINS_ID || itemId == PLATINUM_TOKEN_ID)
            {
                continue;
            }

            int gained = currentQty - previousQty;

            int dropped = droppedItems.getOrDefault(itemId, 0);
            if (dropped > 0)
            {
                int deduct = Math.min(dropped, gained);
                droppedItems.put(itemId, dropped - deduct);
                gained -= deduct;
            }

            if (gained <= 0)
            {
                continue;
            }

            ItemComposition itemComp = itemManager.getItemComposition(itemId);
            String itemName = itemComp.getName();

            if (itemName == null || itemName.equals("null"))
            {
                continue;
            }

            if (!ItemRules.isAllowedItem(itemName))
            {
                plugin.getResourceTracker().trackResource(itemName, gained);
                plugin.getPanel().update();
            }
        }

        previousInventory = currentInventory;
    }

    public void reset()
    {
        previousInventory.clear();
        droppedItems.clear();
        initialized = false;
        bankOpen = false;
    }
}