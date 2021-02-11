package pt.up.hs.project.web.rest.vm;

import java.util.HashMap;
import java.util.Map;

public class TaskCopyPayload {
    private Long projectId;

    private boolean move = false;

    private Map<Long, Long> labelMapping = new HashMap<>();

    public TaskCopyPayload() {
    }

    public TaskCopyPayload(Long projectId, boolean move, Map<Long, Long> labelMapping) {
        this.projectId = projectId;
        this.move = move;
        this.labelMapping = labelMapping;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public boolean isMove() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    public Map<Long, Long> getLabelMapping() {
        return labelMapping;
    }

    public void setLabelMapping(Map<Long, Long> labelMapping) {
        this.labelMapping = labelMapping;
    }
}
