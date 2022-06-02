package com.gsu.semantic.repository;

import com.gsu.common.util.MaxIdCalculator;
import com.gsu.semantic.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class SemanticGraphDao {

    @Autowired
    private MaxIdCalculator maxIdCalculator;

    @Autowired
    private DataSource sgDataSource;

    public SemanticGraphDao() {

    }

    public SemanticGraphDao(MaxIdCalculator maxIdCalculator, DataSource sgDataSource) {
        this.maxIdCalculator = maxIdCalculator;
        this.sgDataSource = sgDataSource;
    }

    public User findUserById(Long userId) {
        String sql = "select * from user as u where u.id = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);

            User user = null;

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = new User(rs,"u");
            }

            ps.close();

            return user;
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

    public void updateUserLoginTryCount(Long userId, Integer count) {
        String sql = "update user set login_try_count = ? where id = ?;";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);


            ps.setLong(1, count);
            ps.setLong(2, userId);

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

    public User findUserByUsername(String username) {
        String sql = "select * from user as u where u.username = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            User user = null;

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = new User(rs,"u");
            }

            ps.close();

            return user;
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

    public Integer saveUser(User user) {
        String sql = "insert into user set id = ?, email = ?, username = ?, lastfm_username = ?, password = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            Integer id = maxIdCalculator.getMaxIntIdFromTable(conn, true, "node", "id");

            ps.setInt(1, id);
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getLastfmUsername());
            ps.setString(5, user.getPassword());

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

    public Integer saveNode(Node node) {

        String sql = "insert into node set id = ?, name = ?, mbid = ?, url = ?" +
                ", small_img = ?, medium_img = ?, large_img = ?, bio = ?, bio_summary = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            Integer id = maxIdCalculator.getMaxIntIdFromTable(conn, true, "node", "id");

            ps.setInt(1, id);
            ps.setString(2, node.getName());
            ps.setString(3, node.getMbid());
            ps.setString(4, node.getUrl());
            ps.setString(5, node.getSmallImg());
            ps.setString(6, node.getMediumImg());
            ps.setString(7, node.getLargeImg());
            ps.setString(8, node.getBio());
            ps.setString(9, node.getBioSummary());

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

    public Integer updateNode(Node node) {

        String sql = "update node set  name = ?, mbid = ?, url = ?" +
                ", small_img = ?, medium_img = ?, large_img = ?, bio = ?, bio_summary = ? where id = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, node.getName());
            ps.setString(2, node.getMbid());
            ps.setString(3, node.getUrl());
            ps.setString(4, node.getSmallImg());
            ps.setString(5, node.getMediumImg());
            ps.setString(6, node.getLargeImg());
            ps.setString(7, node.getBio());
            ps.setString(8, node.getBioSummary());
            ps.setInt(9, node.getDbId());

            ps.execute();

            ps.close();

            return node.getDbId();
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

    public void removeUserNode(Integer userId, Integer nodeId) {

        String sql = "delete from user_node where user_id = ? and node_id = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);
            ps.setInt(2, nodeId);

            ps.execute();

            ps.close();

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

    public void removeGraphNode(Integer graphId, Integer nodeId) {

        String sql = "delete from graph_node where graph_id = ? and node_id = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, graphId);
            ps.setInt(2, nodeId);

            ps.execute();

            ps.close();

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

    public void saveGraph(Graph graph) {


        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();

            String sql = "insert into node set id = ?, name = ?, mbid = ?, url = ?" +
                    ", small_img = ?, medium_img = ?, large_img = ?, bio = ?, bio_summary = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            for (Node node : graph.getNodes()) {
                Integer id = maxIdCalculator.getMaxIntIdFromTable(conn, true, "node", "id");

                ps.setInt(1, id);
                ps.setString(2, node.getName());
                ps.setString(3, node.getMbid());
                ps.setString(4, node.getUrl());
                ps.setString(5, node.getSmallImg());
                ps.setString(6, node.getMediumImg());
                ps.setString(7, node.getLargeImg());
                ps.setString(8, node.getBio());
                ps.setString(9, node.getBioSummary());

                ps.execute();
            }

            ps.close();

            String linkSql = "insert into link set id = ?, source_id = ?, dest_id = ?";
            ps = conn.prepareStatement(linkSql);

            for (Node node : graph.getNodes()) {
                Integer id = maxIdCalculator.getMaxIntIdFromTable(conn, true, "node", "id");

                ps.setInt(1, id);
                ps.setInt(1, id);
                ps.setInt(1, id);

                ps.execute();
            }

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

    public Integer createGraph(Integer userId, String name) {
        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();

            String sql = "insert into graph set id = ?,user_id = ?, name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            Integer id = maxIdCalculator.getMaxIntIdFromTable(conn, true, "graph", "id");

            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.setString(3, name);

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

    public List<Graph> listGraphs(Integer userId) {
        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();

            String sql = "select * from graph where user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            List<Graph> graphs = new ArrayList<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");

                Graph graph = new Graph();
                graph.setId(id);
                graph.setName(name);
                graph.setUserId(userId);

                graphs.add(graph);
            }

            ps.close();

            return graphs;
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

    public Integer saveGraphNode(Integer graphId, Integer nodeId) {

        String sql = "insert into graph_node set id = ?, graph_id = ?, node_id = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            Integer id = maxIdCalculator.getMaxIntIdFromTable(conn, true, "graph_node", "id");

            ps.setInt(1, id);
            ps.setInt(2, graphId);
            ps.setInt(3, nodeId);

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

    public Integer saveUserNode(Integer userId, Integer nodeId) {

        String sql = "insert into user_node set id = ?, user_id = ?, node_id = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            Integer id = maxIdCalculator.getMaxIntIdFromTable(conn, true, "user_node", "id");

            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.setInt(3, nodeId);

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

    public boolean isUserNodeSaved(Integer userId, Integer nodeId) {
        String sql = "select * from user_node where user_id = ? and node_id = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, nodeId);

            boolean result = false;

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = true;
            }

            ps.close();

            return result;
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

    public boolean isGraphNodeSaved(Integer graphId, Integer nodeId) {
        String sql = "select * from graph_node where graph_id = ? and node_id = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, graphId);
            ps.setInt(2, nodeId);

            boolean result = false;

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = true;
            }

            ps.close();

            return result;
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

    public Integer saveTag(Tag tag) {

        String sql = "insert into tag set id = ?, name = ?, url = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            Integer id = maxIdCalculator.getMaxIntIdFromTable(conn, true, "tag", "id");

            ps.setInt(1, id);
            ps.setString(2, tag.getName());
            ps.setString(3, tag.getUrl());

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

    public Integer saveNodeTag(Integer nodeId, Integer tagId) {
        String sql = "insert into node_tag set id = ?, node_id = ?, tag_id = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            Integer id = maxIdCalculator.getMaxIntIdFromTable(conn, true, "node_tag", "id");

            ps.setInt(1, id);
            ps.setInt(2, nodeId);
            ps.setInt(3, tagId);

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

    public Integer saveLink(Integer sourceId, Integer destId) {
        String sql = "insert into link set id = ?, source_id = ?, dest_id = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            Integer id = maxIdCalculator.getMaxIntIdFromTable(conn, true, "link", "id");

            ps.setInt(1, id);
            ps.setInt(2, sourceId);
            ps.setInt(3, destId);

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

    public List<Link> getAllLinks() {
        String sql = "select * from link l, node s, node d where l.source_id = s.id and l.dest_id = d.id";

        Connection conn = null;

        List<Link> links = new ArrayList<>();
        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                links.add(new Link(rs));
            }

            ps.close();

            return links;
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

    public Link getLink(String sourceName, String destName) {
        String sql = "select * from link l, node s, node d where l.source_id = s.id and l.dest_id = d.id " +
                "and s.name = ? and d.name = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sourceName);
            ps.setString(2, destName);

            Link link = null;

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                link = new Link(rs);
            }

            ps.close();

            return link;
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

    public List<Node> getAllNodes() {
        String sql = "select * from node n";

        Connection conn = null;

        List<Node> nodes = new ArrayList<>();
        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                nodes.add(new Node(rs, "n"));
            }

            ps.close();

            return nodes;
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

    public Node getNodeByName(String name) {
        String sql = "select * from node n where n.name = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);

            Node node = null;

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                node = new Node(rs, "n");
            }

            ps.close();

            return node;
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

    public Node getNodeByEscapedName(String name) {
        String sql = "select * from node n where n.escaped_name = ?";

        Connection conn = null;

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);

            Node node = null;

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                node = new Node(rs, "n");
            }

            ps.close();

            return node;
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


    public Graph getGraphByNodeName(String name) {
        String sql = "select * from link l, node s, node d where l.source_id = s.id and l.dest_id = d.id and s.name = ?";

        Connection conn = null;

        List<Link> links = new ArrayList<>();
        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                links.add(new Link(rs));
            }

            if (links.size() == 0) {
                return null;
            }

            HashMap<String, Node> map = new HashMap<>();
            for (Link link : links) {
                if (!map.containsKey(link.getSource().getName())) {
                    map.put(link.getSource().getName(), link.getSource());
                }

                if (!map.containsKey(link.getTarget().getName())) {
                    map.put(link.getTarget().getName(), link.getTarget());
                }
            }

            Graph graph = new Graph();
            graph.setNodes(new ArrayList(map.values()));
            graph.setLinks(links);

            ps.close();

            return graph;
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

    public List<Node> getUserNodes(Integer userId) {
        String sql = "select * from user_node un, node n where un.node_id = n.id and un.user_id = ?";

        Connection conn = null;

        List<Node> nodes = new ArrayList<>();
        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Node n = new Node(rs, "n");
                n.setSaved(true);

                nodes.add(n);
            }

//            if (nodes.size() == 0) {
//                return null;
//            }

            ps.close();

            return nodes;
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

    public List<Link> getLinksForSourceNodes(List<Node> nodes) {
        String sql = "select * from link l, node s, node d where l.source_id = s.id " +
                " and l.dest_id = d.id and s.id = ? ";

        Connection conn = null;

        List<Link> links = new ArrayList<>();

        if (nodes.size() == 0) {
            return links;
        }

        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            for (Node node : nodes) {
                ps.setInt(1, node.getDbId());

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    links.add(new Link(rs));
                }
            }

            ps.close();

            return links;
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

    public List<Node> getGraphNodes(Integer graphId) {
        String sql = "select * from graph_node gn, node n where gn.node_id = n.id and gn.graph_id = ?";

        Connection conn = null;

        List<Node> nodes = new ArrayList<>();
        try {
            conn = sgDataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, graphId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Node n = new Node(rs, "n");
                n.setSaved(true);

                nodes.add(n);
            }

//            if (nodes.size() == 0) {
//                return null;
//            }

            ps.close();

            return nodes;
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
