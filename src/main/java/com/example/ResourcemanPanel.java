package com.example;

import com.example.data.ResourceTracker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

public class ResourcemanPanel extends PluginPanel
{
    private final ResourcemanPlugin plugin;

    // Resources tab
    private JPanel resourceListPanel;
    private JLabel sessionResourceLabel;
    private JLabel allTimeResourceLabel;

    // Violations tab
    private JLabel sessionViolationsLabel;
    private JLabel allTimeViolationsLabel;
    private JLabel geViolationsLabel;
    private JLabel tradeViolationsLabel;
    private JLabel groundViolationsLabel;
    private JLabel telegrabViolationsLabel;
    private JLabel shopViolationsLabel;

    public ResourcemanPanel(ResourcemanPlugin plugin)
    {
        this.plugin = plugin;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        build();
    }

    private void build()
    {
        // ─── Title ─────────────────────────────────────────
        JLabel title = new JLabel("Resourceman Mode");
        title.setForeground(Color.WHITE);
        title.setFont(FontManager.getRunescapeBoldFont());
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ─── Tabs ───────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ColorScheme.DARK_GRAY_COLOR);
        tabs.setForeground(Color.WHITE);

        tabs.addTab("Resources", buildResourcesTab());
        tabs.addTab("Violations", buildViolationsTab());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildResourcesTab()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        // Summary row
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1));
        summaryPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        sessionResourceLabel = new JLabel("Session: 0 items");
        sessionResourceLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        sessionResourceLabel.setFont(FontManager.getRunescapeSmallFont());

        allTimeResourceLabel = new JLabel("All-time: 0 items");
        allTimeResourceLabel.setForeground(Color.WHITE);
        allTimeResourceLabel.setFont(FontManager.getRunescapeSmallFont());

        summaryPanel.add(allTimeResourceLabel);
        summaryPanel.add(sessionResourceLabel);
        panel.add(summaryPanel);

        // Resource list
        resourceListPanel = new JPanel();
        resourceListPanel.setLayout(new BoxLayout(resourceListPanel, BoxLayout.Y_AXIS));
        resourceListPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JScrollPane scrollPane = new JScrollPane(resourceListPanel);
        scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(0, 400));

        panel.add(scrollPane);

        return panel;
    }

    private JPanel buildViolationsTab()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Session violations
        JLabel sessionHeader = new JLabel("── Session ──");
        sessionHeader.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        sessionHeader.setFont(FontManager.getRunescapeSmallFont());
        sessionHeader.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(sessionHeader);

        sessionViolationsLabel = makeViolationLabel("Total: 0");
        panel.add(sessionViolationsLabel);

        // All time violations
        JLabel allTimeHeader = new JLabel("── All-time ──");
        allTimeHeader.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        allTimeHeader.setFont(FontManager.getRunescapeSmallFont());
        allTimeHeader.setAlignmentX(CENTER_ALIGNMENT);
        allTimeHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(allTimeHeader);

        allTimeViolationsLabel = makeViolationLabel("Total: 0");
        panel.add(allTimeViolationsLabel);

        // Breakdown
        JLabel breakdownHeader = new JLabel("── Breakdown ──");
        breakdownHeader.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        breakdownHeader.setFont(FontManager.getRunescapeSmallFont());
        breakdownHeader.setAlignmentX(CENTER_ALIGNMENT);
        breakdownHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(breakdownHeader);

        geViolationsLabel = makeViolationLabel("Grand Exchange: 0");
        tradeViolationsLabel = makeViolationLabel("Player Trade: 0");
        groundViolationsLabel = makeViolationLabel("Ground Item: 0");
        telegrabViolationsLabel = makeViolationLabel("Telegrab: 0");
        shopViolationsLabel = makeViolationLabel("NPC Shop: 0");

        panel.add(geViolationsLabel);
        panel.add(tradeViolationsLabel);
        panel.add(groundViolationsLabel);
        panel.add(telegrabViolationsLabel);
        panel.add(shopViolationsLabel);

        return panel;
    }

    private JLabel makeViolationLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(FontManager.getRunescapeSmallFont());
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return label;
    }

    /**
     * Called to refresh all data in the panel.
     */
    public void update()
    {
        SwingUtilities.invokeLater(() ->
        {
            ResourceTracker tracker = plugin.getResourceTracker();
            if (tracker == null)
            {
                return;
            }

            // Update resource summary
            sessionResourceLabel.setText("Session: " +
                    String.format("%,d", tracker.getSessionResourceCount()) + " items");
            allTimeResourceLabel.setText("All-time: " +
                    String.format("%,d", tracker.getAllTimeResourceCount()) + " items");

            // Update resource list
            resourceListPanel.removeAll();
            for (Map.Entry<String, Integer> entry : tracker.getAllTimeResources().entrySet())
            {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(ColorScheme.DARK_GRAY_COLOR);
                row.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

                JLabel nameLabel = new JLabel(capitalize(entry.getKey()));
                nameLabel.setForeground(Color.WHITE);
                nameLabel.setFont(FontManager.getRunescapeSmallFont());

                JLabel quantLabel = new JLabel(String.format("%,d", entry.getValue()));
                quantLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
                quantLabel.setFont(FontManager.getRunescapeSmallFont());

                row.add(nameLabel, BorderLayout.WEST);
                row.add(quantLabel, BorderLayout.EAST);
                resourceListPanel.add(row);
            }

            resourceListPanel.revalidate();
            resourceListPanel.repaint();

            // Update violations
            sessionViolationsLabel.setText("Total: " + tracker.getSessionViolations());
            allTimeViolationsLabel.setText("Total: " + tracker.getAllTimeViolations());
            geViolationsLabel.setText("Grand Exchange: " + tracker.getAllTimeGEViolations());
            tradeViolationsLabel.setText("Player Trade: " + tracker.getAllTimeTradeViolations());
            groundViolationsLabel.setText("Ground Item: " + tracker.getAllTimeGroundViolations());
            telegrabViolationsLabel.setText("Telegrab: " + tracker.getAllTimeTelegrabViolations());
            shopViolationsLabel.setText("NPC Shop: " + tracker.getAllTimeShopViolations());
        });
    }

    private String capitalize(String str)
    {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words)
        {
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1));
        }
        return sb.toString();
    }
}