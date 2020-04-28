package pt.up.hs.project.web.rest.errors;

import org.zalando.problem.Status;

import java.net.URI;

public class InternalServerException extends ProblemWithMessageException {
    private static final long serialVersionUID = 1L;

    public InternalServerException(String defaultMessage, String entityName, String errorKey) {
        super(defaultMessage, entityName, errorKey);
    }

    public InternalServerException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(type, Status.INTERNAL_SERVER_ERROR, defaultMessage, entityName, errorKey);
    }
}
