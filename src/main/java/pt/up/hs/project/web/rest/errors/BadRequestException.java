package pt.up.hs.project.web.rest.errors;

import org.zalando.problem.Status;

import java.net.URI;

public class BadRequestException extends ProblemWithMessageException {
    private static final long serialVersionUID = 1L;

    public BadRequestException(String defaultMessage, String entityName, String errorKey) {
        super(defaultMessage, entityName, errorKey);
    }

    public BadRequestException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(type, Status.BAD_REQUEST, defaultMessage, entityName, errorKey);
    }
}
