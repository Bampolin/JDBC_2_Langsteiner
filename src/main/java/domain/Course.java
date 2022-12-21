package domain;

import java.time.LocalDate;

public class Course {

    private final Integer id;
    private final CourseType type;
    private final Professor professor;
    private final String description;
    private final LocalDate begin;

    public Course(Integer id, CourseType type, Professor professor, String description, LocalDate begin) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("description");

        if (type == null)
            throw new IllegalArgumentException("no type");

        if (begin == null)
            throw new IllegalArgumentException("no begin");

        if (professor == null)
            throw new IllegalArgumentException("no professor");





        this.id = id;
        this.type = type;
        this.professor = professor;
        this.description = description;
        this.begin = begin;
    }

    public Course(CourseType type, Professor professor, String description, LocalDate begin) {
        this(null, type, professor, description, begin);
    }

    public Integer getId() {
        return id;
    }

    public CourseType getType() {
        return type;
    }

    public Professor getProfessor() {
        return professor;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getBegin() {
        return begin;
    }
}
