package pt.up.hs.project.client.sampling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import pt.up.hs.project.client.sampling.dto.CopyPayload;

import java.util.Map;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SamplingMicroService {

    private final Logger log = LoggerFactory.getLogger(SamplingMicroService.class);

    private final SamplingFeignClient samplingFeignClient;

    @Autowired
    public SamplingMicroService(
        SamplingFeignClient samplingFeignClient
    ) {
        this.samplingFeignClient = samplingFeignClient;
    }

    public void bulkCopyProtocols(
        Long projectId, boolean move, Long toProjectId,
        Map<Long, Long> tasks, Map<Long, Long> participants
    ) {
        log.info("Bulk copy protocols for " + projectId);
        samplingFeignClient.bulkCopyProtocols(projectId, new Long[0], new CopyPayload(toProjectId, move, tasks, participants));
    }

    public void bulkCopyTexts(
        Long projectId, boolean move, Long toProjectId,
        Map<Long, Long> tasks, Map<Long, Long> participants
    ) {
        log.info("Bulk copy texts for " + projectId);
        samplingFeignClient.bulkCopyTexts(projectId, new Long[0], new CopyPayload(toProjectId, move, tasks, participants));
    }
}
