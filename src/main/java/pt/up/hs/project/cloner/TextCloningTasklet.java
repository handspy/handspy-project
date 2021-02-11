package pt.up.hs.project.cloner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.up.hs.project.client.sampling.SamplingMicroService;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static pt.up.hs.project.cloner.ClonerConstants.*;

@Component
@StepScope
public class TextCloningTasklet implements Tasklet, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(TextCloningTasklet.class);

    private SamplingMicroService samplingMicroService;

    @Value("#{jobParameters[" + PROJECT_ID_PARAMETER + "]}")
    Long projectId;

    @Value("#{jobParameters[" + NEW_PROJECT_ID_PARAMETER + "]}")
    Long newProjectId;

    @Value("#{jobParameters[" + MOVE_PARAMETER + "]}")
    String move;

    public SamplingMicroService getSamplingMicroService() {
        return samplingMicroService;
    }

    @Autowired
    public void setSamplingMicroService(SamplingMicroService samplingMicroService) {
        this.samplingMicroService = samplingMicroService;
    }

    @Override
    public RepeatStatus execute(
        @Nonnull StepContribution contribution,
        @Nonnull ChunkContext chunkContext
    ) {
        Map<String, Object> executionContext = chunkContext
            .getStepContext()
            .getJobExecutionContext();

        Map<Long, Long> tasks = executionContext.containsKey(TASK_MAPPING_PARAMETER)
            ? (Map<Long, Long>) executionContext.get(TASK_MAPPING_PARAMETER)
            : new HashMap<>();

        Map<Long, Long> participants = executionContext.containsKey(PARTICIPANT_MAPPING_PARAMETER)
            ? (Map<Long, Long>) executionContext.get(PARTICIPANT_MAPPING_PARAMETER)
            : new HashMap<>();

        LOG.error("{} {}", participants.size(), tasks.size());

        samplingMicroService.bulkCopyTexts(projectId, Boolean.parseBoolean(move), newProjectId, tasks, participants);

        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() {
    }
}
