package pt.up.hs.project.utils;

import pt.up.hs.project.domain.enumeration.HandwritingMean;

public class HandwritingMeans {

    public static HandwritingMean fromString(String meansStr) {
        if (meansStr == null) {
            return null;
        }
        if (meansStr.matches("(?i:L|LEFT|LEFT[\\s_.-]HAND)")) {
            return HandwritingMean.LEFT_HAND;
        } else if (meansStr.matches("(?i:R|RIGHT|RIGHT[\\s_.-]HAND)")) {
            return HandwritingMean.RIGHT_HAND;
        }
        return HandwritingMean.OTHER;
    }
}
