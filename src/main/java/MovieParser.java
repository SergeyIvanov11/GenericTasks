import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MovieParser {
    public final String REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    public final String DATE_PATTERN = "MMMM d, yyyy";

    public Movie parseCSVLine(String line) {
        try {
            String[] parts = line.split(REGEX, -1); // корректный split с кавычками

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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.ENGLISH);
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

}
