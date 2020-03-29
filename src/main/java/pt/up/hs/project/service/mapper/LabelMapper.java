package pt.up.hs.project.service.mapper;

import pt.up.hs.project.domain.*;
import pt.up.hs.project.service.dto.LabelDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Label} and its DTO {@link LabelDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class, TaskMapper.class})
public interface LabelMapper extends EntityMapper<LabelDTO, Label> {

    LabelDTO toDto(Label label);

    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "removeParticipants", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "removeTasks", ignore = true)
    Label toEntity(LabelDTO labelDTO);

    default Label fromId(Long id) {
        if (id == null) {
            return null;
        }
        Label label = new Label();
        label.setId(id);
        return label;
    }
}
