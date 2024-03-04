package team.reborn.energy.api.base;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * An energy storage that delegates to another energy storage,
 * with an optional boolean supplier to check that the storage is still valid.
 * This can be used for easier item energy storage implementation, or overridden for custom delegation logic.
 */
public class DelegatingEnergyStorage implements EnergyStorage {
	protected final Supplier<EnergyStorage> backingStorage;
	protected final BooleanSupplier validPredicate;

	/**
	 * Create a new instance.
	 * @param backingStorage Storage to delegate to.
	 * @param validPredicate A function that can return false to prevent any operation, or true to call the delegate as usual.
	 *                       {@code null} can be passed if no filtering is necessary.
	 */
	public DelegatingEnergyStorage(EnergyStorage backingStorage, @Nullable BooleanSupplier validPredicate) {
		this(() -> backingStorage, validPredicate);
		Objects.requireNonNull(backingStorage);
	}

	/**
	 * More general constructor that allows the backing storage to change over time.
	 */
	public DelegatingEnergyStorage(Supplier<EnergyStorage> backingStorage, @Nullable BooleanSupplier validPredicate) {
		this.backingStorage = Objects.requireNonNull(backingStorage);
		this.validPredicate = validPredicate == null ? () -> true : validPredicate;
	}

	@Override
	public boolean supportsInsertion() {
		return validPredicate.getAsBoolean() && backingStorage.get().supportsInsertion();
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		if (validPredicate.getAsBoolean()) {
			return backingStorage.get().insert(maxAmount, transaction);
		} else {
			return 0;
		}
	}

	@Override
	public boolean supportsExtraction() {
		return validPredicate.getAsBoolean() && backingStorage.get().supportsExtraction();
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		if (validPredicate.getAsBoolean()) {
			return backingStorage.get().extract(maxAmount, transaction);
		} else {
			return 0;
		}
	}

	@Override
	public long getAmount() {
		if (validPredicate.getAsBoolean()) {
			return backingStorage.get().getAmount();
		} else {
			return 0;
		}
	}

	@Override
	public long getCapacity() {
		if (validPredicate.getAsBoolean()) {
			return backingStorage.get().getCapacity();
		} else {
			return 0;
		}
	}
}
