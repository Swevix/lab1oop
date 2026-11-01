public class Student {
    private final String name;
    private final int age;
    private final double averageGrade;
    private final boolean scholarship;

    public Student(String name, int age, double averageGrade, boolean scholarship) {
        this.name = name;
        this.age = age;
        this.averageGrade = averageGrade;
        this.scholarship = scholarship;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public double getAverageGrade() { return averageGrade; }
    public boolean hasScholarship() { return scholarship; }
}
