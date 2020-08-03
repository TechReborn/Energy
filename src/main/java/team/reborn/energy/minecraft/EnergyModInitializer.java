package team.reborn.energy.minecraft;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import team.reborn.energy.*;

public class EnergyModInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		Energy.registerHolder(ItemStack.class, -10, stack -> {
			return !stack.isEmpty() && stack.getItem() instanceof EnergyHolder;
		}, stack -> {
			final EnergyHolder energyHolder = (EnergyHolder) stack.getItem();
			return new EnergyStorage() {
				@Override
				public double getStored(EnergySide face) {
					validateNBT();
					return stack.getTag().getDouble("energy");
				}

				@Override
				public void setStored(double amount) {
					validateNBT();
					stack.getTag().putDouble("energy", amount);
				}

				@Override
				public double getMaxStoredPower() {
					return energyHolder.getMaxStoredPower();
				}

				@Override
				public EnergyTier getTier() {
					return energyHolder.getTier();
				}

				private void validateNBT() {
					if (!stack.hasTag()) {
						stack.setTag(new CompoundTag());
						stack.getTag().putInt("energy", 0);
					}
				}
			};
		});
	}
}
