package team.reborn.energy;

import java.util.function.BooleanSupplier;

public class EnergyMovement {

	private final EnergyHandler source;
	private final EnergyHandler target;

	private boolean simulate = false;
	private boolean disabled = false;

	//Not to be used by a mod, use EnergyHandler.into
	EnergyMovement(EnergyHandler source, EnergyHandler target) {
		this.source = source;
		this.target = target;
	}

	public EnergyMovement simulate() {
		simulate = true;
		return this;
	}

	public double move() {
		return this.move(Double.MAX_VALUE);
	}

	public EnergyMovement onlyIf(BooleanSupplier booleanSupplier) {
		if (!booleanSupplier.getAsBoolean()) {
			disabled = true;
		}
		return this;
	}

	public double move(double amount) {
		if (disabled) {
			return 0;
		}
		//TODO ensure both are being simulated
		if (simulate) {
			target.simulate();
			source.simulate();
		}
		return target.insert(source.extract(Math.min(target.getMaxInput(), amount)));
	}

}
