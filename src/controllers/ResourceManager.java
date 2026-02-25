package controllers;

public class ResourceManager {

    private int yuan;
    private int gradePoints;

    public ResourceManager(int startingYuan) {
        this.yuan = startingYuan;
        this.gradePoints = 0;
    }

    public int getYuan() { return yuan; }

    public void addYuan(int amount) {
        if (amount > 0) yuan += amount;
    }

    public boolean spendYuan(int amount) {
        if (yuan < amount) return false;
        yuan -= amount;
        return true;
    }

    public int getGradePoints() { return gradePoints; }

    public void addGradePoints(int amount) {
        if (amount > 0) gradePoints += amount;
    }

    public boolean spendGradePoints(int amount) {
        if (gradePoints < amount) return false;
        gradePoints -= amount;
        return true;
    }
}
