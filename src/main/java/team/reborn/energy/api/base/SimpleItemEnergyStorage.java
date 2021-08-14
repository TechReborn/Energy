package team.reborn.energy.api.base;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import team.reborn.energy.api.EnergyStorage;

/**
 * A base energy storage implementation for items, with fixed capacity, and per-operation insertion and extraction limits.
 * The energy is stored in the {@code energy} tag of the stacks.
 *
 * <p>Stackable energy containers are supported just fine, and they will distribute energy evenly.
 * For example, insertion of 3 units of energy into a stack of 2 items using this class will either insert 0 or 2 depending on the remaining capacity.
 */
@SuppressWarnings({"unused", "deprecation", "UnstableApiUsage"})
public final class SimpleItemEnergyStorage implements EnergyStorage {
	public static final String ENERGY_KEY = "energy";

	private final ContainerItemContext ctx;
	private final Item startingItem;
	private final long capacity;
	private final long maxInsert, maxExtract;

	public SimpleItemEnergyStorage(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
		StoragePreconditions.notNegative(capacity);
		StoragePreconditions.notNegative(maxInsert);
		StoragePreconditions.notNegative(maxExtract);

		this.ctx = ctx;
		this.startingItem = ctx.getItemVariant().getItem();
		this.capacity = capacity;
		this.maxInsert = maxInsert;
		this.maxExtract = maxExtract;
	}

	/**
	 * Used to check if the context still has the starting item. (If it doesn't we can't do anything).
	 */
	private boolean hasStartingItem() {
		return ctx.getItemVariant().isOf(startingItem) && ctx.getAmount() > 0;
	}

	/**
	 * Try to set the energy of the stack to {@code energyAmountPerCount}, return true if success.
	 */
	private boolean trySetEnergy(long energyAmountPerCount, long count, TransactionContext transaction) {
		ItemStack newStack = ctx.getItemVariant().toStack();

		if (energyAmountPerCount == 0) {
			// Make sure newly crafted energy containers stack with emptied ones.
			newStack.removeSubTag(ENERGY_KEY);
		} else {
			newStack.getOrCreateTag().putLong(ENERGY_KEY, energyAmountPerCount);
		}

		ItemVariant newVariant = ItemVariant.of(newStack);

		// Try to convert exactly `count` items.
		try (Transaction nested = transaction.openNested()) {
			if (ctx.extract(ctx.getItemVariant(), count, nested) == count && ctx.insert(newVariant, count, nested) == count) {
				nested.commit();
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean supportsInsertion() {
		return maxInsert > 0 && hasStartingItem();
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		// Make sure the item matches.
		if (!hasStartingItem()) return 0;

		long count = ctx.getAmount();

		long maxAmountPerCount = maxAmount / count;
		long currentAmountPerCount = getAmount() / count;
		long insertedPerCount = Math.min(maxInsert, Math.min(maxAmountPerCount, capacity - currentAmountPerCount));

		if (insertedPerCount > 0) {
			if (trySetEnergy(currentAmountPerCount + insertedPerCount, count, transaction)) {
				return insertedPerCount * count;
			}
		}

		return 0;
	}

	@Override
	public boolean supportsExtraction() {
		return maxExtract > 0 && hasStartingItem();
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		// Make sure the item matches.
		if (!hasStartingItem()) return 0;

		long count = ctx.getAmount();

		long maxAmountPerCount = maxAmount / count;
		long currentAmountPerCount = getAmount() / count;
		long extractedPerCount = Math.min(maxExtract, Math.min(maxAmountPerCount, currentAmountPerCount));

		if (extractedPerCount > 0) {
			if (trySetEnergy(currentAmountPerCount - extractedPerCount, count, transaction)) {
				return extractedPerCount * count;
			}
		}

		return 0;
	}

	@Override
	public long getAmount() {
		if (hasStartingItem()) {
			NbtCompound nbt = ctx.getItemVariant().getNbt();
			return nbt == null ? 0 : nbt.getLong(ENERGY_KEY) * ctx.getAmount();
		} else {
			return 0;
		}
	}

	@Override
	public long getCapacity() {
		if (hasStartingItem()) {
			return capacity * ctx.getAmount();
		} else {
			return 0;
		}
	}
}
