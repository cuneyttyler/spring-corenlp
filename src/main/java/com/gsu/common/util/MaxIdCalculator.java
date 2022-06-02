package com.gsu.common.util;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Service
public class MaxIdCalculator {

    private static Logger logger = Logger.getLogger(MaxIdCalculator.class);

    /**
     * Get max id from table to handle auto increment from Java side.
     * This is needed when removing hibernate from the system
     * IMPORTANT! Use synchronized at the methods that you use this function
     * example:
     * 		synchronized (JdbcUtils.maxIdCalculatorService) {
     *      	Long id = JdbcUtils.maxIdCalculatorService.getMaxIdFromTable(conn, true, "basket", "id");
     *          ps.setLong(1, id);
     *          ps.execute();
     *      }
     * @param conn Current established connection
     * @param increase Set true to use directly on insert of a record
     * @param table Table name to get max value of the id
     * @param idColumn ID Column Name
     * @return Max id of the given table
     * @throws java.sql.SQLException If the result cannot be fetched, the exception is throwed
     */
    public Long getMaxIdFromTable(Connection conn, boolean increase, String table, String idColumn)
            throws SQLException {

        String query = "select max(" + idColumn + ") max_id from " + table;
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        long userIdMaxValue;
        if (rs.next()) {
            userIdMaxValue = rs.getLong("max_id");
        } else {
            //This is the first insert operation to the related table
            userIdMaxValue = 0;
            //Do not increase. This is the first id.
            increase = false;
        }

        rs.close();
        ps.close();

        if (increase) {
            userIdMaxValue++;
        }
        return userIdMaxValue;
    }

    public Integer getMaxIntIdFromTable(Connection conn, boolean increase, String table, String idColumn)
            throws SQLException {

        String query = "select max(" + idColumn + ") max_id from " + table;
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        int userIdMaxValue;
        if (rs.next()) {
            userIdMaxValue = rs.getInt("max_id");
        } else {
            //This is the first insert operation to the related table
            userIdMaxValue = 0;
            //Do not increase. This is the first id.
            increase = false;
        }

        rs.close();
        ps.close();

        if (increase) {
            userIdMaxValue++;
        }
        return userIdMaxValue;
    }
}
