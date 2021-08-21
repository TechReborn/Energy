package team.reborn.energy.test;

import net.minecraft.item.Item;
import team.reborn.energy.api.base.SimpleBatteryItem;

public class TestBatteryItem extends Item implements SimpleBatteryItem {
	private final long capacity, maxInput, maxOutput;

	public TestBatteryItem(long capacity, long maxInput, long maxOutput) {
		super(new Item.Settings());
		this.capacity = capacity;
		this.maxInput = maxInput;
		this.maxOutput = maxOutput;
	}

	@Override
	public long getEnergyCapacity() {
		return capacity;
	}

	@Override
	public long getEnergyMaxInput() {
		return maxInput;
	}

	@Override
	public long getEnergyMaxOutput() {
		return maxOutput;
	}
}
