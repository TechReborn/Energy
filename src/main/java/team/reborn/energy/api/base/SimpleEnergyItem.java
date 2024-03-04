package team.reborn.energy.api.base;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.impl.SimpleItemEnergyStorageImpl;

import java.util.Optional;

/**
 * Simple battery-like energy containing item. If this is implemented on an item:
 * <ul>
 *     <li>The energy will directly be stored in the components.</li>
 *     <li>Helper functions in this class to work with the stored energy can be used.</li>
 *     <li>An EnergyStorage will automatically be provided for queries through {@link EnergyStorage#ITEM}.</li>
 * </ul>
 */
// TODO: Consider adding a tooltip and a recipe input -> output energy transfer handler like RC has.
public interface SimpleEnergyItem {

	/**
	 * Return a base energy storage implementation for items, with fixed capacity, and per-operation insertion and extraction limits.
	 * This is used internally for items that implement SimpleEnergyItem, but it may also be used outside of that.
	 * The energy is stored in the {@link EnergyStorage#ENERGY_COMPONENT} of the stacks.
	 *
	 * <p>Stackable energy containers are supported just fine, and they will distribute energy evenly.
	 * For example, insertion of 3 units of energy into a stack of 2 items using this class will either insert 0 or 2 depending on the remaining capacity.
	 */
	static EnergyStorage createStorage(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
		return SimpleItemEnergyStorageImpl.createSimpleStorage(ctx, capacity, maxInsert, maxExtract);
	}

	/**
	 * @param stack Current stack.
	 * @return The max energy that can be stored in this item stack (ignoring current stack size).
	 */
	long getEnergyCapacity(ItemStack stack);

	/**
	 * @param stack Current stack.
	 * @return The max amount of energy that can be inserted in this item stack (ignoring current stack size) in a single operation.
	 */
	long getEnergyMaxInput(ItemStack stack);

	/**
	 * @param stack Current stack.
	 * @return The max amount of energy that can be extracted from this item stack (ignoring current stack size) in a single operation.
	 */
	long getEnergyMaxOutput(ItemStack stack);

	/**
	 * @return The energy stored in the stack. Count is ignored.
	 */
	default long getStoredEnergy(ItemStack stack) {
		return getStoredEnergyUnchecked(stack);
	}

	/**
	 * Directly set the energy stored in the stack. Count is ignored.
	 * It's up to callers to ensure that the new amount is >= 0 and <= capacity.
	 */
	default void setStoredEnergy(ItemStack stack, long newAmount) {
		setStoredEnergyUnchecked(stack, newAmount);
	}

	/**
	 * Try to use exactly {@code amount} energy if there is enough available and return true if successful,
	 * otherwise do nothing and return false.
	 * @throws IllegalArgumentException If the count of the stack is not exactly 1!
	 */
	default boolean tryUseEnergy(ItemStack stack, long amount) {
		if (stack.getCount() != 1) {
			throw new IllegalArgumentException("Invalid count: " + stack.getCount());
		}

		long newAmount = getStoredEnergy(stack) - amount;

		if (newAmount < 0) {
			return false;
		} else {
			setStoredEnergy(stack, newAmount);
			return true;
		}
	}

	/**
	 * @return The currently stored energy, ignoring the count and without checking the current item.
	 */
	static long getStoredEnergyUnchecked(ItemStack stack) {
		return stack.getOrDefault(EnergyStorage.ENERGY_COMPONENT, 0L);
	}

	static long getStoredEnergyUnchecked(ItemVariant variant) {
		return getStoredEnergyUnchecked(variant.getComponents());
	}

	static long getStoredEnergyUnchecked(ComponentChanges components) {
		@Nullable Optional<Long> energy = (Optional<Long>) components.get(EnergyStorage.ENERGY_COMPONENT);

		//noinspection OptionalAssignedToNull
		if (energy != null) {
			return energy.orElse(0L);
		}

		return 0;
	}

	/**
	 * Set the energy, ignoring the count and without checking the current item.
	 */
	static void setStoredEnergyUnchecked(ItemStack stack, long newAmount) {
		if (newAmount <= 0) {
			// Make sure newly crafted energy containers stack with emptied ones.
			stack.remove(EnergyStorage.ENERGY_COMPONENT);
		} else {
			stack.set(EnergyStorage.ENERGY_COMPONENT, newAmount);
		}
	}
}
