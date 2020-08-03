package team.reborn.energy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Energy {

	@SuppressWarnings("rawtypes")
	private static final List<EnergyHolderProvider> PROVIDERS = new ArrayList<>();
	@SuppressWarnings("rawtypes")
	private static final Predicate TRUE = o -> true;

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
		EnergyStorage energyStorage = PROVIDERS.stream()
			.filter(provider -> provider.getProvidingClass().isAssignableFrom(object.getClass()) && provider.test(object))
			.findFirst()
			.orElseGet(() -> {
				throw new UnsupportedOperationException(String.format("object type (%s) not supported", object.getClass().getName()));
			})
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
		return PROVIDERS.stream().anyMatch(provider -> provider.getProvidingClass().isAssignableFrom(object.getClass()) && provider.test(object));
	}

	public static <T> void registerHolder(Class<T> clazz, Function<Object, EnergyStorage> holderFunction) {
		registerHolder(object -> object.getClass() == clazz, holderFunction);
	}

	public static <T> void registerHolder(double priority, Class<T> clazz, Function<Object, EnergyStorage> holderFunction) {
		registerHolder(priority, object -> object.getClass() == clazz, holderFunction);
	}

	public static void registerHolder(Predicate<Object> supports, Function<Object, EnergyStorage> holderFunction) {
		registerHolder(0, supports, holderFunction);
	}

	public static void registerHolder(double priority, Predicate<Object> supports, Function<Object, EnergyStorage> holderFunction) {
		registerHolder(Object.class, priority, supports, holderFunction);
	}

	public static <T> void registerHolder(Class<T> clazz, double priority, Function<T, EnergyStorage> holderFunction) {
		registerHolder(clazz, priority, TRUE, holderFunction);
	}

	public static <T> void registerHolder(Class<T> clazz, double priority, Predicate<T> supports, Function<T, EnergyStorage> holderFunction) {
		registerHolder(EnergyHolderProvider.of(clazz, priority, supports, holderFunction));
	}

	public static void registerHolder(EnergyHolderProvider provider) {
		int insertionPoint = Collections.binarySearch(PROVIDERS, provider, Comparator.reverseOrder());
		PROVIDERS.add((insertionPoint > -1) ? insertionPoint : (-insertionPoint) - 1, provider);
	}

}
