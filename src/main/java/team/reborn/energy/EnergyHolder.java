package team.reborn.energy;

public interface EnergyHolder {

	/**
	 * @return Returns the maximum amount of energy to be stored
	 */
	double getMaxStoredPower();

	/**
	 * @return the tier of this EnergyStorage
	 */
	EnergyTier getTier();

	default double getMaxInput(EnergySide side) {
		return getTier().getMaxInput();
	}

	default double getMaxOutput(EnergySide side) {
		return getTier().getMaxOutput();
	}

}
