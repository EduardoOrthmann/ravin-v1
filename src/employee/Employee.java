package employee;

import address.Address;
import enums.EducationLevel;
import enums.MaritalStatus;
import enums.Position;
import interfaces.Person;

import java.time.LocalDate;

public class Employee extends Person {
    private String rg;
    private MaritalStatus maritalStatus;
    private EducationLevel educationLevel;
    private Position position;
    private String workCardNumber;
    private LocalDate admissionDate;
    private LocalDate resignationDate;
    private boolean isAvailable;

    public Employee(String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, Employee createdBy, String rg, MaritalStatus maritalStatus, EducationLevel educationLevel, Position position, String workCardNumber) {
        super(name, phoneNumber, birthDate, cpf, address, createdBy);
        this.rg = rg;
        this.maritalStatus = maritalStatus;
        this.educationLevel = educationLevel;
        this.position = position;
        this.workCardNumber = workCardNumber;
        this.admissionDate = LocalDate.now();
        this.isAvailable = true;
    }

    public Employee() {
        super();
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getWorkCardNumber() {
        return workCardNumber;
    }

    public void setWorkCardNumber(String workCardNumber) {
        this.workCardNumber = workCardNumber;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDate getResignationDate() {
        return resignationDate;
    }

    public void setResignationDate(LocalDate resignationDate) {
        this.resignationDate = resignationDate;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Employee {\n" +
                "\trg = '" + rg + '\'' + "\n" +
                "\tmaritalStatus = " + maritalStatus + "\n" +
                "\teducationLevel = " + educationLevel + "\n" +
                "\tposition = " + position + "\n" +
                "\tworkCardNumber = '" + workCardNumber + '\'' + "\n" +
                "\tadmissionDate = " + admissionDate + "\n" +
                "\tresignationDate = " + resignationDate + "\n" +
                "\tisAvailable = " + isAvailable + "\n" +
                "} " + super.toString();
    }
}
