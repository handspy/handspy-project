package pt.up.hs.project.service.importer.batch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class ImportLoggingProcessor<T> implements ItemProcessor<T, T> {

    private final Logger log = LoggerFactory.getLogger(ImportLoggingProcessor.class);

    @Override
    public T process(T item) {
        log.info("Processing import of information: {}", item);
        return item;
    }
}
