package pt.up.hs.project.web.rest.vm;

import java.util.HashMap;
import java.util.Map;

public class LabelCopyPayload {
    private Long projectId;

    private boolean move = false;

    public LabelCopyPayload() {
    }

    public LabelCopyPayload(Long projectId, boolean move) {
        this.projectId = projectId;
        this.move = move;
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

}
