package persistence;

import domain.Professor;
import domain.Student;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public record JdbcStudentRepository(Connection connection) implements StudentRepository {

    @Override
    public List<Student> findAll() throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("select * from students");
        List<Student> p = new LinkedList<>();

        while (resultSet.next()) {
            p.add(new Student(resultSet.getInt("student_id"),
                    resultSet.getString("last_name"),
                    resultSet.getString("first_name")));
        }

        return p;
    }

    @Override
    public Optional<Student> findById(int id) throws SQLException {
        return findAll().stream()
                .filter(s -> s.getId() == id)
                .findFirst();
    }

    @Override
    public Student save(Student student) throws SQLException {
        if (student.getId() != null) {
            throw new IllegalArgumentException("id exists");
        }

        try (PreparedStatement statement = connection.prepareStatement("insert into students (last_name, first_name) values (?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, student.getLastName());
            statement.setString(2, student.getFirstName());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                student = new Student(generatedKeys.getInt(1), student.getLastName(), student.getFirstName());
            }
        }

        return student;
    }

    @Override
    public void update(Student student) throws SQLException {
        connection.createStatement().executeUpdate("update students set last_name=" + student.getLastName() + "first_name=" + student.getFirstName()
                + "where id like" + student.getId());
    }

    @Override
    public void delete(Student student) throws SQLException {
        if (student.getId() == null)
            throw new IllegalArgumentException("no id");

        connection.createStatement().executeUpdate("delete from students where student_id = " + student.getId());
    }
}