package team.reborn.energy.api;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

/**
 * Helper functions to work with {@link EnergyStorage}s.
 */
@SuppressWarnings({"unused", "deprecation", "UnstableApiUsage"})
public class EnergyStorageUtil {
	/**
	 * Move energy between two energy storages, and return the amount that was successfully moved.
	 *
	 * @param from The source storage. May be null.
	 * @param to The target storage. May be null.
	 * @param maxAmount The maximum amount that may be moved.
	 * @param transaction The transaction this transfer is part of,
	 *                    or {@code null} if a transaction should be opened just for this transfer.
	 * @return The amount of energy that was successfully moved.
	 */
	public static long move(@Nullable EnergyStorage from, @Nullable EnergyStorage to, long maxAmount, @Nullable TransactionContext transaction) {
		if (from == null || to == null) return 0;

		StoragePreconditions.notNegative(maxAmount);

		// Simulate extraction first.
		long maxExtracted;

		try (Transaction extractionTestTransaction = openTransaction(transaction)) {
			maxExtracted = from.extract(maxAmount, extractionTestTransaction);
		}

		try (Transaction moveTransaction = openTransaction(transaction)) {
			// Then insert what can be extracted.
			long accepted = to.insert(maxExtracted, moveTransaction);

			// Extract for real.
			if (from.extract(accepted, transaction) == accepted) {
				// Commit if the amounts match.
				moveTransaction.commit();
				return accepted;
			}
		}

		return 0;
	}

	// TODO: should perhaps be added in the transfer api? Transaction.openNested(@Nullable TransactionContext) ?
	private static Transaction openTransaction(@Nullable TransactionContext maybeParent) {
		return maybeParent == null ? Transaction.openOuter() : maybeParent.openNested();
	}

	private EnergyStorageUtil() {
	}
}
