package pt.up.hs.project.client.sampling;

import org.springframework.web.bind.annotation.*;
import pt.up.hs.project.client.AuthorizedFeignClient;
import pt.up.hs.project.client.sampling.dto.CopyPayload;

@AuthorizedFeignClient(name = "sampling")
public interface SamplingFeignClient {

    @RequestMapping(value = "/api/projects/{projectId}/protocols/copy", method = RequestMethod.POST)
    void bulkCopyProtocols(
        @PathVariable("projectId") Long projectId,
        @RequestParam("ids") Long[] ids,
        @RequestBody CopyPayload payload
    );

    @RequestMapping(value = "/api/projects/{projectId}/texts/copy", method = RequestMethod.POST)
    void bulkCopyTexts(
        @PathVariable("projectId") Long projectId,
        @RequestParam("ids") Long[] ids,
        @RequestBody CopyPayload payload
    );
}
