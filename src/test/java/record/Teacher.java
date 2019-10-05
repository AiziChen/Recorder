package record;

import com.coq.record.annotation.Record;

import java.util.Arrays;

@Record
public class Teacher {
    private IDCard card;
    private Student[] student;
    private String name;

    public IDCard getCard() {
        return card;
    }

    public void setCard(IDCard card) {
        this.card = card;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student[] getStudent() {
        return student;
    }

    public void setStudent(Student[] student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "name='" + name + '\'' +
                ", student=" + Arrays.toString(student) +
                ", card=" + card +
                '}';
    }
}
