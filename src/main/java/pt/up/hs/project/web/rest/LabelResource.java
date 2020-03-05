package pt.up.hs.project.web.rest;

import pt.up.hs.project.service.LabelService;
import pt.up.hs.project.web.rest.errors.BadRequestAlertException;
import pt.up.hs.project.service.dto.LabelDTO;
import pt.up.hs.project.service.dto.LabelCriteria;
import pt.up.hs.project.service.LabelQueryService;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link pt.up.hs.project.domain.Label}.
 */
@RestController
@RequestMapping("/api/projects/{projectId}")
public class LabelResource {

    private final Logger log = LoggerFactory.getLogger(LabelResource.class);

    private static final String ENTITY_NAME = "projectLabel";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LabelService labelService;
    private final LabelQueryService labelQueryService;

    public LabelResource(LabelService labelService, LabelQueryService labelQueryService) {
        this.labelService = labelService;
        this.labelQueryService = labelQueryService;
    }

    /**
     * {@code POST  /labels} : Create a new label.
     *
     * @param projectId ID of the project to which the label will belong.
     * @param labelDTO  the labelDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new labelDTO,
     * or with status {@code 400 (Bad Request)} if the label has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/labels")
    public ResponseEntity<LabelDTO> createLabel(
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody LabelDTO labelDTO
    ) throws URISyntaxException {
        log.debug("REST request to save Label {} in project {}", labelDTO, projectId);
        if (labelDTO.getId() != null) {
            throw new BadRequestAlertException("A new label cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LabelDTO result = labelService.save(projectId, labelDTO);
        return ResponseEntity.created(new URI("/api/projects/" + projectId + "/labels/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /labels} : Updates an existing label.
     *
     * @param projectId ID of the project to which the label belongs.
     * @param labelDTO  the labelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated labelDTO,
     * or with status {@code 400 (Bad Request)} if the labelDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the labelDTO couldn't be updated.
     */
    @PutMapping("/labels")
    public ResponseEntity<LabelDTO> updateLabel(
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody LabelDTO labelDTO
    ) {
        log.debug("REST request to update Label {} in project {}", labelDTO, projectId);
        if (labelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LabelDTO result = labelService.save(projectId, labelDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, labelDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /labels} : get all the labels.
     *
     * @param projectId ID of the project to which the labels belong.
     * @param pageable  the pagination information.
     * @param criteria  the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of labels in body.
     */
    @GetMapping("/labels")
    public ResponseEntity<List<LabelDTO>> getAllLabels(
        @PathVariable("projectId") Long projectId,
        LabelCriteria criteria, Pageable pageable
    ) {
        log.debug("REST request to get Labels by criteria {} in project {}", criteria, projectId);
        Page<LabelDTO> page = labelQueryService.findByCriteria(projectId, criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /labels/count} : count all the labels.
     *
     * @param projectId ID of the project to which the labels belong.
     * @param criteria  the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/labels/count")
    public ResponseEntity<Long> countLabels(
        @PathVariable("projectId") Long projectId,
        LabelCriteria criteria
    ) {
        log.debug("REST request to count Labels by criteria {} in project {}", criteria, projectId);
        return ResponseEntity.ok().body(labelQueryService.countByCriteria(projectId, criteria));
    }

    /**
     * {@code GET  /labels/:id} : get the "id" label.
     *
     * @param projectId ID of the project to which the label belongs.
     * @param id        the id of the labelDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the labelDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/labels/{id}")
    public ResponseEntity<LabelDTO> getLabel(
        @PathVariable("projectId") Long projectId,
        @PathVariable Long id
    ) {
        log.debug("REST request to get Label {} in project {}", id, projectId);
        Optional<LabelDTO> labelDTO = labelService.findOne(projectId, id);
        return ResponseUtil.wrapOrNotFound(labelDTO);
    }

    /**
     * {@code DELETE  /labels/:id} : delete the "id" label.
     *
     * @param projectId ID of the project to which the label belongs.
     * @param id        the id of the labelDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/labels/{id}")
    public ResponseEntity<Void> deleteLabel(
        @PathVariable("projectId") Long projectId,
        @PathVariable Long id
    ) {
        log.debug("REST request to delete Label {} in project {}", id, projectId);
        labelService.delete(projectId, id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
