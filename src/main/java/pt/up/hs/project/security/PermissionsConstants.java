package pt.up.hs.project.security;

/**
 * Constants for Project permissions.
 */
public class PermissionsConstants {

    public static final String READ = "READ";
    public static final String WRITE = "WRITE";
    public static final String MANAGE = "MANAGE";
    public static final String ADMIN = "ADMIN";

    public static final String[] ALL =  new String[] {
            READ, WRITE, MANAGE, ADMIN
    };

    private PermissionsConstants() {
    }
}
