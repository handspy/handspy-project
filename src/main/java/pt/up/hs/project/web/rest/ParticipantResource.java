package pt.up.hs.project.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.up.hs.project.constants.EntityNames;
import pt.up.hs.project.constants.ErrorKeys;
import pt.up.hs.project.service.ParticipantService;
import pt.up.hs.project.service.dto.BulkImportResultDTO;
import pt.up.hs.project.service.dto.ParticipantBasicDTO;
import pt.up.hs.project.service.dto.ParticipantDTO;
import pt.up.hs.project.service.dto.TaskDTO;
import pt.up.hs.project.web.rest.errors.BadRequestException;
import pt.up.hs.project.web.rest.vm.ParticipantCopyPayload;
import pt.up.hs.project.web.rest.vm.TaskCopyPayload;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link pt.up.hs.project.domain.Participant}.
 */
@RestController
@RequestMapping("/api/projects/{projectId}")
public class ParticipantResource {

    private final Logger log = LoggerFactory.getLogger(ParticipantResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParticipantService participantService;

    public ParticipantResource(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * {@code POST  /participants} : Create a new participant.
     *
     * @param projectId      ID of the project to which the participant will belong.
     * @param participantDTO the participantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new participantDTO, or with status {@code 400 (Bad Request)} if the participant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/participants")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'WRITE')")
    public ResponseEntity<ParticipantDTO> createParticipant(
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody ParticipantDTO participantDTO
    ) throws URISyntaxException {
        log.debug("REST request to create Participant {} in project {}", participantDTO, projectId);
        if (participantDTO.getId() != null) {
            throw new BadRequestException("A new participant cannot already have an ID", EntityNames.PARTICIPANT, ErrorKeys.ERR_ID_EXISTS);
        }
        participantDTO.setProjectId(projectId);
        ParticipantDTO result = participantService.save(projectId, participantDTO);
        return ResponseEntity.created(new URI("/api/projects/" + projectId + "/participants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, EntityNames.PARTICIPANT, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /participants} : Updates an existing participant.
     *
     * @param projectId      ID of the project to which this participant belongs.
     * @param participantDTO the participantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participantDTO,
     * or with status {@code 400 (Bad Request)} if the participantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the participantDTO couldn't be updated.
     */
    @PutMapping("/participants")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'WRITE')")
    public ResponseEntity<ParticipantDTO> updateParticipant(
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody ParticipantDTO participantDTO
    ) {
        log.debug("REST request to update Participant {} in project {}", participantDTO, projectId);
        if (participantDTO.getId() == null) {
            throw new BadRequestException("Invalid id", EntityNames.PARTICIPANT, ErrorKeys.ERR_ID_NULL);
        }
        ParticipantDTO result = participantService.save(projectId, participantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, EntityNames.PARTICIPANT, participantDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /participants} : get all the participants.
     *
     * @param projectId ID of the project to which this participants belong.
     * @param search the search string.
     * @param labels the ids of the labels to filter by.
     * @param pageable  the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of participants in body.
     */
    @GetMapping("/participants")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'READ')")
    public ResponseEntity<List<ParticipantDTO>> getAllParticipants(
        @PathVariable("projectId") Long projectId,
        @RequestParam(value = "search", required = false, defaultValue = "") String search,
        @RequestParam(value = "labels", required = false) List<Long> labels,
        Pageable pageable
    ) {
        log.debug("REST request to get Participants in project {}", projectId);
        Page<ParticipantDTO> page = participantService.findAll(projectId, search, labels, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /participants/basic} : get basic info of all the participants (for selectors).
     *
     * @param projectId ID of the project to which the participants belong.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of participants' basic info in body.
     */
    @GetMapping("/participants/basic")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and" +
        " hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'READ')")
    public ResponseEntity<List<ParticipantBasicDTO>> getAllParticipantsBasic(
        @PathVariable("projectId") Long projectId
    ) {
        log.debug("REST request to get Participants in project {}", projectId);
        List<ParticipantBasicDTO> participantDTOs = participantService.findAllBasic(projectId);
        return ResponseEntity.ok().body(participantDTOs);
    }

    /**
     * {@code GET  /participants/count} : count all the participants.
     *
     * @param projectId ID of the project to which this participants belong.
     * @param search the search string.
     * @param labels the ids of the labels to filter by.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/participants/count")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'READ')")
    public ResponseEntity<Long> countParticipants(
        @PathVariable("projectId") Long projectId,
        @RequestParam(value = "search", required = false, defaultValue = "") String search,
        @RequestParam(value = "labels", required = false) List<Long> labels
    ) {
        log.debug("REST request to count Participants in project {}", projectId);
        return ResponseEntity.ok().body(participantService.count(projectId, search, labels));
    }

    /**
     * {@code GET  /participants/:id} : get the "id" participant.
     *
     * @param projectId ID of the project to which this participant belongs.
     * @param id        the id of the participantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with
     * body the participantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/participants/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'READ')")
    public ResponseEntity<ParticipantDTO> getParticipant(
        @PathVariable("projectId") Long projectId,
        @PathVariable Long id
    ) {
        log.debug("REST request to get Participant {} in project {}", id, projectId);
        Optional<ParticipantDTO> participantDTO = participantService.findOne(projectId, id);
        return ResponseUtil.wrapOrNotFound(participantDTO);
    }

    /**
     * {@code POST /participants/import} : import CSV sent in body.
     *
     * @param projectId ID of the project to which this participant belongs.
     * @param sep       column separator.
     * @param arraySep  separator for array elements.
     * @param useHeader does the CSV include an header?
     * @param is        {@link InputStream} input stream of the request body.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with
     * body the {@link BulkImportResultDTO}.
     */
    @PostMapping(value = "/participants/import", consumes = "text/csv")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'WRITE')")
    public ResponseEntity<BulkImportResultDTO<ParticipantDTO>> importSimple(
        @PathVariable("projectId") Long projectId,
        @RequestParam(value = "sep", defaultValue = ",") String sep,
        @RequestParam(value = "array-sep", defaultValue = ";") String arraySep,
        @RequestParam(value = "use-header", defaultValue = "true") boolean useHeader,
        @RequestBody InputStream is
    ) {
        log.debug("REST request to import Participants from CSV sent in body in project {}", projectId);
        return ResponseEntity.ok(participantService.importFromCsv(projectId, is, sep, arraySep, useHeader));
    }

    /**
     * {@code POST /participants/import} : import CSV sent in multipart/form-data.
     *
     * @param projectId ID of the project to which this participant belongs.
     * @param sep       column separator.
     * @param arraySep  separator for array elements.
     * @param useHeader does the CSV include an header?
     * @param file      {@link MultipartFile} file from multipart/form-data.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with
     * body the {@link BulkImportResultDTO}.
     */
    @PostMapping(value = "/participants/import", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'WRITE')")
    public ResponseEntity<BulkImportResultDTO<ParticipantDTO>> importMultipart(
        @PathVariable("projectId") Long projectId,
        @RequestParam(value = "sep", defaultValue = ",") String sep,
        @RequestParam(value = "array-sep", defaultValue = ";") String arraySep,
        @RequestParam(value = "use-header", defaultValue = "true") boolean useHeader,
        @RequestParam("file") MultipartFile file
    ) throws IOException {
        log.debug("REST request to import Participants from CSV sent in multipart/form-data in project {}", projectId);
        return ResponseEntity.ok(
            participantService.importFromCsv(projectId, file.getInputStream(), sep, arraySep, useHeader)
        );
    }

    /**
     * {@code DELETE  /participants/:id} : delete the "id" participant.
     *
     * @param projectId ID of the project to which this participant belongs.
     * @param id        the id of the participantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/participants/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'MANAGE')")
    public ResponseEntity<Void> deleteParticipant(
        @PathVariable("projectId") Long projectId,
        @PathVariable Long id
    ) {
        log.debug("REST request to delete Participant {} in project {}", id, projectId);
        participantService.delete(projectId, id);
        return ResponseEntity.noContent().headers(
            HeaderUtil.createEntityDeletionAlert(applicationName, true, EntityNames.PARTICIPANT, id.toString())
        ).build();
    }

    @PostMapping("/participants/{id}/copy")
    @PreAuthorize(
        "hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
            "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'READ') and " +
            "(not payload.move or hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'MANAGE')) and " +
            "hasPermission(#payload.projectId, 'pt.up.hs.project.domain.Project', 'WRITE')"
    )
    public ResponseEntity<ParticipantDTO> copy(
        @PathVariable("projectId") Long projectId,
        @PathVariable("id") Long id,
        @Valid @RequestBody ParticipantCopyPayload payload
    ) throws URISyntaxException {
        log.debug("REST request to copy Participant {} from project {} to project {}", id, projectId, payload.getProjectId());
        ParticipantDTO result = participantService.copy(projectId, id, payload.getProjectId(), payload.isMove(), payload.getLabelMapping());
        return ResponseEntity
            .created(new URI("/api/projects/" + payload.getProjectId() + "/participants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, EntityNames.PARTICIPANT, result.getId().toString()))
            .body(result);
    }
}
