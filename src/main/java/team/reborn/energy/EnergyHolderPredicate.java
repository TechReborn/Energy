package team.reborn.energy;

import java.util.function.Predicate;

public interface EnergyHolderPredicate extends Predicate<Object>, Comparable<EnergyHolderPredicate> {
    /**
     * @return the priority of the holder provider.
     */
    default double getPriority() {
        return 0;
    }
    
    @Override
    default int compareTo(EnergyHolderPredicate other) {
        return Double.compare(getPriority(), other.getPriority());
    }
    
    static EnergyHolderPredicate of(Predicate<Object> predicate) {
    	return predicate::test;
    }
	
	static EnergyHolderPredicate of(double priority, Predicate<Object> predicate) {
		return new EnergyHolderPredicate() {
			@Override
			public boolean test(Object object) {
				return predicate.test(object);
			}
			
			@Override
			public double getPriority() {
				return priority;
			}
		};
	}
}
