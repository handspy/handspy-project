package pt.up.hs.project.client.sampling.dto;

import java.util.HashMap;
import java.util.Map;

public class CopyPayload {
    private Long projectId;

    private boolean move = false;

    private Map<Long, Long> taskMapping = new HashMap<>();
    private Map<Long, Long> participantMapping = new HashMap<>();

    public CopyPayload() {
    }

    public CopyPayload(Long projectId, boolean move, Map<Long, Long> taskMapping, Map<Long, Long> participantMapping) {
        this.projectId = projectId;
        this.move = move;
        this.taskMapping = taskMapping;
        this.participantMapping = participantMapping;
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

    public Map<Long, Long> getTaskMapping() {
        return taskMapping;
    }

    public void setTaskMapping(Map<Long, Long> taskMapping) {
        this.taskMapping = taskMapping;
    }

    public Map<Long, Long> getParticipantMapping() {
        return participantMapping;
    }

    public void setParticipantMapping(Map<Long, Long> participantMapping) {
        this.participantMapping = participantMapping;
    }
}
