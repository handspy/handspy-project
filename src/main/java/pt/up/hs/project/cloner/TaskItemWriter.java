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
import pt.up.hs.project.service.LabelService;
import pt.up.hs.project.service.TaskService;
import pt.up.hs.project.service.dto.LabelDTO;
import pt.up.hs.project.service.dto.TaskDTO;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pt.up.hs.project.cloner.ClonerConstants.*;

@Component
@StepScope
public class TaskItemWriter implements ItemWriter<Long> {

    private final TaskService taskService;

    private final Long projectId;
    private final Long newProjectId;
    private final boolean move;

    private StepExecution stepExecution;

    public TaskItemWriter(
        final TaskService taskService,
        @Value("#{jobParameters[" + PROJECT_ID_PARAMETER + "]}") final Long projectId,
        @Value("#{jobParameters[" + NEW_PROJECT_ID_PARAMETER + "]}") final Long newProjectId,
        @Value("#{jobParameters[" + MOVE_PARAMETER + "]}") final boolean move
    ) {
        this.taskService = taskService;
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

        Map<Long, Long> tasks;
        if (executionContext.containsKey(TASK_MAPPING_PARAMETER)) {
            tasks = (Map<Long, Long>) executionContext.get(TASK_MAPPING_PARAMETER);
        } else {
            tasks = new HashMap<>();
        }

        for (Long taskId: items) {
            TaskDTO savedTask = taskService.copy(
                projectId,
                taskId,
                newProjectId,
                move,
                labels
            );
            tasks.put(taskId, savedTask.getId());
        }
        executionContext.put(TASK_MAPPING_PARAMETER, tasks);
    }
}
