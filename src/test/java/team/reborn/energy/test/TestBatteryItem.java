package team.reborn.energy.test;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.base.SimpleEnergyItem;

public class TestBatteryItem extends Item implements SimpleEnergyItem {
	private final long capacity, maxInput, maxOutput;

	public TestBatteryItem(long capacity, long maxInput, long maxOutput) {
		super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("energy_test", "battery"))));
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
