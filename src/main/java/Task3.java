import java.util.Arrays;
import java.util.List;

/*
    Создать метод, который будет принимать
    N списков с элементами типов-наследников
    Number и выводить сумму всех элементов
     */
public class Task3 {
    @SafeVarargs
    public static double listsOfNumbersSummarize(List<? extends Number>... lists) {
        double result = 0.0;
        for (var list : lists) {
            for (var i : list) {
                result += i.doubleValue();
            }
        }
        return result;
    }

    // используя StreamAPI
    @SafeVarargs
    public static double listsOfNumbersSummarizeWithStreamAPI(List<? extends Number>... lists) {
        return Arrays.stream(lists)
                .parallel()
                .flatMap(List::stream)
                .mapToDouble(Number::doubleValue)
                .sum();
    }
}
