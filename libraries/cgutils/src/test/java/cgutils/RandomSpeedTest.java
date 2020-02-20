package cgutils;

import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import cgutils.random.FastRandom;

@State(Scope.Thread)
public class RandomSpeedTest {
Random random = new Random(42);
Random frandom = new FastRandom(42);
  
static long result;
static double dresult;
  
  @Benchmark
  public void stockRandom() {
    result = random.nextLong();
    dresult = random.nextDouble();
  }
  
  @Benchmark
  public void fastRandom() {
    result = frandom.nextLong();
    dresult = frandom.nextDouble();
  }
  
  public static void main(String[] args) throws RunnerException {

    Options options = new OptionsBuilder()
        .include(RandomSpeedTest.class.getSimpleName()).threads(1)
        .forks(1).shouldFailOnError(true).shouldDoGC(true)
        .jvmArgs("-server").build();
    new Runner(options).run();

  }
}
