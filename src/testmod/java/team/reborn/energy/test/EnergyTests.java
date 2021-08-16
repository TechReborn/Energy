package team.reborn.energy.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import team.reborn.energy.api.base.SimpleItemEnergyStorage;

import static org.junit.Assert.*;
import static team.reborn.energy.api.base.SimpleBatteryItem.ENERGY_KEY;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class EnergyTests implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger("TR Energy Tests");

	@Override
	public void onInitialize() {
		testSimpleEnergyStorage();
		testItemEnergyStorage();
		LOGGER.info("TR Energy tests successful!");
	}

	public void testSimpleEnergyStorage() {
		SimpleEnergyStorage simpleStorage = new SimpleEnergyStorage(100, 5, 10);
		assertEquals(0, simpleStorage.getAmount());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(5, simpleStorage.insert(100, transaction));
			assertEquals(3, simpleStorage.insert(3, transaction));
			assertEquals(8, simpleStorage.getAmount());
		}

		assertEquals(0, simpleStorage.getAmount());

		try (Transaction transaction = Transaction.openOuter()) {
			assertEquals(5, simpleStorage.insert(100, transaction));
			assertEquals(3, simpleStorage.insert(3, transaction));
			assertEquals(8, simpleStorage.getAmount());

			transaction.commit();
		}

		assertEquals(8, simpleStorage.getAmount());
	}

	public void testItemEnergyStorage() {
		SingleVariantStorage<ItemVariant> slot = new SingleVariantStorage<>() {
			@Override
			protected ItemVariant getBlankVariant() {
				return ItemVariant.blank();
			}

			@Override
			protected long getCapacity(ItemVariant variant) {
				return 2;
			}
		};
		ContainerItemContext ctx = ContainerItemContext.ofSingleSlot(slot);

		// Set starting items (diamonds here)
		slot.variant = ItemVariant.of(Items.DIAMOND);
		slot.amount = 2;

		// Create the energy storage
		EnergyStorage energyStorage = new SimpleItemEnergyStorage(ctx, 60, 50, 30);

		try (Transaction transaction = Transaction.openOuter()) {
			assertTrue(energyStorage.supportsInsertion());
			assertTrue(energyStorage.supportsExtraction());
			// Insertion of 200 should only insert 100 (50 per item).
			assertEquals(100, energyStorage.insert(200, transaction));
			assertEquals(50, slot.variant.getNbt().getLong(ENERGY_KEY));
			// Insertion of 200 should only insert 20 (10 per item) due to the capacity.
			assertEquals(20, energyStorage.insert(200, transaction));
			assertEquals(60, slot.variant.getNbt().getLong(ENERGY_KEY));
			// Extraction of 30 should extract 30 (15 per item).
			assertEquals(30, energyStorage.extract(30, transaction));
			assertEquals(45, slot.variant.getNbt().getLong(ENERGY_KEY));
			// Check amount and capacity.
			assertEquals(90, energyStorage.getAmount());
			assertEquals(120, energyStorage.getCapacity());

			// Now check that everything returns 0 if we change the item in the slot.
			slot.variant = ItemVariant.blank();
			assertFalse(energyStorage.supportsInsertion());
			assertFalse(energyStorage.supportsExtraction());
			assertEquals(0, energyStorage.insert(Long.MAX_VALUE, transaction));
			assertEquals(0, energyStorage.extract(Long.MAX_VALUE, transaction));
			assertEquals(0, energyStorage.getAmount());
			assertEquals(0, energyStorage.getCapacity());
		}
	}
}
