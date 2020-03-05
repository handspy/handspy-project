package pt.up.hs.project.service.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * A DTO for the result of a bulk import.
 */
public class BulkImportResultDTO<T extends Serializable> implements Serializable {

    private int total;
    private int invalid;

    private List<T> data;

    private long processingTime;

    public BulkImportResultDTO() {
    }

    public BulkImportResultDTO(int total, int invalid, List<T> data, long processingTime) {
        this.total = total;
        this.invalid = invalid;
        this.data = data;
        this.processingTime = processingTime;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getInvalid() {
        return invalid;
    }

    public void setInvalid(int invalid) {
        this.invalid = invalid;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }
}
