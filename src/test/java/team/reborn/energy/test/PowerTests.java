package team.reborn.energy.test;

import org.junit.Test;
import team.reborn.energy.*;
import team.reborn.energy.test.minecraft.Item;
import team.reborn.energy.test.minecraft.ItemStack;
import team.reborn.energy.test.minecraft.PoweredItem;

import static org.junit.Assert.*;

public class PowerTests {

	@Test
	public void basicSet() {
		TestingHolder energyHolder = new TestingHolder(0, 1000, EnergyTier.LOW);

		Energy.valid(energyHolder);

		Energy.of(energyHolder).set(100);

		assertEquals(100,
		             Energy.of(energyHolder).getEnergy()
			, 0);

		Energy.of(energyHolder).simulate().set(250);

		assertEquals(100,
		             Energy.of(energyHolder).getEnergy()
			, 0);

		Energy.of(energyHolder).set(9999);

		assertEquals(1000,
		             Energy.of(energyHolder).getEnergy()
			, 0);

		Energy.of(energyHolder).set(-10);

		assertEquals(0,
		             Energy.of(energyHolder).getEnergy()
			, 0);

		assertEquals(1000,
		             Energy.of(energyHolder).getMaxStored()
			, 0);
	}

	@Test
	public void basicExtract() {
		//Start with 10 power
		TestingHolder energyHolder = new TestingHolder(10, 1000, EnergyTier.LOW);

		//Ensure there is 10 energy in the holder
		assertEquals(10,
		             Energy.of(energyHolder).getEnergy()
					, 0);

		//Remove 5
		assertEquals(5,
		             Energy.of(energyHolder).extract(5)
			, 0);

		//Check there is now 5 in there
		assertEquals(5,
		             Energy.of(energyHolder).getEnergy()
			, 0);

		//Try to simulate removing 20, but only remove 5
		assertEquals(5,
		             Energy.of(energyHolder)
			             .simulate()
			             .extract(10)
			, 0);

		//Check there is still 5 energy left as the last operation was a simulated one
		assertEquals(5,
		             Energy.of(energyHolder).getEnergy()
			, 0);

		//Try to remove 20, but only remove 5
		assertEquals(5,
		             Energy.of(energyHolder).extract(10)
			, 0);

		//Should now be 0
		assertEquals(0,
		             Energy.of(energyHolder).getEnergy()
			, 0);
	}

	@Test
	public void basicInsert() {
		//Start with 25 power
		TestingHolder energyHolder = new TestingHolder(25, 1000, EnergyTier.EXTREME);


		//Insert 950 power
		assertEquals(950,
		             Energy.of(energyHolder)
			             .insert(950)
			, 0);

		//Check there is now 975 in there
		assertEquals(975,
		             Energy.of(energyHolder)
			             .getEnergy()
			, 0);

		//Simulate adding 50, but only add 25
		assertEquals(25,
		             Energy.of(energyHolder)
			             .simulate()
			             .insert(50)
			, 0);

		//Check there is still 975 in there after the simulate
		assertEquals(975,
		             Energy.of(energyHolder)
			             .getEnergy()
			, 0);

		//Try to add 50, but only add 25
		assertEquals(25,
		             Energy.of(energyHolder)
			             .insert(50)
			, 0);

		//Should now be 1000
		assertEquals(1000,
		             Energy.of(energyHolder)
			             .getEnergy()
			, 0);
	}

	@Test
	public void testMaxIO() {
		//Limit the max IO to 32 with the low energy tier
		TestingHolder holder = new TestingHolder(500, 1000, EnergyTier.LOW);

		//Test removing 50 energy, but ensure only 32 is removed
		assertEquals(32,
		             Energy.of(holder)
			             .extract(50)
			, 0);

		//Test removing 30 energy, and ensure only 30 is removed
		assertEquals(30,
		             Energy.of(holder)
			             .extract(30)
			, 0);

		//Test inserting 50 energy, but ensure only 32 is inserted
		assertEquals(32,
		             Energy.of(holder)
			             .insert(50)
			, 0);

		//Test inserting 30 energy, and ensure only 30 is inserted
		assertEquals(30,
		             Energy.of(holder)
			             .insert(30)
			, 0);

	}

	@Test
	public void testMoving() {
		TestingHolder source = new TestingHolder(500, 1000, EnergyTier.HIGH);
		TestingHolder target = new TestingHolder(0, 1000, EnergyTier.HIGH);

		//Move 100 energy into the target
		Energy.of(source)
			.into(Energy.of(target))
			.move(100);

		assertEquals(400, Energy.of(source).getEnergy(), 0);
		assertEquals(100, Energy.of(target).getEnergy(), 0);

		//Test simulating of energy movement
		double transfered = Energy.of(source)
			.into(Energy.of(target))
			.simulate()
			.move();

		//Ensure no energy was moved
		assertEquals(400, Energy.of(source).getEnergy(), 0);
		assertEquals(100, Energy.of(target).getEnergy(), 0);
		//Ensure the simulated amount of energy transfer is correct
		assertEquals(400, transfered, 0);

		//Move as much energy as possible into the target
		Energy.of(source)
			.into(Energy.of(target))
			.move();

		assertEquals(0, Energy.of(source).getEnergy(), 0);
		assertEquals(500, Energy.of(target).getEnergy(), 0);

		//Testing that onlyIf doesnt move any energy
		Energy.of(target)
			.into(Energy.of(source))
			.onlyIf(() -> false)
			.move();

		assertEquals(0, Energy.of(source).getEnergy(), 0);
		assertEquals(500, Energy.of(target).getEnergy(), 0);

		//Testing that onlyIf doesnt move any energy
		Energy.of(target)
			.into(Energy.of(source))
			.onlyIf(() -> true)
			.move();

		assertEquals(500, Energy.of(source).getEnergy(), 0);
		assertEquals(0, Energy.of(target).getEnergy(), 0);

	}

	@Test
	public void testHolders(){
		//This class represents an object (player or world for example) that can have power attached to it
		class DummyClass {}

		Energy.registerHolder(DummyClass.class, dummyClass -> new EnergyStorage() {

			@Override
			public double getStored(EnergySide side) {
				return 100;
			}

			@Override
			public void setStored(double amount) {
				dummyClass.getClass(); //You can easilt access the class that is provided
			}

			@Override
			public double getMaxStoredPower() {
				return 0;
			}

			@Override
			public EnergyTier getTier() {
				return EnergyTier.LOW;
			}
		});

		DummyClass dummy = new DummyClass();
		assertEquals(100, Energy.of(dummy).getEnergy(), 0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testNoHolder(){
		//This crashes as there is no registered holder for this class
		Energy.of(this);
	}

	@Test
	public void testSidedness() {
		//Source can only be accessed from the top
		TestingHolder source = new TestingHolder.Facing(500, 1000, EnergyTier.HIGH, EnergySide.UP);
		//target can only be accessed form the bottom
		TestingHolder target = new TestingHolder.Facing(0, 1000, EnergyTier.HIGH, EnergySide.DOWN);

		//No side provided, due to the impl of the Holder, this should return 0
		assertEquals(0, Energy.of(source).getEnergy(), 0);
		//Same test as before, just from the top
		assertEquals(500, Energy.of(source).side(EnergySide.UP).getEnergy(), 0);

		//Try an extract 100 energy out of the unknown side, should fail
		assertEquals(0, Energy.of(source).extract(100), 0);
		//Ensure it failed by checking we still have 500 energy
		assertEquals(500, Energy.of(source).side(EnergySide.UP).getEnergy(), 0);

		//Remove 100 energy from the allowed top side
		assertEquals(100, Energy.of(source).side(EnergySide.UP).extract(100), 0);
		//Test there is now only 400 energy in the the source
		assertEquals(400, Energy.of(source).side(EnergySide.UP).getEnergy(), 0);


		double moved = Energy.of(source)
			.side(EnergySide.UP)
			.into(Energy.of(target)) //This should fail because no side is set on the target holder
			.move(100);

		assertEquals(0, moved, 0);

		assertEquals(0, Energy.of(target).side(EnergySide.UP).getEnergy(), 0);
		assertEquals(400, Energy.of(source).side(EnergySide.UP).getEnergy(), 0);

		moved = Energy.of(source)
			.side(EnergySide.UP)
			.into(Energy.of(target).side(EnergySide.DOWN)) //This should now work as the target can only be accessed from the bottom
			.move(100);

		assertEquals(100, moved, 0);
		assertEquals(300, Energy.of(source).side(EnergySide.UP).getEnergy(), 0);
		assertEquals(100, Energy.of(target).side(EnergySide.DOWN).getEnergy(), 0);
	}


	//This test is setup using the dummy mc classes, its a more real world example of how things would work
	@Test
	public void minecraftTests() {

		Item poweredItem = new PoweredItem();
		ItemStack itemStack = new ItemStack(poweredItem);

		Energy.registerHolder(object -> {
			if(object instanceof ItemStack){
				return ((ItemStack) object).getItem() instanceof EnergyHolder;
			}
			return false;
		}, is -> {
			final EnergyHolder energyHolder = (EnergyHolder) ((ItemStack) is).getItem();
			return new EnergyStorage() {
				@Override
				public double getStored(EnergySide side) {
					return 100; //TODO read from NBT here.
				}

				@Override
				public void setStored(double amount) {
					//TODO this is where you would write to nbt
				}

				@Override
				public double getMaxStoredPower() {
					return energyHolder.getMaxStoredPower();
				}

				@Override
				public EnergyTier getTier() {
					return energyHolder.getTier();
				}
			};
		});

		assertTrue(Energy.valid(itemStack));

		assertEquals(100, Energy.of(itemStack).getEnergy(), 0);

		assertEquals(EnergySide.DOWN, EnergySide.fromMinecraft(EnergySide.DOWN));
		assertEquals(EnergySide.UNKNOWN, EnergySide.fromMinecraft(null));

	}
	
	@Test
	public void holderPriorityTests() {
		class SpecialHolder implements EnergyStorage {
			private double stored = 50;
			@Override
			public double getMaxStoredPower() {
				return 1000;
			}

			@Override
			public EnergyTier getTier() {
				return EnergyTier.EXTREME;
			}

			@Override
			public double getStored(EnergySide face) {
				return stored;
			}

			@Override
			public void setStored(double amount) {
				this.stored = amount;
			}
		}
		
		class SpecialHolderResizer implements EnergyStorage {
			private final double scale;
			private final SpecialHolder holder;

			public SpecialHolderResizer(double scale, SpecialHolder holder) {
				this.scale = scale;
				this.holder = holder;
			}

			@Override
			public double getStored(EnergySide face) {
				return holder.getStored(face) * scale;
			}

			@Override
			public void setStored(double amount) {
				holder.setStored(amount / scale);
			}

			@Override
			public double getMaxStoredPower() {
				return holder.getMaxStoredPower() * scale;
			}

			@Override
			public EnergyTier getTier() {
				return holder.getTier();
			}
		}

		SpecialHolder specialHolder = new SpecialHolder();
		
		assertEquals(50, Energy.of(specialHolder).getEnergy(), 0);
		assertEquals(1000, Energy.of(specialHolder).getMaxStored(), 0);

		Energy.registerHolder(SpecialHolder.class, 0, holder -> {
			return new SpecialHolderResizer(100, holder);
		});

		assertEquals(5000, Energy.of(specialHolder).getEnergy(), 0);
		assertEquals(100000, Energy.of(specialHolder).getMaxStored(), 0);

		Energy.registerHolder(SpecialHolder.class, 10, holder -> {
			return new SpecialHolderResizer(10000, holder);
		});

		assertEquals(500000, Energy.of(specialHolder).getEnergy(), 0);
		assertEquals(10000000, Energy.of(specialHolder).getMaxStored(), 0);

		Energy.registerHolder(SpecialHolder.class, -7, holder -> {
			return new SpecialHolderResizer(2, holder);
		});

		assertEquals(500000, Energy.of(specialHolder).getEnergy(), 0);
		assertEquals(10000000, Energy.of(specialHolder).getMaxStored(), 0);

		Energy.registerHolder(SpecialHolder.class, 10, holder -> {
			return new SpecialHolderResizer(0, holder);
		});

		assertEquals(0, Energy.of(specialHolder).getEnergy(), 0);
		assertEquals(0, Energy.of(specialHolder).getMaxStored(), 0);
	}

	@Test
	public void morePowerTests() {
		TestingHolder sourceHolder = new TestingHolder(0, Double.MAX_VALUE, EnergyTier.INFINITE);
		sourceHolder.setStored(Double.MAX_VALUE);

		TestingHolder targetHolder = new TestingHolder(0, 1000, EnergyTier.LOW);

		Energy.of(sourceHolder).into(Energy.of(targetHolder)).move();
		assertEquals(32, Energy.of(targetHolder).getEnergy(), 0);

		Energy.of(sourceHolder).into(Energy.of(targetHolder)).move();
		assertEquals(64, Energy.of(targetHolder).getEnergy(), 0);

	}

	@Test
	public void useTests() {
		TestingHolder targetHolder = new TestingHolder(500, 1000, EnergyTier.LOW);

		boolean success = false;
		boolean failure = false;

		//Should fail
		if(Energy.of(targetHolder).use(750)){
			success = true;
		} else {
			failure = true;
		}

		assertFalse(success);
		assertTrue(failure);

		assertEquals(500, Energy.of(targetHolder).getEnergy(), 0);

		//reset
		failure = false;

		//Should pass
		if(Energy.of(targetHolder).use(250)){
			success = true;
		} else {
			failure = true;
		}

		assertTrue(success);
		assertFalse(failure);

		assertEquals(250, Energy.of(targetHolder).getEnergy(), 0);

		assertTrue(Energy.of(targetHolder).use(250));
		assertEquals(0, Energy.of(targetHolder).getEnergy(), 0);

		assertFalse(Energy.of(targetHolder).use(250));

	}
}