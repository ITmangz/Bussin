package com.bussin.bussin_api.dto;

import com.bussin.bussin_api.entity.BusStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateBusRequest {

    @NotBlank(message = "Plate number is required")
    @Size(max = 20, message = "Plate number must be at most 20 characters")
    private String plateNumber;

    @Size(max = 100, message = "Model must be at most 100 characters")
    private String model;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 200, message = "Capacity must be at most 200")
    private Integer capacity;

    @NotNull(message = "Status is required")
    private BusStatus status;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BusStatus getStatus() {
        return status;
    }

    public void setStatus(BusStatus status) {
        this.status = status;
    }
}
