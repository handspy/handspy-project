package pt.up.hs.project.service.importer.converters;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter extends AbstractBeanField<LocalDate, String> {

    @Override
    protected LocalDate convert(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd][dd-MM-yyyy][MM/dd/yyyy][yyyy/MM/dd]");
        return LocalDate.parse(s, formatter);
    }
}
