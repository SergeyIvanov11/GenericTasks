import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Task1Test {
    private MovieParser parser;
    private MovieAnalyser analyser;

    @BeforeEach
    void setup() {
        parser = new MovieParser();
        analyser = new MovieAnalyser();
    }

    // ---------- MovieParser ----------
    @Test
    void testParseCSVLine() {
        String line = "\"The Irishman\",Drama,\"November 1, 2019\",209 min,7.8,English";
        Movie m = parser.parseCSVLine(line);

        assertNotNull(m);
        assertEquals("The Irishman", m.title);
        assertEquals("Drama", m.genre);
        assertEquals("English", m.language);
        assertEquals(LocalDate.of(2019, 11, 1), m.premiere);
        assertEquals(209, m.runtimeMinutes);
        assertEquals(7.8, m.imdb);
    }

    @Test
    void testParseInvalidLine() {
        String invalid = "Incomplete,Data,Here";
        Movie m = parser.parseCSVLine(invalid);
        assertNull(m);
    }

    // ---------- MovieAnalyser ----------
    @Test
    void testGenreAndLanguageCount() {
        Movie m1 = new Movie("Film1", "Action", "English", LocalDate.of(2020, 5, 1), 120, 8.2);
        Movie m2 = new Movie("Film2", "Drama", "Spanish", LocalDate.of(2018, 6, 10), 90, 7.1);
        Movie m3 = new Movie("Film3", "Action", "English", LocalDate.of(2019, 3, 15), 110, 8.0);

        analyser.analyze(m1);
        analyser.analyze(m2);
        analyser.analyze(m3);

        assertEquals(2, analyser.genreCount.size());
        assertEquals(2, analyser.languageCount.size());
        assertEquals(2, analyser.genreCount.get("Action"));
        assertEquals(2, analyser.languageCount.get("English"));
    }

    @Test
    void testOldestAndNewest() {
        Movie m1 = new Movie("Old", "Drama", "English", LocalDate.of(2001, 1, 1), 100, 6.0);
        Movie m2 = new Movie("New", "Action", "English", LocalDate.of(2022, 12, 31), 120, 9.0);

        analyser.analyze(m1);
        analyser.analyze(m2);

        assertEquals("Old", analyser.oldest.title);
        assertEquals("New", analyser.newest.title);
    }

    @Test
    void testHighestLowestRated() {
        Movie m1 = new Movie("Good", "Drama", "English", LocalDate.of(2020, 1, 1), 100, 9.5);
        Movie m2 = new Movie("Bad", "Drama", "English", LocalDate.of(2020, 2, 1), 90, 3.2);

        analyser.analyze(m1);
        analyser.analyze(m2);

        assertEquals("Good", analyser.highestRated.get(2020).title);
        assertEquals("Bad", analyser.lowestRated.get(2020).title);
    }

    @Test
    void testTop5Movies() {
        for (int i = 1; i <= 10; i++) {
            Movie m = new Movie("Film" + i, "Genre", "English", LocalDate.of(2020, 1, i), 100, i); // imdb от 1 до 10
            analyser.analyze(m);
        }

        assertEquals(5, analyser.top5.size());
        assertTrue(analyser.top5.stream().allMatch(m -> m.imdb >= 6)); // top5 → imdb ≥ 6
    }

    @Test
    void testTotalRuntime() {
        Movie m1 = new Movie("Film1", "Drama", "English", LocalDate.of(2020, 1, 1), 100, 8.0);
        Movie m2 = new Movie("Film2", "Action", "English", LocalDate.of(2021, 1, 1), 150, 9.0);

        analyser.analyze(m1);
        analyser.analyze(m2);

        assertEquals(250, analyser.totalMinutes);
    }

    // ---------- CSVReader ----------
    @Test
    void testCSVReader() throws IOException {
        // создаём временный файл с CSV-данными
        java.nio.file.Path temp = java.nio.file.Files.createTempFile("movies", ".csv");
        java.nio.file.Files.write(temp, List.of("Title,Genre,Premiere,Runtime,IMDB,Language", "\"Movie1\",Action,\"January 1, 2020\",120 min,8.5,English"));

        CSVReader reader = new CSVReader();
        List<String> lines = reader.readCSVFile(temp.toString());

        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("Movie1"));
    }
}
