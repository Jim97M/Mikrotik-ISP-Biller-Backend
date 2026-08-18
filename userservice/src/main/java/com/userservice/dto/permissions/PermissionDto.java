package com.userservice.dto.permissions;

public class PermissionDto {

    private Long id;
    private String permissionName;
    private String permissionDescription;

    public PermissionDto(Long id, String permissionName, String permissionDescription) {
        this.id = id;
        this.permissionName = permissionName;
        this.permissionDescription = permissionDescription;
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

    // ✅ Builder Implementation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String permissionName;
        private String permissionDescription;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder permissionName(String permissionName) {
            this.permissionName = permissionName;
            return this;
        }

        public Builder permissionDescription(String permissionDescription) {
            this.permissionDescription = permissionDescription;
            return this;
        }

        public PermissionDto build() {
            return new PermissionDto(id, permissionName, permissionDescription);
        }
    }
}

