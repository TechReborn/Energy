package team.reborn.energy;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Energy {

	private static final Map<EnergyHolderPredicate, Function<Object, EnergyStorage>> holderRegistry = new TreeMap<>(Comparator.reverseOrder());

	static {
		registerHolder(-5, object -> object instanceof EnergyStorage, object -> (EnergyStorage) object);
	}

	/**
	 * Used to get an EnergyHandler from an object
	 *
	 * EnergyStorage is supported by default. other projects may add game specific holders
	 *
	 * If no compatible holder is found an UnsupportedOperationException will be thrown, use {@link #valid(Object)} valid} if you want to check if an object is supported
	 *
	 * @param object The input object, there must be a supported holder for the object
	 * @return an EnergyHandler instance
	 */
	public static EnergyHandler of(Object object) {
		EnergyStorage energyStorage = holderRegistry.entrySet().stream()
			.filter(entry -> entry.getKey().test(object))
			.findFirst()
			.orElseGet(() -> {
				throw new UnsupportedOperationException(String.format("object type (%s) not supported", object.getClass().getName()));
			})
			.getValue()
			.apply(object);
		return new EnergyHandler(energyStorage);
	}

	/**
	 * This is used to ensure there is a supported holder registered for the given object
	 *
	 * @param object An object that should be tested against, must be none null
	 * @return turns true if the supplied object supports energy.
	 */
	public static boolean valid(Object object) {
		return holderRegistry.keySet().stream().anyMatch(objectPredicate -> objectPredicate.test(object));
	}

	public static <T> void registerHolder(Class<T> clazz, Function<Object, EnergyStorage> holderFunction) {
		registerHolder(object -> object.getClass() == clazz, holderFunction);
	}

	public static <T> void registerHolder(double priority, Class<T> clazz, Function<Object, EnergyStorage> holderFunction) {
		registerHolder(priority, object -> object.getClass() == clazz, holderFunction);
	}

	public static void registerHolder(Predicate<Object> supports, Function<Object, EnergyStorage> holderFunction) {
		if (supports instanceof EnergyHolderPredicate) {
			registerHolder((EnergyHolderPredicate) supports, holderFunction);
		} else {
			registerHolder(supports::test, holderFunction);
		}
	}

	public static void registerHolder(double priority, Predicate<Object> supports, Function<Object, EnergyStorage> holderFunction) {
		registerHolder(EnergyHolderPredicate.of(priority, supports), holderFunction);
	}

	public static void registerHolder(EnergyHolderPredicate supports, Function<Object, EnergyStorage> holderFunction) {
		holderRegistry.put(supports, holderFunction);
	}

}
