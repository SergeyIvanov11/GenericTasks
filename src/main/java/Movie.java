import java.time.LocalDate;

public class Movie {
    String title;
    String genre;
    String language;
    LocalDate premiere;
    int runtimeMinutes;
    double imdb;

    public Movie(String title, String genre, String language, LocalDate premiere, int runtimeMinutes, double imdb) {
        this.title = title;
        this.genre = genre;
        this.language = language;
        this.premiere = premiere;
        this.runtimeMinutes = runtimeMinutes;
        this.imdb = imdb;
    }
}
