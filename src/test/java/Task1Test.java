import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class Task1Test {
    private Task1 task;

    @BeforeEach
    void setUp() {
        task = new Task1();

        // сбрасываем все статические поля перед каждым тестом
        Task1.genreCount.clear();
        Task1.languageCount.clear();
        Task1.oldest = null;
        Task1.newest = null;
        Task1.totalMinutes = 0L;
    }

    @Test
    void testParseToMovie_validLine() {
        String line = "\"Movie A\",Action,\"June 26, 2015\",120 min,7.8,English";
        Task1.Movie movie = task.parseToMovie(line);

        assertNotNull(movie);
        assertEquals("Movie A", movie.title);
        assertEquals("Action", movie.genre);
        assertEquals("English", movie.language);
        assertEquals(120, movie.runtimeMinutes);
        assertEquals(7.8, movie.imdb);
        assertEquals(LocalDate.of(2015, 6, 26), movie.premiere);
    }

    @Test
    void testParseToMovie_invalidLine() {
        String line = "invalid,data,line";
        Task1.Movie movie = task.parseToMovie(line);
        assertNull(movie);
    }

    @Test
    void testUpdateOldestNewest() {
        Task1.Movie m1 = new Task1.Movie("Old", "Drama", "English", LocalDate.of(2010, 1, 1), 100, 7.0);
        Task1.Movie m2 = new Task1.Movie("New", "Action", "English", LocalDate.of(2020, 1, 1), 100, 8.0);

        task.analyzeMovie(m1);
        task.analyzeMovie(m2);

        assertEquals("Old", Task1.oldest.title);
        assertEquals("New", Task1.newest.title);
    }

    @Test
    void testGenreAndLanguageCounts() {
        Task1.Movie m1 = new Task1.Movie("A", "Drama", "English", LocalDate.now(), 100, 8.0);
        Task1.Movie m2 = new Task1.Movie("B", "Drama", "French", LocalDate.now(), 90, 7.5);
        Task1.Movie m3 = new Task1.Movie("C", "Action", "English", LocalDate.now(), 110, 8.5);

        task.analyzeMovie(m1);
        task.analyzeMovie(m2);
        task.analyzeMovie(m3);

        Map<String, Integer> genres = Task1.genreCount;
        Map<String, Integer> languages = Task1.languageCount;

        assertEquals(2, genres.get("Drama"));
        assertEquals(1, genres.get("Action"));
        assertEquals(2, languages.get("English"));
        assertEquals(1, languages.get("French"));
    }

    @Test
    void testTop5Movies() {
        for (int i = 1; i <= 10; i++) {
            task.analyzeMovie(new Task1.Movie("Movie" + i, "Action", "English", LocalDate.now(), 100, i));
        }

        Queue<Task1.Movie> top5 = task.top5;

        assertEquals(5, top5.size());
        assertTrue(top5.stream().allMatch(m -> m.imdb > 5)); // в топе должны остаться фильмы с рейтингом 6–10
    }

    @Test
    void testTotalMinutesAccumulation() {
        Task1.Movie m1 = new Task1.Movie("A", "Drama", "English", LocalDate.now(), 100, 7.0);
        Task1.Movie m2 = new Task1.Movie("B", "Drama", "English", LocalDate.now(), 200, 8.0);

        task.analyzeMovie(m1);
        task.analyzeMovie(m2);

        assertEquals(300L, Task1.totalMinutes);
    }
}