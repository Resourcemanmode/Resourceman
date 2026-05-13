package com.example;

public class ItemRules
{
    public static boolean isAllowedItem(String itemName)
    {
        if (itemName == null || itemName.equals("null") || itemName.isEmpty())
        {
            return true;
        }

        String lower = itemName.toLowerCase().trim();

        if (Whitelist.isDart(lower))
        {
            return false;
        }

        if (Whitelist.isWhitelisted(lower))
        {
            return true;
        }

        if (isResource(lower))
        {
            return false;
        }

        if (isEquipment(lower))
        {
            return true;
        }

        return false;
    }

    private static boolean isResource(String lower)
    {
        if (lower.endsWith("(1)") || lower.endsWith("(2)") ||
                lower.endsWith("(3)") || lower.endsWith("(4)"))
        {
            return true;
        }

        if (lower.endsWith("rune") || lower.endsWith("runes"))
        {
            return true;
        }

        if (lower.endsWith("arrow") || lower.endsWith("arrows"))
        {
            return true;
        }

        if (lower.endsWith("bolt") || lower.endsWith("bolts"))
        {
            return true;
        }

        if (lower.endsWith("javelin") || lower.endsWith("javelins"))
        {
            return true;
        }

        // Specific fish that contain equipment keyword "sword"
        if (lower.contains("swordfish"))
        {
            return true;
        }

        // Specific tar items
        if (lower.equals("swamp tar") || lower.equals("guam tar") ||
                lower.equals("marrentill tar") || lower.equals("tarromin tar") ||
                lower.equals("harralander tar"))
        {
            return true;
        }

        String[] resourceKeywords = {
                " ore", " bar", " logs", " log", "raw ", "grimy ", "clean ",
                " seed", " herb", " fish", "bones", "hide", "leather", "fur",
                "feather", " essence", "clay", "flax", "coal", "grain",
                "potato", "onion", "cabbage", "berry", "meat", "egg",
                "milk", "wool", "silk", "sand", "molten glass",
                "tinderbox", "noted", "potion", " mix",
                "brew", " dose", "bait", "dynamite", "compost",
                "limestone", "bucket", "pot ", "bowl", "vial", "jug"
        };

        for (String keyword : resourceKeywords)
        {
            if (lower.contains(keyword))
            {
                return true;
            }
        }

        return false;
    }

    private static boolean isEquipment(String lower)
    {
        String[] materials = {
                "bronze ", "iron ", "steel ", "black ", "mithril ", "adamant ",
                "rune ", "dragon ", "barrows", "crystal ", "3rd age",
                "bandos", "armadyl", "ancestral", "torva", "pernix", "virtus",
                "dharok", "guthan", "torag", "verac", "ahrim", "karil",
                "void ", "graceful", "shayzien", "hosidius", "lovakengj",
                "arceuus", "piscarilius", "gilded "
        };

        for (String material : materials)
        {
            if (lower.startsWith(material))
            {
                return true;
            }
        }

        String[] equipKeywords = {
                "scimitar", "sword", "dagger", "mace", "axe", "bow", "staff",
                "wand", "shield", "helm", "helmet", "platebody", "platelegs",
                "plateskirt", "chainbody", "chainlegs", "chaps", "coif",
                "boots", "gloves", "cape", "amulet", "necklace", "ring",
                "bracelet", "hat", "hood", "mask", "top",
                "robe", "skirt", "cloak", "whip", "halberd", "spear", "hasta",
                "crossbow", "ballista", "blowpipe", "trident", "gauntlets",
                "vambraces", "kiteshield", "sq shield", "defender", "blessed",
                "armour", "armor", "tassets", "cuisse", "greaves",
                "full helm", "med helm", "torture", "anguish", "occult",
                "fury", "glory", "amulet of", "berserker", "archer",
                "seers", "warrior", "recoil", "suffering", "tyrannical",
                "treasonous", "zenyte", "onyx", "dragonstone",
                "ornament kit", "d'hide"
        };

        for (String keyword : equipKeywords)
        {
            if (lower.contains(keyword))
            {
                return true;
            }
        }

        return false;
    }
}