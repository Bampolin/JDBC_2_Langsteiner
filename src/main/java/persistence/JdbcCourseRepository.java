package persistence;

import domain.*;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public record JdbcCourseRepository(Connection connection) implements CourseRepository {

    @Override
    public List<Course> findAll() throws SQLException {
        ResultSet result = connection.createStatement().executeQuery("select * from courses;");

        List<Course> l = new LinkedList<>();

        while (result.next()) {
            Integer i = result.getInt("course_id");
            String courseType = result.getString("type_id");
            Integer professor = result.getInt("professor_id");
            String description = result.getString("description");
            LocalDate begin = result.getDate("begin_date").toLocalDate();
            Professor prof = new JdbcProfessorRepository(connection).findById(professor).get();

            l.add(new Course(i, new CourseType(courseType.charAt(0), description), prof, description, begin));
        }

        return l;
    }

    @Override
    public List<Course> findAllByProfessor(Professor professor) throws SQLException {
        return null;
    }

    @Override
    public Optional<Course> findById(int id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Course save(Course course) throws SQLException {
        if (course.getId() != null)
            throw new IllegalArgumentException("course has id");

        if (course.getBegin().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("invalid begin time");

        PreparedStatement prep = connection.prepareStatement("insert into courses (type_id, professor_id, description, begin_date) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

        prep.
        prep.setInt(2, course.getProfessor().getId());
        prep.setString(3, course.getDescription());
        prep.setDate(4, Date.valueOf(course.getBegin()));
        prep.executeUpdate();

        ResultSet res = prep.getGeneratedKeys();
        if (res.next()) {
            course = new Course(res.getInt(1), course.getType(), course.getProfessor(), course.getDescription(), course.getBegin());
        }


        return course;
    }

    @Override
    public List<Course> findAllByStudent(Student student) throws SQLException {
        return null;
    }

    @Override
    public void enrollInCourse(Student student, Course course) throws SQLException {
        connection.createStatement().executeUpdate("insert into courses_students (course_id, student_id) values (" + course.getId() + ", " + student.getId() + ")");
    }

    @Override
    public void unenrollFromCourse(Student student, Course course) throws SQLException {
        connection.createStatement().executeUpdate("delete from courses_students where course_id = " + course.getId() + " and student_id = " + student.getId());
    }
}