package com.example;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class Resourceman
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ResourcemanPlugin.class);
		RuneLite.main(args);
	}
}