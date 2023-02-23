package team.reborn.energy.api;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.DelegatingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;
import team.reborn.energy.impl.EnergyImpl;
import team.reborn.energy.impl.SimpleItemEnergyStorageImpl;

/**
 * An object that can store energy.
 *
 * <p><ul>
 *     <li>{@link #supportsInsertion} and {@link #supportsExtraction} can be used to tell if insertion and extraction
 *     functionality are possibly supported by this storage.</li>
 *     <li>{@link #insert} and {@link #extract} can be used to insert or extract resources from this storage.</li>
 *     <li>{@link #getAmount} and {@link #getCapacity} can be used to query the current amount and capacity of this storage.
 *     There is no guarantee that the current amount of energy can be extracted,
 *     nor that something can be inserted if capacity > amount.
 *     If you want to know, you can simulate the operation with {@link #insert} and {@link #extract}.
 *     </li>
 * </ul>
 *
 * @see Transaction
 */
@SuppressWarnings({"unused", "deprecation", "UnstableApiUsage"})
public interface EnergyStorage {
	/**
	 * Sided block access to energy storages.
	 * The {@code Direction} parameter may be null, meaning that the full storage (ignoring side restrictions) should be queried.
	 * Refer to {@link BlockApiLookup} for documentation on how to use this field.
	 *
	 * <p>The system is push based. That means that power sources are responsible for pushing power to nearby machines.
	 * Machines and wires should NOT pull power from other sources.
	 *
	 * <p>{@link SimpleEnergyStorage} and {@link SimpleSidedEnergyContainer} are provided as base implementations.
	 *
	 * <p>When the operations supported by an energy storage change,
	 * that is if the return value of {@link EnergyStorage#supportsInsertion} or {@link EnergyStorage#supportsExtraction} changes,
	 * the storage should notify its neighbors with a block update so that they can refresh their connections if necessary.
	 *
	 * <p>This may be queried safely both on the logical server and on the logical client threads.
	 * On the server thread (i.e. with a server world), all transfer functionality is always supported.
	 * On the client thread (i.e. with a client world), contents of queried EnergyStorages are unreliable and should not be modified.
	 */
	BlockApiLookup<EnergyStorage, @Nullable Direction> SIDED =
			BlockApiLookup.get(new Identifier("teamreborn:sided_energy"), EnergyStorage.class, Direction.class);

	/**
	 * Item access to energy storages.
	 * Querying should always happen through {@link ContainerItemContext#find}.
	 *
	 * <p>{@link SimpleItemEnergyStorageImpl} is provided as an implementation example.
	 * Instances of it can be optained through {@link SimpleEnergyItem#createStorage}.
	 * Custom implementations should treat the context as a wrapper around a single slot,
	 * and always check the current item variant and amount before any operation, like {@code SimpleItemEnergyStorageImpl} does it.
	 * The check can be handled by {@link DelegatingEnergyStorage}.
	 *
	 * <p>This may be queried both client-side and server-side.
	 * Returned APIs should behave the same regardless of the logical side.
	 */
	ItemApiLookup<EnergyStorage, ContainerItemContext> ITEM =
			ItemApiLookup.get(new Identifier("teamreborn:energy"), EnergyStorage.class, ContainerItemContext.class);

	/**
	 * Always empty energy storage.
	 */
	EnergyStorage EMPTY = EnergyImpl.EMPTY;

	/**
	 * Return false if calling {@link #insert} will absolutely always return 0, or true otherwise or in doubt.
	 *
	 * <p>Note: This function is meant to be used by cables or other devices that can transfer energy to know if
	 * they should interact with this storage at all.
	 */
	default boolean supportsInsertion() {
		return true;
	}

	/**
	 * Try to insert up to some amount of energy into this storage.
	 *
	 * @param maxAmount The maximum amount of energy to insert. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was inserted.
	 */
	long insert(long maxAmount, TransactionContext transaction);

	/**
	 * Return false if calling {@link #extract} will absolutely always return 0, or true otherwise or in doubt.
	 *
	 * <p>Note: This function is meant to be used by cables or other devices that can transfer energy to know if
	 * they should interact with this storage at all.
	 */
	default boolean supportsExtraction() {
		return true;
	}

	/**
	 * Try to extract up to some amount of energy from this storage.
	 *
	 * @param maxAmount The maximum amount of energy to extract. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was extracted.
	 */
	long extract(long maxAmount, TransactionContext transaction);

	/**
	 * Return the current amount of energy that is stored.
	 */
	long getAmount();

	/**
	 * Return the maximum amount of energy that could be stored.
	 */
	long getCapacity();
}
