/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.sample.libs.FastArray;

@State(Scope.Thread)
public class JMHSortBenchmark {
  public static final int DEPTH = 100;
  Random random;
  Integer[] array;

  List<Integer> arrayList = new ArrayList<Integer>(DEPTH);
  FastArray<Integer> flist = new FastArray<>(Integer.class, DEPTH);
  
  Integer[] asArray = new Integer[DEPTH];
  int asArrayFE = 0;
  
  
  Integer temp;
  
  @Setup(Level.Trial)
  public void init() {
    arrayList.clear();
    flist.clear();
    asArrayFE = 0;
    
    random = new Random();
    array = new Integer[DEPTH];
    for (int i = 0; i < DEPTH; i++) {
      int randomNumber = random.nextInt();
      array[i] = randomNumber;
      arrayList.add(randomNumber);
      flist.add(randomNumber);
      asArray[i] = randomNumber;
    }
  }

  @Benchmark
  public void arrayList() {
    for (Integer i : arrayList) {
      temp = i;
    }
  }

  @Benchmark
  public void fastarray() {
    for (int i=0;i<flist.length;i++) {
      temp = flist.elements[i];
    }
  }

  @Benchmark
  public void fastarray_directAccess() {
    flist.iterate(i -> {temp = i;});
  }

  @Benchmark
  public void purearray() {
    asArrayFE = 0;
    for (int i=0;i<DEPTH;i++) {
      temp = asArray[i];
    }
  }

  public static void main(String[] args) throws RunnerException {

    Options options = new OptionsBuilder()
        .include(JMHSortBenchmark.class.getSimpleName()).threads(1)
        .forks(1).shouldFailOnError(true).shouldDoGC(true)
        .jvmArgs("-server").build();
    new Runner(options).run();

  }

}
