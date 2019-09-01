package team.reborn.energy.test;

import team.reborn.energy.EnergyFace;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

public class TestingHolder implements EnergyStorage {

	private double stored;
	private double max;

	EnergyTier tier = null;

	public TestingHolder(double stored, double max) {
		this.stored = stored;
		this.max = max;
	}

	public TestingHolder(double stored, double max, EnergyTier tier) {
		this.stored = stored;
		this.max = max;
		this.tier = tier;
	}

	@Override
	public double getStored(EnergyFace face) {
		return stored;
	}

	@Override
	public void setStored(double amount) {
		stored = amount;
	}

	@Override
	public double getMaxStoredPower() {
		return max;
	}

	@Override
	public EnergyTier getTier() {
		return tier == null ? EnergyTier.HIGH : tier;
	}


	//Only allows power access on the face provided
	public static class Facing extends TestingHolder {

		private final EnergyFace face;

		public Facing(double stored, double max, EnergyFace face) {
			super(stored, max);
			this.face = face;
		}

		public Facing(double stored, double max, EnergyTier tier, EnergyFace face) {
			super(stored, max, tier);
			this.face = face;
		}

		@Override
		public double getStored(EnergyFace face) {
			return this.face == face ? super.getStored(face) : 0;
		}

		@Override
		public double getMaxInput(EnergyFace face) {
			return this.face == face ? super.getMaxInput(face) : 0;
		}

		@Override
		public double getMaxOutput(EnergyFace face) {
			return this.face == face ? super.getMaxOutput(face) : 0;
		}
	}

}
