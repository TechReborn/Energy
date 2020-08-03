package team.reborn.energy;

import java.util.function.Function;
import java.util.function.Predicate;

public interface EnergyHolderProvider<T> extends Predicate<T>, Comparable<EnergyHolderProvider<?>> {
	Class<T> getProvidingClass();

	/**
	 * @return The priority of the holder provider. The higher the value the more important it is.
	 */
	default double getPriority() {
		return 0;
	}

	EnergyStorage apply(T t);

	@Override
	default int compareTo(EnergyHolderProvider<?> other) {
		return Double.compare(getPriority(), other.getPriority());
	}

	static <T> EnergyHolderProvider<T> of(Class<T> clazz, Predicate<T> predicate, Function<T, EnergyStorage> function) {
		return of(clazz, 0, predicate, function);
	}

	static <T> EnergyHolderProvider<T> of(Class<T> clazz, double priority, Predicate<T> predicate, Function<T, EnergyStorage> function) {
		return new EnergyHolderProvider<T>() {
			@Override
			public boolean test(T object) {
				return predicate.test(object);
			}

			@Override
			public EnergyStorage apply(T o) {
				return function.apply(o);
			}

			@Override
			public Class<T> getProvidingClass() {
				return clazz;
			}

			@Override
			public double getPriority() {
				return priority;
			}
		};
	}
}
