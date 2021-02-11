package pt.up.hs.project.cloner;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.up.hs.project.service.ParticipantService;
import pt.up.hs.project.service.TaskService;
import pt.up.hs.project.service.dto.ParticipantBasicDTO;
import pt.up.hs.project.service.dto.TaskBasicDTO;

import java.util.Iterator;

import static pt.up.hs.project.cloner.ClonerConstants.PROJECT_ID_PARAMETER;

@Component
@StepScope
public class ParticipantItemReader implements ItemReader<Long> {

    private final ParticipantService participantService;
    private final Long projectId;

    private ItemReader<Long> delegate = null;

    public ParticipantItemReader(
        final ParticipantService participantService,
        @Value("#{jobParameters[" + PROJECT_ID_PARAMETER + "]}") final Long projectId
    ) {
        this.participantService = participantService;
        this.projectId = projectId;
    }

    @Override
    public Long read() throws Exception {
        if (delegate == null) {
            delegate = new IteratorItemReader<>(participantIdsIterator());
        }
        return delegate.read();
    }

    public Iterator<Long> participantIdsIterator() {
        return participantService.findAllBasic(projectId)
            .parallelStream()
            .map(ParticipantBasicDTO::getId)
            .iterator();
    }
}
