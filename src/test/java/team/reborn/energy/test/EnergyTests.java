package team.reborn.energy.test;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyItem;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import team.reborn.energy.impl.EnergyImpl;

import static org.junit.jupiter.api.Assertions.*;
import static team.reborn.energy.api.base.SimpleEnergyItem.getStoredEnergyUnchecked;

public class EnergyTests {
	private static TestBatteryItem item;

	@BeforeAll
	public static void setup() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		EnergyImpl.init();

		item = new TestBatteryItem(60, 50, 50);
		Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("energy_test", "battery"), item);
	}

	@Test
	public void testEmptyStorage() {
		try (Transaction transaction = Transaction.openOuter()) {
			ensureEmpty(EnergyStorage.EMPTY, transaction);
		}
	}

	@Test
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

	@Test
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
		EnergyStorage energyStorage = SimpleEnergyItem.createStorage(ctx, 60, 50, 30);

		try (Transaction transaction = Transaction.openOuter()) {
			assertTrue(energyStorage.supportsInsertion());
			assertTrue(energyStorage.supportsExtraction());
			// Insertion of 200 should only insert 100 (50 per item).
			assertEquals(100, energyStorage.insert(200, transaction));
			assertEquals(50, getStoredEnergyUnchecked(slot.variant));
			// Insertion of 200 should only insert 20 (10 per item) due to the capacity.
			assertEquals(20, energyStorage.insert(200, transaction));
			assertEquals(60, getStoredEnergyUnchecked(slot.variant));
			// Extraction of 30 should extract 30 (15 per item).
			assertEquals(30, energyStorage.extract(30, transaction));
			assertEquals(45, getStoredEnergyUnchecked(slot.variant));
			// Check amount and capacity.
			assertEquals(90, energyStorage.getAmount());
			assertEquals(120, energyStorage.getCapacity());

			// Now check that everything returns 0 if we change the item in the slot.
			slot.variant = ItemVariant.blank();
			ensureEmpty(energyStorage, transaction);
		}
	}

	private static void ensureEmpty(EnergyStorage energyStorage, TransactionContext transaction) {
		assertFalse(energyStorage.supportsInsertion());
		assertFalse(energyStorage.supportsExtraction());
		assertEquals(0, energyStorage.insert(Long.MAX_VALUE, transaction));
		assertEquals(0, energyStorage.extract(Long.MAX_VALUE, transaction));
		assertEquals(0, energyStorage.getAmount());
		assertEquals(0, energyStorage.getCapacity());
	}

	@Test
	public void testBatteryItem() {
		ItemStack stack = new ItemStack(item);

		assertEquals(0, item.getStoredEnergy(stack));
		assertTrue(EnergyStorageUtil.isEnergyStorage(stack));

		item.setStoredEnergy(stack, 10);
		assertEquals(10, item.getStoredEnergy(stack));
	}

	@Test
	public void testGetStackEnergy() {
		ItemStack stack = new ItemStack(item);
		ItemVariant variant = ItemVariant.of(stack);

		assertEquals(0L, SimpleEnergyItem.getStoredEnergyUnchecked(stack));
		assertEquals(0L, SimpleEnergyItem.getStoredEnergyUnchecked(variant));
		assertNull(stack.get(EnergyStorage.ENERGY_COMPONENT));
		assertNull(variant.getComponents().get(EnergyStorage.ENERGY_COMPONENT));

		SimpleEnergyItem.setStoredEnergyUnchecked(stack, 1000L);
		variant = ItemVariant.of(stack);

		assertEquals(1000L, SimpleEnergyItem.getStoredEnergyUnchecked(stack));
		assertEquals(1000L, SimpleEnergyItem.getStoredEnergyUnchecked(variant));
		assertNotNull(stack.get(EnergyStorage.ENERGY_COMPONENT));
		assertNotNull(variant.getComponents().get(EnergyStorage.ENERGY_COMPONENT));

		SimpleEnergyItem.setStoredEnergyUnchecked(stack, 0L);
		variant = ItemVariant.of(stack);

		assertEquals(0L, SimpleEnergyItem.getStoredEnergyUnchecked(stack));
		assertEquals(0L, SimpleEnergyItem.getStoredEnergyUnchecked(variant));
		assertNull(stack.get(EnergyStorage.ENERGY_COMPONENT));
		assertNull(variant.getComponents().get(EnergyStorage.ENERGY_COMPONENT));
	}
}
