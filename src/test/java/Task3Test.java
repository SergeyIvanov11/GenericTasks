import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class Task3Test {
    private final List<Integer> ints = List.of(1, 2, 3, 4, 5);
    private final List<Double> doubles = List.of(2.5, 3.5, 4.6, 5.4, 6.3, 7.7);
    private final List<Long> longs = List.of(10L, 20L, 33L, 45L, 57L);

    @Test
    void listsOfNumbersSummarizeTest() {
        double result = Task3.listsOfNumbersSummarize(ints, doubles, longs);
        assertEquals(210.0, result);
    }

    @Test
    void listsOfNumbersSummarizeWithStreamAPITest() {
        double result = Task3.listsOfNumbersSummarizeWithStreamAPI(ints, doubles, longs);
        assertEquals(210.0, result);
    }

    @Test
    void emptyListsTest() {
        List<Integer> empty1 = List.of();
        List<Double> empty2 = List.of();
        double first = Task3.listsOfNumbersSummarize(empty1, empty2);
        double second = Task3.listsOfNumbersSummarizeWithStreamAPI(empty1, empty2);
        assertEquals(0.0, first);
        assertEquals(0.0, second);
    }

    @Test
    void speedTest() throws UnsupportedEncodingException {
        Random random = new Random(42);
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        List<Integer> randomInts = new ArrayList<>();
        List<Double> randomDoubles = new ArrayList<>();
        List<Long> randomLongs = new ArrayList<>();

        for (int i = 0; i < 10_000_000; i++) {
            randomInts.add(random.nextInt(999));
            randomDoubles.add(random.nextDouble(99.9));
            randomLongs.add(random.nextLong(999L));
        }
        long startWithoutStreams = System.nanoTime();
        double first = Task3.listsOfNumbersSummarize(randomInts, randomDoubles, randomLongs);
        long timeWithoutStreams = System.nanoTime() - startWithoutStreams;

        long startWithStreams = System.nanoTime();
        double second = Task3.listsOfNumbersSummarizeWithStreamAPI(randomInts, randomDoubles, randomLongs);
        long timeWithStreams = System.nanoTime() - startWithStreams;

        assertEquals(first, second, 1e-3);
        System.out.println("Время работы метода без StreamAPI: " + timeWithoutStreams / 1_000_000 + " мс");
        System.out.println("Время работы метода с использованием StreamAPI: " + timeWithStreams / 1_000_000 + " мс");
    }


}