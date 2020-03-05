package pt.up.hs.project.service.importer.exception;

/**
 * Exception thrown when an nonrecoverable exception occurs while reading CSV.
 */
public class CsvReaderException extends RuntimeException {

    public CsvReaderException() {
    }

    public CsvReaderException(String message) {
        super(message);
    }

    public CsvReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvReaderException(Throwable cause) {
        super(cause);
    }
}
