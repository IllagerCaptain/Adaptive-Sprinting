package com.adaptivesprinting;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import org.slf4j.Logger;

public class AdaptiveSprinting implements ClientModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();

	private static boolean shouldAlwaysSprint = false;

	public static boolean shouldAlwaysSprint() {
		return shouldAlwaysSprint;
	}

	@Override
	public void onInitializeClient() {
		HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
		HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, packet -> {
			LOGGER.info("Current ServerType: " + packet.getServerType().get().getName());
			if (packet.getServerType().isEmpty()) { // this basically will only happen if we're not on hypixel
				shouldAlwaysSprint = false;
			} else {
				switch (packet.getServerType().get().getName()) {
					case "WALLS":          // The Walls
					case "SURVIVAL_GAMES": // Blitz Survival Games
					case "WALLS3":     // Mega Walls
					case "UHC":            // UHC Champions
					case "HOUSING":            // UHC Champions
					case "SMP":            // SMP.. duh
						shouldAlwaysSprint = false;
						break;
					default:
						shouldAlwaysSprint = true;
						break;
				}
			}
		});
	}
}