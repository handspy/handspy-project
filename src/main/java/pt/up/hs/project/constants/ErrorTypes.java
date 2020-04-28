package pt.up.hs.project.constants;

import java.net.URI;

public class ErrorTypes {

    public static final String PROBLEM_BASE_URL = "https://handspy.up.pt/faq/exceptions";

    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
}
