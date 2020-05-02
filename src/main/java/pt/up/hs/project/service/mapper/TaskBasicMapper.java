package pt.up.hs.project.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pt.up.hs.project.domain.Label;
import pt.up.hs.project.domain.Task;
import pt.up.hs.project.service.dto.TaskBasicDTO;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskBasicDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class, LabelMapper.class})
public interface TaskBasicMapper extends EntityMapper<TaskBasicDTO, Task> {

    @Mapping(source = "labels", target = "labelIds", qualifiedByName = "labelsToIds")
    TaskBasicDTO toDto(Task task);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "removeLabels", ignore = true)
    @Mapping(source = "labelIds", target = "labels", qualifiedByName = "idsToLabels")
    Task toEntity(TaskBasicDTO taskBasicDTO);

    default Task fromId(Long id) {
        if (id == null) {
            return null;
        }
        Task task = new Task();
        task.setId(id);
        return task;
    }

    @Named("labelsToIds")
    default Set<Long> labelsToIds(Set<Label> labels) {
        return labels.parallelStream()
            .map(Label::getId)
            .collect(Collectors.toSet());
    }

    @Named("idsToLabels")
    default Set<Label> idsToLabels(Set<Long> labelIds) {
        return labelIds.parallelStream()
            .map(id -> {
                if (id == null) {
                    return null;
                }
                Label label = new Label();
                label.setId(id);
                return label;
            })
            .collect(Collectors.toSet());
    }
}
