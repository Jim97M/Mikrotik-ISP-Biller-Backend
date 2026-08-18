package com.userservice.dto.servicepermission;

import com.userservice.dto.availableservice.AvailableServiceDto;

import java.time.LocalDateTime;
import java.util.List;

public class ServicePermissionsDto {

    protected Long id;

    private String permissionName;

    private String permissionDescription;

    private List<AvailableServiceDto> availableServiceDtos;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ServicePermissionsDto(Long id, String permissionName, String permissionDescription, List<AvailableServiceDto> availableServiceDtos, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.permissionName = permissionName;
        this.permissionDescription = permissionDescription;
        this.availableServiceDtos = availableServiceDtos;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionDescription() {
        return permissionDescription;
    }

    public void setPermissionDescription(String permissionDescription) {
        this.permissionDescription = permissionDescription;
    }

    public List<AvailableServiceDto> getAvailableServiceDtos() {
        return availableServiceDtos;
    }

    public void setAvailableServiceDtos(List<AvailableServiceDto> availableServiceDtos) {
        this.availableServiceDtos = availableServiceDtos;
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
