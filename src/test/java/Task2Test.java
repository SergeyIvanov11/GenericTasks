import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class Task2Test {
    @Test
    void testMap_IntegerToString() {
        List<Integer> input = List.of(1, 2, 3);
        Function<Integer, String> mapper = i -> "num:" + i;

        List<String> result = Task2.map(input, mapper);

        assertEquals(List.of("num:1", "num:2", "num:3"), result);
    }

    @Test
    void testMap_EmptyList() {
        List<Integer> input = List.of();
        Function<Integer, String> mapper = Object::toString;

        List<String> result = Task2.map(input, mapper);

        assertTrue(result.isEmpty());
    }

    @Test
    void testMapWithStreamAPI_IntegerToString() {
        List<Integer> input = List.of(1, 2, 3);
        Function<Integer, String> mapper = Object::toString;

        List<String> result = Task2.mapWithStreamAPI(input, mapper);

        assertEquals(List.of("1", "2", "3"), result);
    }

    @Test
    void testMapWithStreamAPI_DoubleToInteger() {
        List<Double> input = List.of(1.1, 2.2, 3.3);
        Function<Double, Integer> mapper = d -> (int) Math.floor(d);

        List<Integer> result = Task2.mapWithStreamAPI(input, mapper);

        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void testBothMethodsReturnSameResult() {
        List<String> input = List.of("a", "b", "c");
        Function<String, Integer> mapper = String::length;

        List<Integer> r1 = Task2.map(input, mapper);
        List<Integer> r2 = Task2.mapWithStreamAPI(input, mapper);

        assertEquals(r1, r2);
    }

    @Test
    void testMap_NullInput_ThrowsException() {
        Function<Integer, String> mapper = Object::toString;
        assertThrows(NullPointerException.class, () -> Task2.map(null, mapper));
    }

    @Test
    void testMap_NullMapper_ThrowsException() {
        List<Integer> input = List.of(1, 2, 3);
        assertThrows(NullPointerException.class, () -> Task2.map(input, null));
    }
}