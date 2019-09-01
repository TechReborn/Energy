package team.reborn.energy;

public enum EnergyTier {
	MICRO(8, 8),
	LOW(32, 32),
	MEDIUM(128, 128),
	HIGH(512, 512),
	EXTREME(2048, 2048),
	INSANE(8192, 8192),
	INFINITE(Integer.MAX_VALUE, Integer.MAX_VALUE);

	private final int maxInput;
	private final int maxOutput;

	EnergyTier(int maxInput, int maxOutput) {
		this.maxInput = maxInput;
		this.maxOutput = maxOutput;
	}

	public int getMaxInput() {
		return maxInput;
	}

	public int getMaxOutput() {
		return maxOutput;
	}

}
