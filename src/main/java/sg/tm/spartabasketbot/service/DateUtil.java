package sg.tm.spartabasketbot.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String getCurrentDate() {
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.of("UTC+7"));

        Instant instant = Instant.now();
        return formatter.format(instant);
    }
}
