package team.reborn.energy;

public class EnergyHandler {

	private final EnergyStorage holder;

	private boolean simulate = false;
	private EnergySide side = EnergySide.UNKNOWN;

	//Not to be called by a mod, use Energy.of
	EnergyHandler(EnergyStorage holder) {
		this.holder = holder;
	}

	public EnergyHandler simulate() {
		simulate = true;
		return this;
	}

	/**
	 * @param amount the amount of energy to try and extract
	 * @return the amount of energy actually extracted
	 */
	public double extract(double amount) {
		double stored = holder.getStored(side);
		double energyExtracted = Math.min(Math.min(stored, amount), holder.getMaxOutput(side));
		if (energyExtracted > 0 && !simulate) {
			holder.setStored(stored - energyExtracted);
		}
		return energyExtracted;
	}

	public double insert(double amount) {
		double stored = holder.getStored(side);
		double energyInserted = Math.min(Math.min(holder.getMaxStoredPower() - stored, amount), holder.getMaxInput(side));
		if (!simulate) {
			holder.setStored(stored + energyInserted);
		}
		return energyInserted;
	}

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

	public double getEnergy() {
		return holder.getStored(side);
	}

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
}
