import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/*
Есть CSV файл (https://www.kaggle.com/datasets/luiscorter/netflix-original-films-imdb-scores),
требуется вычитать его и произвести следующие операции:
- Посчитать кол-во фильмов каждого жанра и языка
- составить топ-5 самых популярных
- Вывести самый новый и самый старый фильм
- Вывести общую длительность фильмов в часах и днях.
- Для каждого года вывести самый высоко оцененный
и самый низко оцененный фильм

Необходимо читать с диска и проходиться по фильмам один раз
O(n)
  */
public class Task1 {

    //файл предварительно скачан в директорию данного проекта
    public static final String filePath = "src/main/resources/NetflixOriginals.csv";
    public static void main(String[] args) throws IOException {
        Task1 task = new Task1();
        task.readFile(filePath);
    }

    public void readFile(String filePath) throws IOException {
        CSVReader reader = new CSVReader();
        MovieParser parser = new MovieParser();
        MovieAnalyser analyser = new MovieAnalyser();

        reader.readCSVFile(filePath).stream()
                .map(parser::parseCSVLine)
                .filter(Objects::nonNull)
                .forEach(analyser::analyze);

        printResults(analyser);
    }

    // выводим результаты задачи
    private void printResults(MovieAnalyser analyser) throws UnsupportedEncodingException {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.out.println("Количество фильмов по жанрам:");
        analyser.genreCount.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed())
                .forEach(e -> System.out.println(" - " + e.getKey() + ": " + e.getValue()));

        System.out.println("\nКоличество фильмов по языкам:");
        analyser.languageCount.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed())
                .forEach(e -> System.out.println(" - " + e.getKey() + ": " + e.getValue()));

        System.out.println("\nТоп-5 фильмов по рейтингу IMDB:");
        analyser.top5.stream()
                .sorted(Comparator.comparingDouble((Movie m) -> m.imdb).reversed())
                .forEach(m -> System.out.printf(" %.1f — %s (%s)%n", m.imdb, m.title, m.premiere));

        if (analyser.oldest != null)
            System.out.printf("%nСамый старый фильм: %s (%s)%n", analyser.oldest.title, analyser.oldest.premiere);
        if (analyser.newest != null)
            System.out.printf("Самый новый фильм: %s (%s)%n", analyser.newest.title, analyser.newest.premiere);

        double totalHours = analyser.totalMinutes / 60.0;
        double totalDays = totalHours / 24.0;
        System.out.printf("%nОбщая длительность всех фильмов: %.1f часов (%.2f дней)%n", totalHours, totalDays);

        System.out.println("\nСамые высоко и низко оцененные фильмы по годам:");
        analyser.highestRated.forEach((year, movie) -> {
            Movie low = analyser.lowestRated.get(year);
            System.out.printf(" %d: ↑ %.1f — %s | ↓ %.1f — %s%n",
                    year, movie.imdb, movie.title, low.imdb, low.title);
        });
    }

}
