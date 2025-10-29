import java.time.Duration;
import java.util.*;

public class MovieAnalyser {
    public final Map<String, Integer> genreCount = new HashMap<>();
    public final Map<String, Integer> languageCount = new HashMap<>();
    public final Map<Integer, Movie> highestRated = new HashMap<>();
    public final Map<Integer, Movie> lowestRated = new HashMap<>();
    public final Queue<Movie> top5 = new PriorityQueue<>(Comparator.comparingDouble(m -> m.imdb));
    public Movie oldest = null;
    public Movie newest = null;
    public Duration totalMinutes = Duration.ofMinutes(0L);

    public void analyze(Movie m) {
        genreCount.merge(m.genre, 1, Integer::sum);
        languageCount.merge(m.language, 1, Integer::sum);
        updateOldestNewest(m);
        updateHighestLowestRated(m);
        updateTop5(m);
        totalMinutes = totalMinutes.plusMinutes(m.runtimeMinutes);
    }

    private void updateOldestNewest(Movie m) {
        if (m.premiere == null) return;
        if (oldest == null || m.premiere.isBefore(oldest.premiere)){
            oldest = m;
            return;
        }
        if (newest == null || m.premiere.isAfter(newest.premiere)) newest = m;
    }

    private void updateHighestLowestRated(Movie m) {
        if (m.premiere == null) return;
        int year = m.premiere.getYear();

        highestRated.merge(year, m,
                (old, cur) -> cur.imdb > old.imdb ? cur : old);

        lowestRated.merge(year, m,
                (old, cur) -> cur.imdb < old.imdb ? cur : old);
    }

    private void updateTop5(Movie m) {
        top5.offer(m);
        if (top5.size() > 5) {
            top5.poll(); // убираем наименьший рейтинг
        }
    }

}
