package team.reborn.energy;

public interface EnergyStorage {

	/**
	 * Returns the currently stored energy
	 */
	double getStored(EnergyFace face);

	/**
	 * Sets the stored energy to the provided amount
	 *
	 * @param amount the amount of energy to set
	 */
	void setStored(double amount);

	/**
	 * @return Returns the maximum amount of energy to be stored
	 */
	double getMaxStoredPower();

	/**
	 * @return the tier of this EnergyStorage
	 */
	EnergyTier getTier();

	default double getMaxInput(EnergyFace face) {
		return getTier().getMaxInput();
	}

	default double getMaxOutput(EnergyFace face) {
		return getTier().getMaxOutput();
	}

}
