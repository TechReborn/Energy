package team.reborn.energy;

public final class EnergyHandler {

	private final EnergyStorage holder;

	private boolean simulate = false;
	private EnergySide side = EnergySide.UNKNOWN;

	//Not to be called by a mod, use Energy.of
	EnergyHandler(EnergyStorage holder) {
		this.holder = holder;
	}

	/**
	 * Once called all future energy transactions will only be simulated. Returning the same value but without making the changes to the amount of energy in the holder.
	 * @return an EnergyHandler instance
	 */
	public EnergyHandler simulate() {
		simulate = true;
		return this;
	}

	//Internal, used by EnergyMovement
	boolean isSimulate(){
		return simulate;
	}

	/**
	 *
	 * Extract upto the amount of energy provided, returns the amount of energy that was extracted.
	 * This is limited by the max output of the holder, as well as the amount of energy that is held in the holder
	 *
	 * @param amount the maximum amount of energy to try to extract.
	 * @return the amount of energy actually extracted from the holder
	 */
	public double extract(double amount) {
		double stored = holder.getStored(side);
		double energyExtracted = Math.min(Math.min(stored, amount), holder.getMaxOutput(side));
		if (energyExtracted > 0 && !simulate) {
			holder.setStored(stored - energyExtracted);
		}
		return energyExtracted;
	}

	/**
	 *
	 *
	 *
	 * @param amount the max amount of energy to try and insert into the holder
	 * @return the actual amount of energy inserted into the holder
	 */
	public double insert(double amount) {
		double stored = holder.getStored(side);
		double energyInserted = Math.min(Math.min(holder.getMaxStoredPower() - stored, amount), holder.getMaxInput(side));
		if (!simulate) {
			holder.setStored(stored + energyInserted);
		}
		return energyInserted;
	}

	/**
	 *
	 * Sets the current amount of energy stored in the holder, it will now allow negative values, and will limit values greater than the maximum stored amount
	 *
	 * @param amount the amount of energy to set in the holder
	 */
	public void set(double amount){
		if(amount > holder.getMaxStoredPower()){
			amount = holder.getMaxStoredPower();
		}
		if(amount < 0){
			amount = 0;
		}
		if (!simulate) {
			holder.setStored(amount);
		}
	}

	//Returns the max amount of energy that can be inputted
	public double getMaxInput() {
		return Math.min(holder.getMaxInput(side), holder.getMaxStoredPower() - holder.getStored(side));
	}

	public double getMaxOutput() {
		return Math.min(holder.getMaxOutput(side), holder.getStored(side));
	}

	/**
	 *
	 * Used to get the current amount of energy in the holder.
	 *
	 * @return the current amount of energy in the holder
	 *
	 */
	public double getEnergy() {
		return holder.getStored(side);
	}

	/**
	 *
	 * Used to get the maximum amount of energy that the holder is allowed to hold.
	 *
	 * @return the maximum amount of energy that the holder can hold
	 */
	public double getMaxStored() {
		return holder.getMaxStoredPower();
	}

	public EnergyMovement into(EnergyHandler target) {
		return new EnergyMovement(this, target);
	}

	public EnergyHandler side(EnergySide side) {
		this.side = side;
		return this;
	}

	public boolean use(double amount){
		if(getEnergy() >= amount){
			if(!simulate){
				set(getEnergy() - amount);
			}
			return true;
		}
		return false;
	}

}
