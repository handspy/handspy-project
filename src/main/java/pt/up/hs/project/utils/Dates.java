package pt.up.hs.project.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Dates {

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        return dateToConvert.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }
}
