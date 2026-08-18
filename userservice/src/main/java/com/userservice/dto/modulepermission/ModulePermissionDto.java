package com.userservice.dto.modulepermission;

import com.userservice.dto.permissions.PermissionDto;

import java.util.List;

public class ModulePermissionDto {

    private Long id;
    private String moduleName;
    private String moduleDescription;
    private List<PermissionDto> permissions;

    public ModulePermissionDto(Long id, String moduleName, String moduleDescription, List<PermissionDto> permissions) {
        this.id = id;
        this.moduleName = moduleName;
        this.moduleDescription = moduleDescription;
        this.permissions = permissions;
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

    public List<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }

    // ✅ Builder Implementation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String moduleName;
        private String moduleDescription;
        private List<PermissionDto> permissions;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder moduleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }

        public Builder moduleDescription(String moduleDescription) {
            this.moduleDescription = moduleDescription;
            return this;
        }

        public Builder permissions(List<PermissionDto> permissions) {
            this.permissions = permissions;
            return this;
        }

        public ModulePermissionDto build() {
            return new ModulePermissionDto(id, moduleName, moduleDescription, permissions);
        }
    }
}

