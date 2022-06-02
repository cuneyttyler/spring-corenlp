package com.gsu.semantic.repository;

import com.gsu.common.util.MaxIdCalculator;
import com.gsu.semantic.model.Place;
import com.gsu.semantic.model.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

@Component
public class HistoricalIstanbulDao {

    @Autowired
    private MaxIdCalculator maxIdCalculator;

    @Autowired
    private DataSource hiDataSource;

    public Long savePlace(Place place) {

        String sql = "insert into PLACE set id = ?, name = ?, description = ?, dbpedia_uri = ?" +
                ", typ = ?, typ_uri = ?, lat = ?, lon = ?";

        Connection conn = null;

        try {
            conn = hiDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            Long id = maxIdCalculator.getMaxIdFromTable(conn, true, "place", "id");

            ps.setLong(1, id);
            ps.setString(2, place.getName());
            ps.setString(3, place.getDescription());
            ps.setString(4, place.getDbpediaUri());
            ps.setString(5, place.getType());
            ps.setString(6, place.getTypeUri());
            ps.setDouble(7, place.getLat());
            ps.setDouble(8, place.getLon());

            ps.execute();

            ps.close();

            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public void saveProperty(Property property) {

        String sql = "insert into PROPERTY set id = ?, place_id = ?, name = ?, lang = ?, typ = ?, value = ?, " +
                " uri = ?, datatype = ?";

        Connection conn = null;

        try {
            conn = hiDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            Long id = maxIdCalculator.getMaxIdFromTable(conn, true, "property", "id");

            ps.setLong(1, id);
            ps.setLong(2, property.getPlaceId());
            ps.setString(3, property.getName());
            ps.setString(4, property.getLang());
            ps.setString(5, property.getType());
            ps.setString(6, property.getValue());
            ps.setString(7, property.getUri());
            ps.setString(8, property.getDatatype());

            ps.execute();

            ps.close();

            return;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public Collection<Place> findAllPlaces() {
        String sql = "select * from place pl left join property pr on pr.place_id = pl.id;";

        Connection conn = null;

        HashMap<Long, Place> map = new HashMap<>();
        try {
            conn = hiDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Long placeId = rs.getLong("pl.id");

                Place place;
                if (!map.containsKey(placeId)) {
                    place = new Place(rs);

                    map.put(placeId, place);
                } else {
                    place = map.get(placeId);
                }

                Property property = new Property(rs);
                place.getProperties().add(property);
            }

            rs.close();
            ps.close();

            return map.values();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public Place findPlaceByName(String name) {
        String sql = "select * from place pl left join property pr on pr.place_id = pl.id where pl.name = ?;";

        Connection conn = null;

        Place place = null;
        try {
            conn = hiDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Long placeId = rs.getLong("pl.id");

                place = new Place(rs);

                Property property = new Property(rs);
                place.getProperties().add(property);
            }

            rs.close();
            ps.close();

            return place;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}