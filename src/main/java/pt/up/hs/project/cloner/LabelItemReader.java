package pt.up.hs.project.cloner;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.up.hs.project.service.LabelService;
import pt.up.hs.project.service.dto.LabelDTO;

import java.util.Iterator;
import java.util.List;

import static pt.up.hs.project.cloner.ClonerConstants.PROJECT_ID_PARAMETER;

@Component
@StepScope
public class LabelItemReader implements ItemReader<Long> {

    private final LabelService labelService;
    private final Long projectId;

    private ItemReader<Long> delegate = null;

    public LabelItemReader(
        final LabelService labelService,
        @Value("#{jobParameters[" + PROJECT_ID_PARAMETER + "]}") final Long projectId
    ) {
        this.labelService = labelService;
        this.projectId = projectId;
    }

    @Override
    public Long read() throws Exception {
        if (delegate == null) {
            delegate = new IteratorItemReader<>(labelIdsIterator());
        }
        return delegate.read();
    }

    public Iterator<Long> labelIdsIterator() {
        return labelService.findAll(projectId)
            .parallelStream()
            .map(LabelDTO::getId)
            .iterator();
    }
}
