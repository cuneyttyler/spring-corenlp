package com.gsu.knowledgebase.model;

import java.sql.ResultSet;

/**
 * Created by cnytync on 01/03/2018.
 */
public class UserConfirmation {
    private Long id;
    private Long userId;
    private String confirmationCode;
    private Integer status;

    public UserConfirmation() {

    }

    public UserConfirmation(ResultSet rs) throws Exception {
        id = rs.getLong("uc.id");
        userId = rs.getLong("uc.user_id");
        confirmationCode = rs.getString("uc.confirmation_code");
        status = rs.getInt("uc.status");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
