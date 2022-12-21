package persistence;

import domain.Professor;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public record JdbcProfessorRepository(Connection connection) implements ProfessorRepository {

    @Override
    public List<Professor> findAll() throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("select * from professors");
        List<Professor> p = new LinkedList<>();

        while (resultSet.next()) {
            p.add(new Professor(resultSet.getInt("professor_id"),
                    resultSet.getString("last_name"),
                    resultSet.getString("first_name")));
        }

        return p;
    }

    @Override
    public Optional<Professor> findById(int id) throws SQLException {
        return findAll().stream()
                .filter(s -> s.getId() == id)
                .findFirst();
    }

    @Override
    public Professor save(Professor professor) throws SQLException {
        if (professor.getId() != null) {
            throw new IllegalArgumentException("id exists");
        }

        try (PreparedStatement statement = connection.prepareStatement("insert into professors (last_name, first_name) values (?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, professor.getLastName());
            statement.setString(2, professor.getFirstName());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                professor = new Professor(generatedKeys.getInt(1), professor.getLastName(), professor.getFirstName());
            }
        }

        return professor;
    }

    @Override
    public void delete(Professor professor) throws SQLException {
        if (professor.getId() == null) {
            throw new IllegalArgumentException("no ID");
        }

        PreparedStatement statement = connection.prepareStatement("delete from professors where professor_id = ?;");
        statement.setInt(1, professor.getId());
        statement.executeUpdate();
    }
}