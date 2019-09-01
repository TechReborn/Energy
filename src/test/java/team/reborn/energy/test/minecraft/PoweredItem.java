package team.reborn.energy.test.minecraft;

import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;

public class PoweredItem extends Item implements EnergyHolder {

	@Override
	public double getMaxStoredPower() {
		return 1000;
	}

	@Override
	public EnergyTier getTier() {
		return EnergyTier.LOW;
	}
}
