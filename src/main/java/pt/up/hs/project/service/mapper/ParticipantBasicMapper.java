package pt.up.hs.project.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pt.up.hs.project.domain.Label;
import pt.up.hs.project.domain.Participant;
import pt.up.hs.project.service.dto.ParticipantBasicDTO;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for the entity {@link Participant} and its DTO {@link ParticipantBasicDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class, LabelMapper.class})
public interface ParticipantBasicMapper extends EntityMapper<ParticipantBasicDTO, Participant> {

    @Mapping(source = "labels", target = "labelIds", qualifiedByName = "labelsToIds")
    ParticipantBasicDTO toDto(Participant participant);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "birthdate", ignore = true)
    @Mapping(target = "handedness", ignore = true)
    @Mapping(target = "additionalInfo", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "imageContentType", ignore = true)
    @Mapping(target = "removeLabels", ignore = true)
    @Mapping(source = "labelIds", target = "labels", qualifiedByName = "idsToLabels")
    Participant toEntity(ParticipantBasicDTO participantBasicDTO);

    default Participant fromId(Long id) {
        if (id == null) {
            return null;
        }
        Participant participant = new Participant();
        participant.setId(id);
        return participant;
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
