# Energy

An Energy API used by TechReborn

* Fully unit tested
* Depends on Minecraft and fabric-loader

Currently WIP, the API may change at any time! I am very open to feedback and suggestions on the issue tracker.

This may seem quite different from any energy API that you have seen before (code wise), as this is because I wanted to try something different.

# Reference Values

* 1 coal = 4000
* 1 plank = 750

# Including the API in your project

![Latest version](https://img.shields.io/github/v/tag/TechReborn/Energy?label=Latest%20Version)

Add the following into your dependencies block in build.gradle

```groovy
modApi 'teamreborn:energy:<latest_version>'
include 'teamreborn:energy:<latest_version>'
```

## Basic Example

This basic example shows how to move energy from place to another, the source or target can be anything that has a registered holder.

```java
Energy.of(source)
	.side(side)
	.into(
	   Energy.of(target).side(side.getOpposite())
	)
	.move();
```

# Full documentation

WIP will be done at some point

## Energy

### Energy.of

`Energy.of(object)` will return an `EnergyHandler` that can be used to read or interact with the energy of a supplied object (More on this later).

The Object may be an instance of `EnergyStorage` or any other object that has a supported holder registered (More on this later). 

RebornCore (Included in TechReborn) registers a holder for Minecraft's `ItemStack`, allow you easily get the energy off an `ItemStack`. BlockEntities are expected to implement `EnergyStorage`

### Energy.valid

`Energy.valid` will return true if the given object is supported

## EnergyHandler

An instance of EnergyHandler can be got from `Energy.of()`

### getEnergy

Returns the current amount of stored energy

```java
double energy = Energy.of(object).getEnergy()
```

### getMaxStored

Returns the maximum amount of energy that the holder can store

```java
double energy = Energy.of(object).getEnergy()
```

### set

Set the amount of energy stored in the holder

```java
Energy.of(object).set(250)
```

### extract

Extract upto the amount of energy provided, returns the amount of energy that was extracted. This is limited by the max output of the holder, as well as the amount of energy that is held in the holder. Returns the the amount of energy actually extracted from the holder.

```java
double extracted = Energy.of(object).extract(20)
```

### insert

Insert upto the amount of energy provided, returns the amount of energy that was inserted. This is limited by the max input of the holder, as well as the amount of free space in the hold. Returns the the amount of energy actually inserted from the holder.


```java
double inserted = Energy.of(object).insert(20)
```

### getMaxInput

### getMaxOutput

### simulate

### side

### into

### use(amount)

## EnergyMovement

### move

### move(amount)

### onlyIf