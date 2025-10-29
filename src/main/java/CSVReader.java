import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class CSVReader {
    public List<String> readCSVFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(path, java.nio.charset.StandardCharsets.ISO_8859_1)) {
            return lines.skip(1)// пропускаем заголовок
                    .toList();
        }
    }
}
