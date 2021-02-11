package pt.up.hs.project.cloner;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pt.up.hs.project.service.ParticipantService;
import pt.up.hs.project.service.dto.ParticipantDTO;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pt.up.hs.project.cloner.ClonerConstants.*;

@Component
@StepScope
public class ParticipantItemWriter implements ItemWriter<Long> {

    private final ParticipantService participantService;

    private final Long projectId;
    private final Long newProjectId;
    private final boolean move;

    private StepExecution stepExecution;

    public ParticipantItemWriter(
        final ParticipantService participantService,
        @Value("#{jobParameters[" + PROJECT_ID_PARAMETER + "]}") final Long projectId,
        @Value("#{jobParameters[" + NEW_PROJECT_ID_PARAMETER + "]}") final Long newProjectId,
        @Value("#{jobParameters[" + MOVE_PARAMETER + "]}") final boolean move
    ) {
        this.participantService = participantService;
        this.projectId = projectId;
        this.newProjectId = newProjectId;
        this.move = move;
    }

    @BeforeStep
    public void beforeStep(final StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void write(@Nonnull List<? extends Long> items) throws Exception {
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();

        Map<Long, Long> labels;
        if (executionContext.containsKey(LABEL_MAPPING_PARAMETER)) {
            labels = (Map<Long, Long>) executionContext.get(LABEL_MAPPING_PARAMETER);
        } else {
            labels = new HashMap<>();
        }

        Map<Long, Long> participants;
        if (executionContext.containsKey(PARTICIPANT_MAPPING_PARAMETER)) {
            participants = (Map<Long, Long>) executionContext.get(PARTICIPANT_MAPPING_PARAMETER);
        } else {
            participants = new HashMap<>();
        }

        for (Long participantId: items) {
            ParticipantDTO savedParticipant = participantService.copy(
                projectId,
                participantId,
                newProjectId,
                move,
                labels
            );
            participants.put(participantId, savedParticipant.getId());
        }
        executionContext.put(PARTICIPANT_MAPPING_PARAMETER, participants);
    }
}
