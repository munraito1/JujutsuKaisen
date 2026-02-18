package enums;

public enum Grade {
    GRADE_4(4, "Grade 4"),
    GRADE_3(3, "Grade 3"),
    GRADE_2(2, "Grade 2"),
    GRADE_1(1, "Grade 1"),
    SPECIAL_GRADE(0, "Special Grade");

    private final int rank;
    private final String displayName;

    Grade(int rank, String displayName) {
        this.rank = rank;
        this.displayName = displayName;
    }

    public int getRank() {
        return rank;
    }

    public String getDisplayName() {
        return displayName;
    }
}
