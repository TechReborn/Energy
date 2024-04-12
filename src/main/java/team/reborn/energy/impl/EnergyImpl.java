package team.reborn.energy.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.ApiStatus;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

@ApiStatus.Internal
public class EnergyImpl {
	static {
		EnergyStorage.ITEM.registerFallback((stack, ctx) -> {
			if (stack.getItem() instanceof SimpleEnergyItem energyItem) {
				return SimpleEnergyItem.createStorage(ctx, energyItem.getEnergyCapacity(stack), energyItem.getEnergyMaxInput(stack), energyItem.getEnergyMaxOutput(stack));
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

	public static final DataComponentType<Long> ENERGY_COMPONENT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			new Identifier("team_reborn_energy", "energy"),
			DataComponentType.<Long>builder()
					.codec(nonNegativeLong())
					.packetCodec(PacketCodecs.VAR_LONG)
					.build()
	);

	private static Codec<Long> nonNegativeLong() {
		return Codec.LONG.validate((Long value) -> {
			if (value >= 0) {
				return DataResult.success(value);
			}

			return DataResult.error(() -> "Energy value must be non-negative: " + value);
		});
	}
}
