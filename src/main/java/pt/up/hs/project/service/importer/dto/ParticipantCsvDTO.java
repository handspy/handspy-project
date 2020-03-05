package pt.up.hs.project.service.importer.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByPosition;
import pt.up.hs.project.service.importer.converters.LocalDateConverter;

import java.time.LocalDate;
import java.util.Date;

public class ParticipantCsvDTO {

    @CsvBindByPosition(position = 0, required = true)
    @CsvBindByName(column = "name", required = true)
    private String name;

    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "gender")
    private String gender;

    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "handedness")
    private String handedness;

    @CsvCustomBindByPosition(position = 3, converter = LocalDateConverter.class)
    @CsvCustomBindByName(column = "birthdate", converter = LocalDateConverter.class)
    private LocalDate birthdate;

    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "additional info")
    private String additionalInfo;

    @CsvBindByPosition(position = 5)
    @CsvBindByName(column = "labels")
    private String labels;

    public ParticipantCsvDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHandedness() {
        return handedness;
    }

    public void setHandedness(String handedness) {
        this.handedness = handedness;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "ParticipantCsvDTO{" +
            "name='" + name + '\'' +
            ", gender='" + gender + '\'' +
            ", handedness='" + handedness + '\'' +
            ", birthdate=" + birthdate +
            ", additionalInfo='" + additionalInfo + '\'' +
            ", labels='" + labels + '\'' +
            '}';
    }
}
