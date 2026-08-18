package com.userservice.dto.availableservice;


import com.userservice.dto.permissions.PermissionDto;

import java.util.List;

public class ModuleDto {
    Long id;
    String name;
    String description;
    List<PermissionDto> permissions;

    public ModuleDto(Long id, String name, String description, List<PermissionDto> permissions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }
}
