# Energy

An Energy API for Fabric mods, originally written for TechReborn.

Uses Fabric's API Lookup and Transaction systems.

# Conventions
To ensure good interop between all the mods using this API, here are a few conventions that should be followed.

* Reference energy values
  * 1 coal = 4000
  * 1 plank = 750
* The system is push-based.
  * This means that power sources are responsible for pushing power to nearby machines.
  * Machines and wires should NOT pull power from other sources.

# Including the API in your project

Find the latest versions [here](https://maven.fabricmc.net/teamreborn/energy/)

Add the following into your dependencies block in build.gradle

```groovy
include modApi('teamreborn:energy:<latest_version>') {
    exclude(group: "net.fabricmc.fabric-api")
}
```

# Documentation
The API revolves around [`EnergyStorage`](src/main/java/team/reborn/energy/api/EnergyStorage.java).
Make sure to check out the documentation.

A few examples follow to get you started.

## Implementing energy-containing blocks
The easiest way, with fixed capacity and insertion/extraction limits:
```groovy
public class MyBlockEntity extends BlockEntity {
    // Store a SimpleEnergyStorage in the block entity class.
    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(CAPACITY, MAX_INSERT, MAX_EXTRACT) {
        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };
    
    // Use the energy internally, for example in tick()
    public void tick() {
        if (!world.isClient && energyStorage.amount >= 10) {
            energyStorage.amount -= 10;
            // do something with the 10 energy we just used.
            markDirty();
        }
    }
    
    // Don't forget to save/read the energy in the block entity NBT.
}

// Don't forget to register the energy storage. Make sure to call this after you create the block entity type.
BlockEntityType<MyBlockEntity> MY_BLOCK_ENTITY;
EnergyStorage.SIDED.registerForBlockEntity((myBlockEntity, direction) -> myBlockEntity.energyStorage, MY_BLOCK_ENTITY);
```

`SimpleSidedEnergyContainer` may be used if the I/O limits are side-dependent.

If you know what you are doing, you can also implement `EnergyStorage` directly, but in most cases that's not necessary.
Refer to the documentation of this API, of the Transaction API and API Lookup for details.

## Usage example (blocks)
Get an energy storage:
```groovy
@Nullable
EnergyStorage maybeStorage = EnergyStorage.SIDED.find(world, pos, direction);
```
Get an adjacent energy storage:
```groovy
// Known things
World world; BlockPos currentPos; Direction adjacentDirection;
// Get adjacent energy storage, or null if there is none
@Nullable
EnergyStorage maybeStorage = EnergyStorage.SIDED.find(world, currentPos.offset(adjacentDirection), adjacentDirection.getOpposite());
```
Move energy between two storages:
```groovy
EnergyStorage source, target;

long amountMoved = EnergyStorageUtil.move(
        source, // from source
        target, // into target
        Long.MAX_VALUE, // no limit on the amount
        null // create a new transaction for this operation 
);
```
Try to extract an exact amount of energy:
```groovy
EnergyStorage source;
long amountToUse;

// Open a transaction: this allows cancelling the operation if it doesn't go as expected.
try (Transaction transaction = Transaction.openOuter()) {
    // Try to extract, will return how much was actually extracted
    long amountExtracted = source.extract(amountToUse, transaction);
    if (amountExtracted == amountToUse) {
        // "Commit" the transaction to make sure the change is applied.
        transaction.commit();
    } else {
        // Doing nothing "aborts" the transaction, cancelling the change.
    }
}
```

## Creating chargeable items
The easiest way to create an item that can be charged by supported mods is by implementing `SimpleEnergyItem` on your item class.
The functions should be self-explanatory.

For more complex items, `EnergyStorage.ITEM` may be used directly.
Make sure you read the documentation of `ContainerItemContext` if you go down that path.

## Charging items
Check out how you can create a `ContainerItemContext`,
and use it to query an `EnergyStorage` implementation with `EnergyStorage.SIDED`.