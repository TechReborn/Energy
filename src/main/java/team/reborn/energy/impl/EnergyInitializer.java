package team.reborn.energy.impl;

import net.fabricmc.api.ModInitializer;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleBatteryItem;
import team.reborn.energy.api.base.SimpleItemEnergyStorage;

public class EnergyInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		EnergyStorage.ITEM.registerFallback((stack, ctx) -> {
			if (stack.getItem() instanceof SimpleBatteryItem battery) {
				return new SimpleItemEnergyStorage(ctx, battery.getEnergyCapacity(), battery.getEnergyMaxInput(), battery.getEnergyMaxOutput());
			} else {
				return null;
			}
		});
	}
}
