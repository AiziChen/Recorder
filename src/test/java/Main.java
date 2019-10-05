import com.coq.record.Configuration;
import com.coq.record.RecordMapper;
import com.coq.record.Recorder;
import record.IDCard;
import record.Student;
import record.Teacher;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Configuration conf = Configuration.getInstance()
                // 配置Records的存储路径，此处为：/Users/coq
                .setStoredLocation("/Users/coq");
        Recorder r = new Recorder(conf);
        RecordMapper<Student> studentMapper = r.load(Student.class);
        RecordMapper<Teacher> teacherMapper = r.load(Teacher.class);
        RecordMapper<IDCard> cardMapper = r.load(IDCard.class);

        long userSize = studentMapper.size();
        long cardSize = cardMapper.size();

        Student student = new Student();
        student.setAge(23);
        student.setHeight(183.3);
        student.setName("DavidChen");
        IDCard card = new IDCard();
        card.setNumber("34274279472342");
        student.setCard(card);

        card = new IDCard();
        card.setNumber("1222121121");
        Teacher teacher = new Teacher();
        teacher.setName("陈权业");
        teacher.setStudent(new Student[]{student, student});
        teacher.setCard(card);
        // 插入操作
        teacherMapper.insert(teacher);

        // 删除index项
        studentMapper.delete(1);
        // 还原被删除index项
        studentMapper.unDelete(1);
        studentMapper.delete(2);
        teacherMapper.unDelete(1);
//        // 打印
        System.out.println(teacherMapper.find(1));
        System.out.println(studentMapper.find(2));
        System.out.println(studentMapper.find(1));

        r.closeAll();
    }
}
