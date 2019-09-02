package team.reborn.energy.test;

import team.reborn.energy.EnergySide;
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
	public double getStored(EnergySide side) {
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


	//Only allows power access on the side provided
	public static class Facing extends TestingHolder {

		private final EnergySide side;

		public Facing(double stored, double max, EnergySide side) {
			super(stored, max);
			this.side = side;
		}

		public Facing(double stored, double max, EnergyTier tier, EnergySide side) {
			super(stored, max, tier);
			this.side = side;
		}

		@Override
		public double getStored(EnergySide side) {
			return this.side == side ? super.getStored(side) : 0;
		}

		@Override
		public double getMaxInput(EnergySide side) {
			return this.side == side ? super.getMaxInput(side) : 0;
		}

		@Override
		public double getMaxOutput(EnergySide side) {
			return this.side == side ? super.getMaxOutput(side) : 0;
		}
	}

}
