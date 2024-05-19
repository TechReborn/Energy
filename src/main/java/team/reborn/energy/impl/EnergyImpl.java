package team.reborn.energy.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

@ApiStatus.Internal
public class EnergyImpl {
	public static final DataComponentType<Long> ENERGY_COMPONENT = DataComponentType.<Long>builder()
		.codec(nonNegativeLong())
		.packetCodec(PacketCodecs.VAR_LONG)
		.build();

	public static void init() {
		Registry.register(Registries.DATA_COMPONENT_TYPE, new Identifier("team_reborn_energy", "energy"), ENERGY_COMPONENT);
		EnergyStorage.ITEM.registerFallback((stack, ctx) -> {
			if (stack.getItem() instanceof SimpleEnergyItem energyItem) {
				return SimpleEnergyItem.createStorage(ctx, energyItem.getEnergyCapacity(stack), energyItem.getEnergyMaxInput(stack), energyItem.getEnergyMaxOutput(stack));
			} else {
				return null;
			}
		});
	}

	private static Codec<Long> nonNegativeLong() {
		return Codec.LONG.validate((Long value) -> {
			if (value >= 0) {
				return DataResult.success(value);
			}

			return DataResult.error(() -> "Energy value must be non-negative: " + value);
		});
	}
}
