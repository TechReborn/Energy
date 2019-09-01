package team.reborn.energy;

public class EnergyHandler {

	private final EnergyStorage holder;

	private boolean simulate = false;
	private EnergyFace face = EnergyFace.UNKNOWN;

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
		double stored = holder.getStored(face);
		double energyExtracted = Math.min(Math.min(stored, amount), holder.getMaxOutput(face));
		if (energyExtracted > 0 && !simulate) {
			holder.setStored(stored - energyExtracted);
		}
		return energyExtracted;
	}

	public double insert(double amount) {
		double stored = holder.getStored(face);
		double energyInserted = Math.min(Math.min(holder.getMaxStoredPower() - stored, amount), holder.getMaxInput(face));
		if (!simulate) {
			holder.setStored(stored + energyInserted);
		}
		return energyInserted;
	}

	//Returns the max amount of energy that can be inputted
	public double getMaxInput() {
		return Math.min(holder.getMaxInput(face), holder.getMaxStoredPower() - holder.getStored(face));
	}

	public double getMaxOutput() {
		return Math.min(holder.getMaxOutput(face), holder.getStored(face));
	}

	public double getEnergy() {
		return holder.getStored(face);
	}

	public EnergyMovement into(EnergyHandler target) {
		return new EnergyMovement(this, target);
	}

	public EnergyHandler face(EnergyFace face) {
		this.face = face;
		return this;
	}
}
