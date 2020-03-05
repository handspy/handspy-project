package pt.up.hs.project.service.importer.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByPosition;
import pt.up.hs.project.service.importer.converters.LocalDateConverter;

import java.time.LocalDate;

public class TaskCsvDTO {

    @CsvBindByPosition(position = 0, required = true)
    @CsvBindByName(column = "name", required = true)
    private String name;

    @CsvCustomBindByPosition(position = 1, converter = LocalDateConverter.class)
    @CsvCustomBindByName(column = "start date", converter = LocalDateConverter.class)
    private LocalDate startDate;

    @CsvCustomBindByPosition(position = 2, converter = LocalDateConverter.class)
    @CsvCustomBindByName(column = "end date", converter = LocalDateConverter.class)
    private LocalDate endDate;

    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "labels")
    private String labels;

    public TaskCsvDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }
}
