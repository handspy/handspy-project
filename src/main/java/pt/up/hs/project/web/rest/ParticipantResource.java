package pt.up.hs.project.web.rest;

import org.springframework.web.multipart.MultipartFile;
import pt.up.hs.project.service.ParticipantService;
import pt.up.hs.project.service.dto.BulkImportResultDTO;
import pt.up.hs.project.web.rest.errors.BadRequestAlertException;
import pt.up.hs.project.service.dto.ParticipantDTO;
import pt.up.hs.project.service.dto.ParticipantCriteria;
import pt.up.hs.project.service.ParticipantQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private static final String ENTITY_NAME = "projectParticipant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParticipantService participantService;

    private final ParticipantQueryService participantQueryService;

    public ParticipantResource(ParticipantService participantService, ParticipantQueryService participantQueryService) {
        this.participantService = participantService;
        this.participantQueryService = participantQueryService;
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
    public ResponseEntity<ParticipantDTO> createParticipant(
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody ParticipantDTO participantDTO
    ) throws URISyntaxException {
        log.debug("REST request to create Participant {} in project {}", participantDTO, projectId);
        if (participantDTO.getId() != null) {
            throw new BadRequestAlertException("A new participant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        participantDTO.setProjectId(projectId);
        ParticipantDTO result = participantService.save(projectId, participantDTO);
        return ResponseEntity.created(new URI("/api/projects/" + projectId + "/participants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
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
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/participants")
    public ResponseEntity<ParticipantDTO> updateParticipant(
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody ParticipantDTO participantDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Participant {} in project {}", participantDTO, projectId);
        if (participantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ParticipantDTO result = participantService.save(projectId, participantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participantDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /participants} : get all the participants.
     *
     * @param projectId ID of the project to which this participants belong.
     * @param pageable  the pagination information.
     * @param criteria  the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of participants in body.
     */
    @GetMapping("/participants")
    public ResponseEntity<List<ParticipantDTO>> getAllParticipants(
        @PathVariable("projectId") Long projectId,
        ParticipantCriteria criteria, Pageable pageable
    ) {
        log.debug("REST request to get Participants by criteria {} in project {}", criteria, projectId);
        Page<ParticipantDTO> page = participantQueryService.findByCriteria(projectId, criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /participants/count} : count all the participants.
     *
     * @param projectId ID of the project to which this participants belong.
     * @param criteria  the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/participants/count")
    public ResponseEntity<Long> countParticipants(
        @PathVariable("projectId") Long projectId,
        ParticipantCriteria criteria
    ) {
        log.debug("REST request to count Participants by criteria {} in project {}", criteria, projectId);
        return ResponseEntity.ok().body(participantQueryService.countByCriteria(projectId, criteria));
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
    public ResponseEntity<Void> deleteParticipant(
        @PathVariable("projectId") Long projectId,
        @PathVariable Long id
    ) {
        log.debug("REST request to delete Participant {} in project {}", id, projectId);
        participantService.delete(projectId, id);
        return ResponseEntity.noContent().headers(
            HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())
        ).build();
    }
}
