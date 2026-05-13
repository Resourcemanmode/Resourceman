package com.example.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.runelite.client.config.ConfigManager;

public class ResourceTracker
{
    private static final String CONFIG_GROUP = "resourceman";
    private static final String RESOURCES_KEY = "trackedResources";
    private static final String VIOLATIONS_KEY = "trackedViolations";

    private final ConfigManager configManager;

    // All time resource counts - item name -> quantity
    private Map<String, Integer> allTimeResources = new HashMap<>();

    // Session resource counts
    private Map<String, Integer> sessionResources = new HashMap<>();

    // Violation counts
    private int allTimeViolations = 0;
    private int allTimeGEViolations = 0;
    private int allTimeTradeViolations = 0;
    private int allTimeGroundViolations = 0;
    private int allTimeTelegrabViolations = 0;
    private int allTimeShopViolations = 0;

    private int sessionViolations = 0;
    private int sessionGEViolations = 0;
    private int sessionTradeViolations = 0;
    private int sessionGroundViolations = 0;
    private int sessionTelegrabViolations = 0;
    private int sessionShopViolations = 0;

    public ResourceTracker(ConfigManager configManager)
    {
        this.configManager = configManager;
        load();
    }

    // ─── Resource Tracking ─────────────────────────────────

    public void trackResource(String itemName, int quantity)
    {
        if (itemName == null || itemName.isEmpty())
        {
            return;
        }

        String name = itemName.toLowerCase();

        // Update all time
        allTimeResources.merge(name, quantity, Integer::sum);

        // Update session
        sessionResources.merge(name, quantity, Integer::sum);

        save();
    }

    public Map<String, Integer> getAllTimeResources()
    {
        // Return sorted by quantity descending
        return allTimeResources.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Integer> getSessionResources()
    {
        return sessionResources.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public int getAllTimeResourceCount()
    {
        return allTimeResources.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getSessionResourceCount()
    {
        return sessionResources.values().stream().mapToInt(Integer::intValue).sum();
    }

    // ─── Violation Tracking ────────────────────────────────

    public enum ViolationType
    {
        GRAND_EXCHANGE,
        TRADE,
        GROUND_ITEM,
        TELEGRAB,
        SHOP
    }

    public void trackViolation(ViolationType type)
    {
        allTimeViolations++;
        sessionViolations++;

        switch (type)
        {
            case GRAND_EXCHANGE:
                allTimeGEViolations++;
                sessionGEViolations++;
                break;
            case TRADE:
                allTimeTradeViolations++;
                sessionTradeViolations++;
                break;
            case GROUND_ITEM:
                allTimeGroundViolations++;
                sessionGroundViolations++;
                break;
            case TELEGRAB:
                allTimeTelegrabViolations++;
                sessionTelegrabViolations++;
                break;
            case SHOP:
                allTimeShopViolations++;
                sessionShopViolations++;
                break;
        }

        save();
    }

    public int getAllTimeViolations() { return allTimeViolations; }
    public int getAllTimeGEViolations() { return allTimeGEViolations; }
    public int getAllTimeTradeViolations() { return allTimeTradeViolations; }
    public int getAllTimeGroundViolations() { return allTimeGroundViolations; }
    public int getAllTimeTelegrabViolations() { return allTimeTelegrabViolations; }
    public int getAllTimeShopViolations() { return allTimeShopViolations; }

    public int getSessionViolations() { return sessionViolations; }
    public int getSessionGEViolations() { return sessionGEViolations; }
    public int getSessionTradeViolations() { return sessionTradeViolations; }
    public int getSessionGroundViolations() { return sessionGroundViolations; }
    public int getSessionTelegrabViolations() { return sessionTelegrabViolations; }
    public int getSessionShopViolations() { return sessionShopViolations; }

    // ─── Persistence ───────────────────────────────────────

    private void save()
    {
        // Save resources as comma separated "name:quantity" pairs
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : allTimeResources.entrySet())
        {
            if (sb.length() > 0) sb.append(",");
            sb.append(entry.getKey()).append(":").append(entry.getValue());
        }
        configManager.setConfiguration(CONFIG_GROUP, RESOURCES_KEY, sb.toString());

        // Save violations as pipe separated values
        String violations = allTimeViolations + "|" +
                allTimeGEViolations + "|" +
                allTimeTradeViolations + "|" +
                allTimeGroundViolations + "|" +
                allTimeTelegrabViolations + "|" +
                allTimeShopViolations;
        configManager.setConfiguration(CONFIG_GROUP, VIOLATIONS_KEY, violations);
    }

    private void load()
    {
        // Load resources
        String resourceData = configManager.getConfiguration(CONFIG_GROUP, RESOURCES_KEY);
        if (resourceData != null && !resourceData.isEmpty())
        {
            for (String entry : resourceData.split(","))
            {
                String[] parts = entry.split(":");
                if (parts.length == 2)
                {
                    try
                    {
                        allTimeResources.put(parts[0], Integer.parseInt(parts[1]));
                    }
                    catch (NumberFormatException e)
                    {
                        // Skip malformed entries
                    }
                }
            }
        }

        // Load violations
        String violationData = configManager.getConfiguration(CONFIG_GROUP, VIOLATIONS_KEY);
        if (violationData != null && !violationData.isEmpty())
        {
            String[] parts = violationData.split("\\|");
            if (parts.length == 6)
            {
                try
                {
                    allTimeViolations = Integer.parseInt(parts[0]);
                    allTimeGEViolations = Integer.parseInt(parts[1]);
                    allTimeTradeViolations = Integer.parseInt(parts[2]);
                    allTimeGroundViolations = Integer.parseInt(parts[3]);
                    allTimeTelegrabViolations = Integer.parseInt(parts[4]);
                    allTimeShopViolations = Integer.parseInt(parts[5]);
                }
                catch (NumberFormatException e)
                {
                    // Skip malformed data
                }
            }
        }
    }

    public void resetSession()
    {
        sessionResources.clear();
        sessionViolations = 0;
        sessionGEViolations = 0;
        sessionTradeViolations = 0;
        sessionGroundViolations = 0;
        sessionTelegrabViolations = 0;
        sessionShopViolations = 0;
    }
}