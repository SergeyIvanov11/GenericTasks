import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/*
    Создать метод с 2-мя дженериками - А и B, который принимает:
    список из типа А и лямбду - функцию конвертер из типа A в тип B
    и возвращает список типа B Пример <Integer, String>
    List<String> convert(List<Integer>, Function<Integer, String>)
     */
public class Task2 {
    public static <A, B> List<B> map(List<A> list, Function<A, B> mapper) {
        List<B> result = new ArrayList<>();
        for (A a : list) {
            result.add(mapper.apply(a));
        }
        return result;
    }

    // используя StreamAPI
    public static <A, B> List<B> mapWithStreamAPI(List<A> list, Function<A, B> mapper) {
        return list.stream()
                .map(mapper)
                .toList();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<String> strings = map(numbers, n -> "Число: " + n);
        System.out.println(strings);
    }
}
