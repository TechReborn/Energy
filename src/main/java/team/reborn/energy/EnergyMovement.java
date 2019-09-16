package team.reborn.energy;

import java.util.function.BooleanSupplier;

public final class EnergyMovement {

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
		if (simulate || target.isSimulate() || source.isSimulate()) {
			simulate = true;
			target.simulate();
			source.simulate();
		}
		double maxMove = Math.min(target.getMaxInput(), Math.min(source.getMaxOutput(), amount));
		if(maxMove < 0){
			return 0;
		}
		return target.insert(source.extract(maxMove));
	}

}
