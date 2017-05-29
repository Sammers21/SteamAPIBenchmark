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

import org.openjdk.jmh.annotations.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.lang.Math.sqrt;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class Main {


    @Benchmark
    public void range9223372036854765807_9223372036854775807_Long_parallel_prime_for() {
        input("9223372036854765807 9223372036854775807 7 kek.txt".split(" "), Long.class, "p", false);
    }

    @Benchmark
    public void range9223372036854765807_9223372036854775807_Long_parallel_prime_stream() {
        input("9223372036854765807 9223372036854775807 7 kek.txt".split(" "), Long.class, "p", true);
    }


    @Benchmark
    public void range_1_1pow10_6_Long_sequential_prime_for() {
        input(new String[]{"1", "1000000", "9", "kek.txt"}, Long.class, "s", false);
    }

    @Benchmark
    public void range_1_1pow10_6_Long_sequential_prime_stream() {
        input(new String[]{"1", "1000000", "9", "kek.txt"}, Long.class, "s", true);
    }

    @Benchmark
    public void range_1_1pow10_6_Long_parallel_prime_for() {
        input(new String[]{"1", "1000000", "9", "kek.txt"}, Long.class, "p", false);
    }

    @Benchmark
    public void range_1_1pow10_6_Long_parallel_prime_stream() {
        input(new String[]{"1", "1000000", "9", "kek.txt"}, Long.class, "p", true);
    }

    @Benchmark
    public void range_1_1pow10_6_BigInt_sequential_prime_for() {
        input(new String[]{"1", "1000000", "9", "kek.txt"}, BigInteger.class, "s", false);
    }

    @Benchmark
    public void range_1_1pow10_6_BigInt_sequential_prime_stream() {
        input(new String[]{"1", "1000000", "9", "kek.txt"}, BigInteger.class, "s", true);
    }

    @Benchmark
    public void range_1_1pow10_6_BigInt_parallel_prime_for() {
        input(new String[]{"1", "1000000", "9", "kek.txt"}, BigInteger.class, "p", false);
    }

    @Benchmark
    public void range_1_1pow10_7_BigInt_parallel_prime_stream() {
        input(new String[]{"1", "10000000", "9", "kek.txt"}, BigInteger.class, "p", true);
    }
/*
    @Benchmark
    public void range_1_1pow10_7_Long_sequential_prime_for() {
        input(new String[]{"1", "10000000", "9", "kek.txt"}, Long.class, "s", false);
    }

    @Benchmark
    public void range_1_1pow10_7_Long_sequential_prime_stream() {
        input(new String[]{"1", "10000000", "9", "kek.txt"}, Long.class, "s", true);
    }

    @Benchmark
    public void range_1_1pow10_7_Long_parallel_prime_for() {
        input(new String[]{"1", "10000000", "9", "kek.txt"}, Long.class, "p", false);
    }

    @Benchmark
    public void range_1_1pow10_7_Long_parallel_prime_stream() {
        input(new String[]{"1", "10000000", "9", "kek.txt"}, Long.class, "p", true);
    }

    @Benchmark
    public void range_1_1pow10_7_BigInt_sequential_prime_for() {
        input(new String[]{"1", "10000000", "9", "kek.txt"}, BigInteger.class, "s", false);
    }

    @Benchmark
    public void range_1_1pow10_7_BigInt_sequential_prime_stream() {
        input(new String[]{"1", "10000000", "9", "kek.txt"}, BigInteger.class, "s", true);
    }

    @Benchmark
    public void range_1_1pow10_7_BigInt_parallel_prime_for() {
        input(new String[]{"1", "10000000", "9", "kek.txt"}, BigInteger.class, "p", false);
    }
*/

    /**
     * Выполняет данное задание
     *
     * @param args cmd args
     * @param type Which class ust be used as number?
     * @param t    parallel or sequential
     */
    public static void input(String[] args, Class<?> type, String t, boolean stream_prime) {
        if (args.length != 3 && args.length != 4) {
            System.out.print("Usage: " +
                    "./executable N M C [file_to_output]");
            System.exit(0);
        }

        Number N = new Long(args[0]);
        Number M = new Long(args[1]);
        Number C = Integer.parseInt(args[2]);

        Stream<Number> numberStream = range_with_last_digit(N, M, C, type /* Long.class / BigInteger.class*/);

        if (t.equals("s") || t.equals("sequential")) {
            numberStream = numberStream.sequential();
        } else {
            numberStream = numberStream.parallel();
        }
        List<Number> collect;
        if (!stream_prime)
            collect = numberStream
                    .filter(Main::prime)
                    .collect(Collectors.toList());
        else {
            collect = numberStream
                    .filter(s -> Main.prime_stream(s, t))
                    .collect(Collectors.toList());
        }


        /*String s = collect.stream()
                .sequential()
                .map(Number::toString)
                .reduce((x, y) -> x + "," + y)
                .get();

        String answer = String.format("%d:<%s>.", collect.size(), s);

        if (args.length == 3)
            System.out.print(answer);
        else if (args.length == 4) {
            Path path = Paths.get(args[3]);
            try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
                writer.write(answer);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public static Stream<Number> range_with_last_digit(Number N, Number M, Number c, Class<?> type) {
        ArrayList<Number> nums = new ArrayList<Number>((int) ((M.longValue() - (N.longValue())) / 10));

        while (N.longValue() % 10 != c.longValue()) {
            N = N.longValue() + 1;
        }


        while (N.longValue() <= M.longValue() && N.longValue() > 0) {
            if (type == Long.class)
                nums.add(N.longValue());
            else
                nums.add(new BigInteger(N.toString()));

            N = N.longValue() + 10;
        }

        return nums.stream();
    }

    public static boolean prime(Number n) {

        if (n instanceof BigInteger) {
            for (long i = 2; i <= sqrt(n.longValue()); i++)
                if (n.longValue() % i == 0)
                    return false;

        } else {
            for (long i = 2; i <= sqrt((Long) n); i++)
                if ((Long) n % i == 0)
                    return false;
        }
        return true;
    }

    public static boolean prime_stream(Number n, String type) {

        LongPredicate isDivisible = index -> n.longValue() % index == 0;
        if (type.equals("s") || type.equals("sequential")) {
            return n.longValue() > 1 && LongStream.range(2, ((int) Math.sqrt(n.longValue())))
                    .sequential()
                    .noneMatch(isDivisible);
        } else {
            return n.longValue() > 1 && LongStream.range(2, ((int) Math.sqrt(n.longValue())))
                    .parallel()
                    .noneMatch(isDivisible);
        }
    }
}
