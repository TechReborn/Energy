package team.reborn.energy.impl;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleBatteryItem;

public class EnergyImpl {
	static {
		EnergyStorage.ITEM.registerFallback((stack, ctx) -> {
			if (stack.getItem() instanceof SimpleBatteryItem battery) {
				return SimpleBatteryItem.createStorage(ctx, battery.getEnergyCapacity(), battery.getEnergyMaxInput(), battery.getEnergyMaxOutput());
			} else {
				return null;
			}
		});
	}

	public static final EnergyStorage EMPTY = new EnergyStorage() {
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
	};
}
