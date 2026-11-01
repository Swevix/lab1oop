import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class StudentApp {
    private final Student[] students = createStudents();
    private final JFrame frame = new JFrame("ООП — Практическое задание 1");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"№", "Имя", "Возраст", "Средний балл", "Стипендия"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JLabel status = new JLabel("Исходный массив: 15 объектов Student");
    private final JTextArea filePreview = new JTextArea(5, 50);

    static Student[] createStudents() {
        return new Student[]{
            new Student("Мария", 20, 4.8, true),
            new Student("Алексей", 19, 3.7, false),
            new Student("София", 18, 4.9, true),
            new Student("Дмитрий", 22, 4.1, true),
            new Student("Анна", 19, 4.6, true),
            new Student("Иван", 21, 3.4, false),
            new Student("Елена", 20, 4.3, true),
            new Student("Максим", 18, 3.9, false),
            new Student("Ольга", 23, 4.7, true),
            new Student("Никита", 21, 3.2, false),
            new Student("Дарья", 19, 5.0, true),
            new Student("Павел", 22, 3.8, false),
            new Student("Виктория", 20, 4.5, true),
            new Student("Артём", 18, 4.2, true),
            new Student("Ксения", 21, 4.4, true)
        };
    }

    static Comparator<Student> byName() {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("ru"));
        return (a, b) -> collator.compare(a.getName(), b.getName());
    }

    static Comparator<Student> byAge() {
        return Comparator.comparingInt(Student::getAge).thenComparing(byName());
    }

    static Comparator<Student> byGrade() {
        return Comparator.comparingDouble(Student::getAverageGrade).reversed().thenComparing(byName());
    }

    static Comparator<Student> byScholarship() {
        return Comparator.comparing(Student::hasScholarship).reversed().thenComparing(byName());
    }
    static void exportCsv(Student[] array, Path path) throws IOException {
        Files.createDirectories(path.toAbsolutePath().getParent());
        StringBuilder csv = new StringBuilder("Имя;Возраст;Средний балл;Стипендия\n");
        for (Student student : array) {
            csv.append('"').append(student.getName().replace("\"", "\"\""))
               .append("\";").append(student.getAge()).append(';')
               .append(String.format(Locale.ROOT, "%.1f", student.getAverageGrade())).append(';')
               .append(student.hasScholarship() ? "Да" : "Нет").append('\n');
        }
        Files.writeString(path, csv, StandardCharsets.UTF_8);
    }

    private StudentApp() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel title = new JLabel("Массив из 15 студентов · сортировки и экспорт");
        title.setFont(new Font("SansSerif", Font.BOLD, 21));
        JPanel top = new JPanel(new BorderLayout(8, 12));
        top.add(title, BorderLayout.NORTH);
        JPanel buttons = new JPanel(new GridLayout(1, 0, 8, 0));
        addButton(buttons, "Имя ↑", () -> sort(byName(), "По имени: А → Я"));
        addButton(buttons, "Возраст ↑", () -> sort(byAge(), "По возрасту: от младших к старшим"));
        addButton(buttons, "Балл ↓", () -> sort(byGrade(), "По среднему баллу: от большего к меньшему"));
        addButton(buttons, "Стипендия", () -> sort(byScholarship(), "По стипендии: сначала получающие"));
        addButton(buttons, "Сохранить CSV", this::save);
        top.add(buttons, BorderLayout.SOUTH);
        content.add(top, BorderLayout.NORTH);
        JTable table = new JTable(model);
        table.setRowHeight(27);
        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(6, 8));
        bottom.add(status, BorderLayout.NORTH);
        filePreview.setEditable(false);
        filePreview.setFont(new Font("Monospaced", Font.PLAIN, 13));
        filePreview.setText("Поля объекта Student:\nString name; int age; double averageGrade; boolean scholarship;\n\nCSV сохраняется в output/students.csv в кодировке UTF-8.");
        bottom.add(new JScrollPane(filePreview), BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);
        frame.setContentPane(content);
        frame.setSize(1000, 760);
        frame.setLocationRelativeTo(null);
        refresh();
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(event -> action.run());
        panel.add(button);
    }

    private void refresh() {
        model.setRowCount(0);
        for (int i = 0; i < students.length; i++) {
            Student s = students[i];
            model.addRow(new Object[]{i + 1, s.getName(), s.getAge(),
                String.format(Locale.ROOT, "%.1f", s.getAverageGrade()), s.hasScholarship() ? "Да" : "Нет"});
        }
    }

    private void sort(Comparator<Student> comparator, String description) {
        Arrays.sort(students, comparator);
        refresh();
        status.setText(description);
    }

    private void save() {
        Path path = Path.of("output", "students.csv");
        try {
            exportCsv(students, path);
            status.setText("Сохранено 15 объектов → output/students.csv (UTF-8)");
            filePreview.setText(Files.readString(path, StandardCharsets.UTF_8));
            filePreview.setCaretPosition(0);
        } catch (IOException error) {
            status.setText("Ошибка записи: " + error.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentApp app = new StudentApp();
            app.frame.setVisible(true);
        });
    }
}
