package pt.up.hs.project.repository;

import pt.up.hs.project.domain.Participant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Participant entity.
 */
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long>, JpaSpecificationExecutor<Participant> {

    @Query(value = "select distinct participant from Participant participant left join fetch participant.labels",
        countQuery = "select count(distinct participant) from Participant participant")
    Page<Participant> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct participant from Participant participant left join fetch participant.labels")
    List<Participant> findAllWithEagerRelationships();

    @Query("select participant from Participant participant left join fetch participant.labels where participant.id =:id")
    Optional<Participant> findOneWithEagerRelationships(@Param("id") Long id);

}
