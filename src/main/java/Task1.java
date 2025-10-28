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
    protected static final Map<String, Integer> genreCount = new HashMap<>();
    protected static final Map<String, Integer> languageCount = new HashMap<>();
    protected static final Map<Integer, Movie> highestRated = new HashMap<>();
    protected static final Map<Integer, Movie> lowestRated = new HashMap<>();
    protected static final Queue<Movie> top5 = new PriorityQueue<>(Comparator.comparingDouble(m -> m.imdb));
    protected static Movie oldest = null;
    protected static Movie newest = null;
    protected static long totalMinutes = 0L;

    static class Movie {
        String title;
        String genre;
        String language;
        LocalDate premiere;
        int runtimeMinutes;
        double imdb;

        Movie(String title, String genre, String language, LocalDate premiere, int runtimeMinutes, double imdb) {
            this.title = title;
            this.genre = genre;
            this.language = language;
            this.premiere = premiere;
            this.runtimeMinutes = runtimeMinutes;
            this.imdb = imdb;
        }
    }

    public static void main(String[] args) throws IOException {
        Task1 task = new Task1();
        task.readFile(filePath);
    }

    public void readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(path, java.nio.charset.StandardCharsets.ISO_8859_1)) {
            lines.skip(1)                             // пропускаем заголовок
                    .map(this::parseToMovie)
                    .filter(Objects::nonNull)
                    .forEach(this::analyzeMovie);
        }
        printResults();
    }

    // парсим строку CSV в объект Movie
    protected Movie parseToMovie(String line) {
        try {
            // CSV формат (пример): Title,Genre,Premiere,Runtime,IMDB Score,Language
            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // корректный split с кавычками

            if (parts.length < 6) return null;

            String title = parts[0].replace("\"", "").trim();
            String genre = parts[1].trim();
            String premiereStr = parts[2].replace("\"", "").replace(".", ",").trim();
            String runtimeStr = parts[3].trim();
            String imdbStr = parts[4].trim();
            String language = parts[5].trim();

            LocalDate premiere = null;
            if (!premiereStr.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
                    premiere = LocalDate.parse(premiereStr, formatter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int runtime = 0;
            if (runtimeStr.endsWith("min")) {
                runtimeStr = runtimeStr.replace("min", "").trim();
            }
            if (!runtimeStr.isEmpty()) {
                runtime = Integer.parseInt(runtimeStr);
            }

            double imdb = 0.0;
            if (!imdbStr.isEmpty()) {
                imdb = Double.parseDouble(imdbStr);
            }

            return new Movie(title, genre, language, premiere, runtime, imdb);
        } catch (Exception e) {
            return null; // строка некорректная — пропускаем
        }
    }

    protected void analyzeMovie(Movie m) {
        genreCount.merge(m.genre, 1, Integer::sum);
        languageCount.merge(m.language, 1, Integer::sum);
        updateOldestNewest(m);
        updateHighestLowestRated(m);
        updateTop5(m);
        totalMinutes += m.runtimeMinutes;
    }

    protected void updateOldestNewest(Movie m) {
        if (m.premiere == null) return;
        if (oldest == null || m.premiere.isBefore(oldest.premiere)) oldest = m;
        if (newest == null || m.premiere.isAfter(newest.premiere)) newest = m;
    }

    protected void updateHighestLowestRated(Movie m) {
        if (m.premiere == null) return;
        int year = m.premiere.getYear();

        highestRated.merge(year, m,
                (old, cur) -> cur.imdb > old.imdb ? cur : old);

        lowestRated.merge(year, m,
                (old, cur) -> cur.imdb < old.imdb ? cur : old);
    }

    protected void updateTop5(Movie m) {
        top5.offer(m);
        if (top5.size() > 5) {
            top5.poll(); // убираем наименьший рейтинг
        }
    }

    // выводим результаты задачи
    protected void printResults() throws UnsupportedEncodingException {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.out.println("Количество фильмов по жанрам:");
        genreCount.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed())
                .forEach(e -> System.out.println(" - " + e.getKey() + ": " + e.getValue()));

        System.out.println("\nКоличество фильмов по языкам:");
        languageCount.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed())
                .forEach(e -> System.out.println(" - " + e.getKey() + ": " + e.getValue()));

        System.out.println("\nТоп-5 фильмов по рейтингу IMDB:");
        top5.stream()
                .sorted(Comparator.comparingDouble((Movie m) -> m.imdb).reversed())
                .forEach(m -> System.out.printf(" %.1f — %s (%s)%n", m.imdb, m.title, m.premiere));

        if (oldest != null)
            System.out.printf("%nСамый старый фильм: %s (%s)%n", oldest.title, oldest.premiere);
        if (newest != null)
            System.out.printf("Самый новый фильм: %s (%s)%n", newest.title, newest.premiere);

        double totalHours = totalMinutes / 60.0;
        double totalDays = totalHours / 24.0;
        System.out.printf("%nОбщая длительность всех фильмов: %.1f часов (%.2f дней)%n", totalHours, totalDays);

        System.out.println("\nСамые высоко и низко оцененные фильмы по годам:");
        highestRated.forEach((year, movie) -> {
            Movie low = lowestRated.get(year);
            System.out.printf(" %d: ↑ %.1f — %s | ↓ %.1f — %s%n",
                    year, movie.imdb, movie.title, low.imdb, low.title);
        });
    }

}
