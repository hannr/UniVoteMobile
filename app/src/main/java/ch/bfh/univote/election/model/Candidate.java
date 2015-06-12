package ch.bfh.univote.election.model;

import ch.bfh.univote.election.interfaces.Option;

public class Candidate implements Option {

    private int id, yearOfBirth, studySemester, listId;
    private String type, number, lastName, firstName, sex, studyBranch, studyDegree, status;

    public Candidate(int id, String type, String number, String lastName, String firstName,
                     String sex, String studyBranch, String studyDegree,
                     int studySemester, int listId) {
        this.id = id;
        this.type = type;
        this.number = number;
        this.lastName = lastName;
        this.firstName = firstName;
        this.sex = sex;
        this.studyBranch = studyBranch;
        this.studyDegree = studyDegree;
        this.studySemester = studySemester;
        this.listId = listId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getListId() {
        return listId;
    }

    public String getSex() {
        return sex;
    }

    public String getStatus() {
        return status;
    }

    public String getStudyBranch() {
        return studyBranch;
    }

    public String getStudyDegree() {
        return studyDegree;
    }

    public int getStudySemester() {
        return studySemester;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getNumber() {
        return this.number;
    }

    @Override
    public String getCommonName() {
        return this.lastName + " " + this.firstName;
    }
}
