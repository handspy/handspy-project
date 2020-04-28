package pt.up.hs.project.service.mapper;

import pt.up.hs.project.domain.*;
import pt.up.hs.project.service.dto.TaskDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class, LabelMapper.class})
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {

    TaskDTO toDto(Task task);

    @Mapping(target = "removeLabels", ignore = true)
    Task toEntity(TaskDTO taskDTO);

    @AfterMapping
    default void setLabelParent(@MappingTarget Task task) {
        for (Label label : task.getLabels()) {
            label.addTasks(task);
        }
    }

    default Task fromId(Long id) {
        if (id == null) {
            return null;
        }
        Task task = new Task();
        task.setId(id);
        return task;
    }
}
