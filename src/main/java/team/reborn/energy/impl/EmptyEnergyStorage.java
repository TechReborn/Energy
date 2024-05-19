package team.reborn.energy.impl;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.ApiStatus;
import team.reborn.energy.api.EnergyStorage;

@ApiStatus.Internal
public final class EmptyEnergyStorage implements EnergyStorage {
	public static final EnergyStorage EMPTY = new EmptyEnergyStorage();

	private EmptyEnergyStorage() {
	}

	@Override
	public boolean supportsInsertion() {
		return false;
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public boolean supportsExtraction() {
		return false;
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public long getAmount() {
		return 0;
	}

	@Override
	public long getCapacity() {
		return 0;
	}
}
