package pt.up.hs.project.service.mapper;


import pt.up.hs.project.domain.*;
import pt.up.hs.project.service.dto.ParticipantDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Participant} and its DTO {@link ParticipantDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class, LabelMapper.class})
public interface ParticipantMapper extends EntityMapper<ParticipantDTO, Participant> {

    ParticipantDTO toDto(Participant participant);

    @Mapping(target = "removeLabels", ignore = true)
    Participant toEntity(ParticipantDTO participantDTO);

    default Participant fromId(Long id) {
        if (id == null) {
            return null;
        }
        Participant participant = new Participant();
        participant.setId(id);
        return participant;
    }
}
