package team.reborn.energy;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Energy {

	private static final HashMap<Predicate<Object>, Function<Object, EnergyStorage>> holderRegistry = new HashMap<>();

	public static EnergyHandler of(Object object) {
		if (object instanceof EnergyStorage) {
			return new EnergyHandler((EnergyStorage) object);
		}

		EnergyStorage energyStorage = holderRegistry.entrySet().stream().filter(entry -> entry.getKey().test(object)).findFirst().orElseGet(() -> {
			throw new UnsupportedOperationException("object type not supported");
		}).getValue().apply(object);
		return new EnergyHandler(energyStorage);
	}

	public static boolean valid(Object object){
		return holderRegistry.keySet().stream().anyMatch(objectPredicate -> objectPredicate.test(object));
	}

	public static <T> void registerHolder(Class<T> clazz, Function<Object, EnergyStorage> holderFunction) {
		registerHolder(object -> object.getClass() == clazz, holderFunction);
	}

	public static void registerHolder(Predicate<Object> supports, Function<Object, EnergyStorage> holderFunction) {
		holderRegistry.put(supports, holderFunction);
	}

}
