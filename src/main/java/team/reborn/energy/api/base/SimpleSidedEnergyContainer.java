package team.reborn.energy.api.base;

/**
 * A base energy storage implementation with a dynamic capacity, and per-side per-operation insertion and extraction limits.
 * {@link #getSideStorage} can be used to get an {@code EnergyStorage} implementation for a given side.
 * Make sure to override {@link #onFinalCommit} to call {@code markDirty} and similar functions.
 *
 * <p>Use {@link SidedEnergyContainer} for more customization on how the amount is stored.
 */
@SuppressWarnings({"unused", "UnstableApiUsage"})
public abstract class SimpleSidedEnergyContainer extends SidedEnergyContainer {
	public long amount = 0;

	@Override
	public long getAmount() {
		return amount;
	}

	@Override
	public void setAmount(long amount) {
		this.amount = amount;
	}
}
