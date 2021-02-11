package pt.up.hs.project.web.rest.vm;

public class ProjectCopyPayload {

    private boolean copyPermissions = false;

    private boolean move = false;

    public ProjectCopyPayload() {
    }

    public ProjectCopyPayload(boolean copyPermissions, boolean move) {
        this.copyPermissions = copyPermissions;
        this.move = move;
    }

    public boolean isCopyPermissions() {
        return copyPermissions;
    }

    public void setCopyPermissions(boolean copyPermissions) {
        this.copyPermissions = copyPermissions;
    }

    public boolean isMove() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }
}
