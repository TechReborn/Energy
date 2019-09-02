package team.reborn.energy;

public interface EnergyStorage extends EnergyHolder {

	/**
	 * Returns the currently stored energy
	 */
	double getStored(EnergySide face);

	/**
	 * Sets the stored energy to the provided amount
	 *
	 * @param amount the amount of energy to set
	 */
	void setStored(double amount);

}
