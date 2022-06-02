package com.gsu.knowledgebase.model;

import com.gsu.common.util.DateUtils;
import org.joda.time.DateTime;

import java.sql.ResultSet;

/**
 * Created by cnytync on 02/03/2018.
 */
public class Login {
    private Long id;
    private DateTime crDate;
    private Long userId;
    private boolean success;
    private int type;

    public Login() {
    }

    public Login(ResultSet rs) throws Exception {
        id = rs.getLong("l.id");
        crDate = DateUtils.millisToJodaTime(rs.getTimestamp("l.cr_date"));
        userId = rs.getLong("l.user_id");
        success = rs.getBoolean("l.success");
        type = rs.getInt("type");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getCrDate() {
        return crDate;
    }

    public void setCrDate(DateTime crDate) {
        this.crDate = crDate;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
