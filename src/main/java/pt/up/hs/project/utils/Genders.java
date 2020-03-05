package pt.up.hs.project.utils;

import pt.up.hs.project.domain.enumeration.Gender;

public class Genders {

    public static Gender fromString(String genderStr) {
        if (genderStr == null) {
            return null;
        }
        if (genderStr.matches("^(?i:M|MALE)$")) {
            return Gender.MALE;
        } else if (genderStr.matches("^(?i:F|FEMALE)$")) {
            return Gender.FEMALE;
        }
        return Gender.OTHER;
    }
}
