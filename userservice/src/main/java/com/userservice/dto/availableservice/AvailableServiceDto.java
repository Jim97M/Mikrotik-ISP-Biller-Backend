package com.userservice.dto.availableservice;

import java.time.LocalDateTime;
import java.util.List;

public class AvailableServiceDto {

    private Long id;

    private String moduleName;

    private String moduleDescription;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public AvailableServiceDto(Long id, String moduleName, String moduleDescription, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.moduleName = moduleName;
        this.moduleDescription = moduleDescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleDescription() {
        return moduleDescription;
    }

    public void setModuleDescription(String moduleDescription) {
        this.moduleDescription = moduleDescription;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
