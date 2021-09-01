package team.reborn.energy;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @deprecated Use the new energy API instead. (Under the team/reborn/energy/api package).
 */
@Deprecated(forRemoval = true)
public final class Energy {

	private static final HashMap<Predicate<Object>, Function<Object, EnergyStorage>> holderRegistry = new LinkedHashMap<>();

	static {
		registerHolder(object -> object instanceof EnergyStorage, object -> (EnergyStorage) object);
	}

	/**
	 *
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
		EnergyHandler holder = ofNullable(object);
		if (holder != null) return holder;

		throw new UnsupportedOperationException(String.format("object type (%s) not supported", object.getClass().getName()));
	}

	/**
	 *
	 * Used to get an EnergyHandler from an object
	 *
	 * EnergyStorage is supported by default. other projects may add game specific holders
	 *
	 * If no compatible holder is found null is returned
	 *
	 * @param object The input object, there must be a supported holder for the object
	 * @return an EnergyHandler instance or null
	 */
	@Nullable
	public static EnergyHandler ofNullable(Object object) {
		if (object == null) return null;

		for (Map.Entry<Predicate<Object>, Function<Object, EnergyStorage>> holder : holderRegistry.entrySet()) {
			if (holder.getKey().test(object)) return new EnergyHandler(holder.getValue().apply(object));
		}

		return null;
	}

	/**
	 *
	 * This is used to ensure there is a supported holder registered for the given object
	 *
	 * @param object An object that should be tested against, must be none null
	 * @return turns true if the supplied object supports energy.
	 */
	public static boolean valid(Object object){
		for (Predicate<Object> predicate : holderRegistry.keySet()) {
			if (predicate.test(object)) return true;
		}
		return false;
	}

	public static <T> void registerHolder(Class<T> clazz, Function<Object, EnergyStorage> holderFunction) {
		registerHolder(object -> object.getClass() == clazz, holderFunction);
	}

	public static void registerHolder(Predicate<Object> supports, Function<Object, EnergyStorage> holderFunction) {
		holderRegistry.put(supports, holderFunction);
	}

}
