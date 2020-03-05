package pt.up.hs.project.service.importer.reader;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvException;
import pt.up.hs.project.service.importer.exception.CsvReaderException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

public class CsvReader<T> {

    private List<T> values;
    private List<CsvException> capturedExceptions;

    protected CsvReader(List<T> values, List<CsvException> capturedExceptions) {
        this.values = values;
        this.capturedExceptions = capturedExceptions;
    }

    public static <T> CsvReader<T> fromInputStream(
        final Class<T> beanClass, InputStream is, String sep, boolean useHeaders
    ) throws CsvReaderException {

        // parse CSV file to create a list of Participant objects
        try (Reader reader = new BufferedReader(new InputStreamReader(is))) {

            // create a mapping strategy
            MappingStrategy<T> strategy;
            if (useHeaders) {
                strategy = new HeaderColumnNameMappingStrategy<>();
            } else {
                strategy = new ColumnPositionMappingStrategy<>();
            }
            strategy.setType(beanClass);

            // create csv bean reader
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                .withMappingStrategy(strategy)
                .withSeparator(sep.charAt(0))
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .withOrderedResults(false)
                .withThrowExceptions(false)
                .build();

            return new CsvReader<>(
                csvToBean.parse(),
                csvToBean.getCapturedExceptions()
            );
        } catch (Exception e) {
            throw new CsvReaderException(e);
        }
    }

    public Iterator<T> iterator() {
        return values.iterator();
    }

    public List<T> getAll() {
        return values;
    }

    public List<CsvException> getCapturedExceptions() {
        return capturedExceptions;
    }

    public int[] getExceptionLines() {
        return capturedExceptions.stream()
            .mapToInt(e -> (int) e.getLineNumber())
            .toArray();
    }
}
