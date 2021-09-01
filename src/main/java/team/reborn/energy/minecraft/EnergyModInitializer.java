package team.reborn.energy.minecraft;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

// TODO: remove alongside the rest of the legacy API.
public class EnergyModInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		Energy.registerHolder(object -> {
			if(object instanceof ItemStack){
				return !((ItemStack) object).isEmpty() && ((ItemStack) object).getItem() instanceof EnergyHolder;
			}
			return false;
		}, object -> {
			final ItemStack stack = (ItemStack) object;
			final EnergyHolder energyHolder = (EnergyHolder) stack.getItem();
			return new EnergyStorage() {
				@Override
				public double getStored(EnergySide face) {
					validateNBT();
					return stack.getNbt().getDouble("energy");
				}

				@Override
				public void setStored(double amount) {
					validateNBT();
					stack.getNbt().putDouble("energy", amount);
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
					if (!stack.hasNbt()) {
						stack.setNbt(new NbtCompound());
						stack.getNbt().putInt("energy", 0);
					}
				}
			};
		});
	}
}
