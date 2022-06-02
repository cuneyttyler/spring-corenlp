package com.gsu.semantic.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private Long id;
    private String username;
    private String lastfmUsername;
    private String email;
    private String password;
    private Long roleId;
    private Integer status;
    private Integer loginTryCount;

    public User() {}

    public User(ResultSet rs, String prefix) throws SQLException {
        id = rs.getLong(prefix + ".id");
        username = rs.getString(prefix + ".username");
        lastfmUsername = rs.getString(prefix + ".lastfm_username");
        email = rs.getString(prefix + ".email");
        password = rs.getString(prefix + ".password");
        roleId = rs.getLong(prefix + "role_id");
        status = rs.getInt(prefix + "status");
        loginTryCount = rs.getInt(prefix + "login_try_count");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastfmUsername() {
        return lastfmUsername;
    }

    public void setLastfmUsername(String lastfmUsername) {
        this.lastfmUsername = lastfmUsername;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLoginTryCount() {
        return loginTryCount;
    }

    public void setLoginTryCount(Integer loginTryCount) {
        this.loginTryCount = loginTryCount;
    }
}
