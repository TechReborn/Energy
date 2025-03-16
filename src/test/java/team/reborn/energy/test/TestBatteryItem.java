package team.reborn.energy.test;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.base.SimpleEnergyItem;

public class TestBatteryItem extends Item implements SimpleEnergyItem {
	private final long capacity, maxInput, maxOutput;

	public TestBatteryItem(long capacity, long maxInput, long maxOutput) {
		super(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("energy_test", "battery"))));
		this.capacity = capacity;
		this.maxInput = maxInput;
		this.maxOutput = maxOutput;
	}

	@Override
	public long getEnergyCapacity(ItemStack stack) {
		return capacity;
	}

	@Override
	public long getEnergyMaxInput(ItemStack stack) {
		return maxInput;
	}

	@Override
	public long getEnergyMaxOutput(ItemStack stack) {
		return maxOutput;
	}
}
