package team.reborn.energy;

import java.util.HashMap;
import java.util.function.Function;

public class Energy {

	private static final HashMap<Class<?>, Function<Object, EnergyStorage>> holderRegistry = new HashMap<>();

	public static EnergyHandler of(Object object) {
		if (object instanceof EnergyStorage) {
			return new EnergyHandler((EnergyStorage) object);
		}

		return new EnergyHandler(holderRegistry.getOrDefault(object.getClass(), energyHolder -> {
			throw new UnsupportedOperationException("Could not find holder for " + energyHolder.getClass().getName());
		}).apply(object));
	}

	/**
	 * Used to register a holder for a specific class
	 *
	 * @param clazz the class that you wish to register a holder for
	 * @param holderFunction A function that returns an energy storage for the supplied object
	 */
	public static <T> void registerHolder(Class<T> clazz, Function<T, EnergyStorage> holderFunction) {
		if (holderRegistry.containsKey(clazz)) {
			throw new RuntimeException(clazz.getName() + " already has a registered holder");
		}
		holderRegistry.put(clazz, (Function<Object, EnergyStorage>) holderFunction);
	}

}
