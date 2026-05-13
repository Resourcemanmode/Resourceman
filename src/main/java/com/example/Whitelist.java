package com.example;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains all item names that are allowed to be traded or bought on the GE.
 * Based on all OSRS equipment slot tables from the wiki.
 * Darts and non-blessing ammunition are excluded even though they appear
 * in the one-handed and ammunition slot tables.
 */
public class Whitelist
{
    private static final Set<String> ALLOWED_ITEMS = new HashSet<>();

    static
    {
        // ─── AMMUNITION SLOT (Blessings only) ──────────────────
        ALLOWED_ITEMS.add("holy blessing");
        ALLOWED_ITEMS.add("peaceful blessing");
        ALLOWED_ITEMS.add("unholy blessing");
        ALLOWED_ITEMS.add("honourable blessing");
        ALLOWED_ITEMS.add("war blessing");
        ALLOWED_ITEMS.add("ancient blessing");
        ALLOWED_ITEMS.add("rada's blessing 1");
        ALLOWED_ITEMS.add("rada's blessing 2");
        ALLOWED_ITEMS.add("rada's blessing 3");
        ALLOWED_ITEMS.add("rada's blessing 4");
        ALLOWED_ITEMS.add("ghommal's lucky penny");
        ALLOWED_ITEMS.add("terrifying charm");
        ALLOWED_ITEMS.add("hallowed grapple");
        ALLOWED_ITEMS.add("mith grapple");
    }

    /**
     * Check if an item name is on the whitelist.
     * Item name is lowercased before checking.
     */
    public static boolean isWhitelisted(String itemName)
    {
        if (itemName == null)
        {
            return false;
        }
        return ALLOWED_ITEMS.contains(itemName.toLowerCase());
    }

    /**
     * Check if an item name is a dart (blocked even though
     * it appears in the one-handed weapon slot table).
     */
    public static boolean isDart(String itemName)
    {
        if (itemName == null)
        {
            return false;
        }
        String name = itemName.toLowerCase();
        return name.endsWith(" dart") || name.endsWith(" dart(p)")
                || name.endsWith(" dart(p+)") || name.endsWith(" dart(p++)");
    }

    /**
     * Add a custom item to the whitelist (used by the UI panel).
     */
    public static void addItem(String itemName)
    {
        if (itemName != null && !itemName.isEmpty())
        {
            ALLOWED_ITEMS.add(itemName.toLowerCase());
        }
    }

    /**
     * Remove a custom item from the whitelist (used by the UI panel).
     */
    public static void removeItem(String itemName)
    {
        if (itemName != null)
        {
            ALLOWED_ITEMS.remove(itemName.toLowerCase());
        }
    }
}