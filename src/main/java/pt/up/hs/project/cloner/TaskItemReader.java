package pt.up.hs.project.cloner;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.up.hs.project.service.LabelService;
import pt.up.hs.project.service.TaskService;
import pt.up.hs.project.service.dto.LabelDTO;
import pt.up.hs.project.service.dto.TaskBasicDTO;
import pt.up.hs.project.service.dto.TaskDTO;

import java.util.Iterator;

import static pt.up.hs.project.cloner.ClonerConstants.PROJECT_ID_PARAMETER;

@Component
@StepScope
public class TaskItemReader implements ItemReader<Long> {

    private final TaskService taskService;
    private final Long projectId;

    private ItemReader<Long> delegate = null;

    public TaskItemReader(
        final TaskService taskService,
        @Value("#{jobParameters[" + PROJECT_ID_PARAMETER + "]}") final Long projectId
    ) {
        this.taskService = taskService;
        this.projectId = projectId;
    }

    @Override
    public Long read() throws Exception {
        if (delegate == null) {
            delegate = new IteratorItemReader<>(taskIdsIterator());
        }
        return delegate.read();
    }

    public Iterator<Long> taskIdsIterator() {
        return taskService.findAllBasic(projectId)
            .parallelStream()
            .map(TaskBasicDTO::getId)
            .iterator();
    }
}
