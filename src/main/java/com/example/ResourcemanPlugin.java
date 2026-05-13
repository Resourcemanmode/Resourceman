package com.example;

import com.example.data.ResourceTracker;
import com.example.listeners.GrandExchangeListener;
import com.example.listeners.GroundItemListener;
import com.example.listeners.ShopListener;
import com.example.listeners.TradeListener;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ChatMessageType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
		name = "Resourceman Mode",
		description = "Enforces self-obtained resources only"
)
public class ResourcemanPlugin extends Plugin
{
	public static final String VIOLATION_MESSAGE = "Resourcemen gather their own resources";
	public static final int VIOLATION_SOUND = 2277;

	@Inject
	private Client client;

	@Inject
	private ResourcemanConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ResourcemanOverlay overlay;

	@Inject
	private GrandExchangeListener grandExchangeListener;

	@Inject
	private TradeListener tradeListener;

	@Inject
	private GroundItemListener groundItemListener;

	@Inject
	private ShopListener shopListener;

	private ResourceTracker resourceTracker;
	private ResourcemanPanel panel;
	private NavigationButton navButton;

	public ResourceTracker getResourceTracker()
	{
		return resourceTracker;
	}

	public ResourcemanPanel getPanel()
	{
		return panel;
	}

	public ItemManager getItemManager()
	{
		return itemManager;
	}

	private BufferedImage buildIcon()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(new Color(180, 120, 40));
		g.fillRect(0, 0, 16, 16);
		g.setColor(Color.WHITE);
		g.drawString("R", 3, 12);
		g.dispose();
		return image;
	}

	@Override
	protected void startUp() throws Exception
	{
		resourceTracker = new ResourceTracker(configManager);

		panel = new ResourcemanPanel(this);
		panel.update();

		navButton = NavigationButton.builder()
				.tooltip("Resourceman Mode")
				.icon(buildIcon())
				.priority(5)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
		overlayManager.add(overlay);

		eventBus.register(grandExchangeListener);
		eventBus.register(tradeListener);
		eventBus.register(groundItemListener);
		eventBus.register(shopListener);

		log.debug("Resourceman Mode started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		overlayManager.remove(overlay);
		eventBus.unregister(grandExchangeListener);
		eventBus.unregister(tradeListener);
		eventBus.unregister(groundItemListener);
		eventBus.unregister(shopListener);
		resourceTracker.resetSession();
		log.debug("Resourceman Mode stopped!");
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived event)
	{
		for (ItemStack item : event.getItems())
		{
			String name = itemManager.getItemComposition(item.getId()).getName();
			resourceTracker.trackResource(name, item.getQuantity());
		}
		panel.update();
	}

	@Subscribe
	public void onPlayerLootReceived(PlayerLootReceived event)
	{
		for (ItemStack item : event.getItems())
		{
			String name = itemManager.getItemComposition(item.getId()).getName();
			resourceTracker.trackResource(name, item.getQuantity());
		}
		panel.update();
	}

	public void triggerViolation()
	{
		if (config.showChatMessage())
		{
			chatMessageManager.queue(QueuedMessage.builder()
					.type(ChatMessageType.GAMEMESSAGE)
					.runeLiteFormattedMessage("<col=ff0000>" + VIOLATION_MESSAGE + "</col>")
					.build());
		}

		if (config.playSound())
		{
			client.playSoundEffect(VIOLATION_SOUND);
		}
	}

	public void triggerViolationWithTracking(ResourceTracker.ViolationType type)
	{
		resourceTracker.trackViolation(type);
		triggerViolation();
		panel.update();
	}

	@Provides
	ResourcemanConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ResourcemanConfig.class);
	}
}