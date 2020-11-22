package team.reborn.energy.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import team.reborn.energy.Energy;

public class ValidBenchmark {

    static {
        Energy.registerHolder((obj) -> obj.hashCode() == 0, o -> null);
        Energy.registerHolder((obj) -> obj instanceof String && obj.toString().equals("hello"), o -> null);
        Energy.registerHolder((obj) -> obj instanceof String && obj.toString().equals("world"), o -> null);
        Energy.registerHolder((obj) -> obj instanceof String && obj.toString().equals("123"), o -> null);
        Energy.registerHolder((obj) -> obj.hashCode() == 0, o -> null);
    }

    @Benchmark
    public boolean isValid() {
       return Energy.valid(new String("123"));
    }
}
