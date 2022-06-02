package com.gsu.semantic.controller;

import com.gsu.knowledgebase.model.Login;
import com.gsu.semantic.model.*;
import com.gsu.semantic.repository.SemanticGraphDao;
import com.gsu.semantic.service.lastfm.Album;
import com.gsu.semantic.service.lastfm.Lastfm;
import com.gsu.semantic.service.musicbrainz.Musicbrainz;
import com.gsu.semantic.service.musicbrainz.Release;
import com.gsu.semantic.service.musicgraph.LookupResponseItem;
import com.gsu.semantic.service.musicgraph.MusicGraph;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Created by cnytync on 21/12/14.
 */
@Controller("SemanticApi")
@RequestMapping(value = "semantic/api")
public class SemanticApi {

    @Autowired
    private Lastfm lastfm;

    @Autowired
    private SemanticGraphDao semanticGraphDao;

    @Autowired
    private Musicbrainz musicbrainz;

    @Autowired
    private MusicGraph musicGraph;

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    void register(@RequestBody User user) throws Exception {
        semanticGraphDao.saveUser(user);
    }

    @RequestMapping(value = "/login/{username}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    User register(@PathVariable String username) throws Exception {
        return semanticGraphDao.findUserByUsername(username);
    }

    @RequestMapping(value = "/getLastfmGraph/{lastfmUsername}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getLastfmGraph(@PathVariable String lastfmUsername) throws Exception {
//        User user = semanticGraphDao.findUserById(userId);

        List<Node> nodes = lastfm.topArtistsByUser(lastfmUsername);
        List<Link> links = new ArrayList<>();
        for(Node n : nodes) {
            if(n != null && n.getName() != null) {
                List<Node> similars = lastfm.similarArtists(n.getName());

                for (Node s : similars) {
                    if (nodes.contains(s)) {
                        links.add(new Link(n, s, 1.0));
                    }
                }
            }
        }

        return new Graph(nodes,links);
    }

    @RequestMapping(value = "/getGraph", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getGraph(@RequestBody GraphRequest request) throws Exception {
        String nodeName = request.getNodeName();

        Graph dbGraph = semanticGraphDao.getGraphByNodeName(nodeName);

        if (dbGraph != null) {
            return dbGraph;
        }

        Node node = lastfm.artistInfo(nodeName);
        List<Node> similarNodes = lastfm.similarArtists(nodeName);
        similarNodes = similarNodes.subList(0, similarNodes.size() < 5 ? similarNodes.size() : 5);

        List<Node> nodes = new ArrayList<>();
        nodes.add(node);
        nodes.addAll(similarNodes);

        List<Link> links = new ArrayList<>();
        for (Node n : similarNodes) {
            links.add(new Link(node, n, 1.0));
        }

        Graph graph = new Graph(nodes, links);

//        final ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
                for (Node n : graph.getNodes()) {
                    Node dbNode = semanticGraphDao.getNodeByName(n.getName());

                    if (dbNode == null) {
                        Integer id = semanticGraphDao.saveNode(n);
                        n.setDbId(id);
                    } else if (dbNode.getBio() == null) {
                        n.setDbId(dbNode.getDbId());
                        Integer id = semanticGraphDao.updateNode(n);
                    } else {
                        n.setDbId(dbNode.getDbId());
                    }
                }

                for (Node n : graph.getNodes()) {
                    for (Link link : graph.getLinks()) {
                        if (link.getSource().getName().equals(n.getName())) {
                            link.setSource(n);
                        }

                        if (link.getTarget().getName().equals(n.getName())) {
                            link.setTarget(n);
                        }
                    }
                }

                for (Link link : graph.getLinks()) {
                    Link dbLink = semanticGraphDao.getLink(link.getSource().getName(), link.getTarget().getName());

                    if (dbLink == null) {
                        semanticGraphDao.saveLink(link.getSource().getDbId(), link.getTarget().getDbId());
                    }
                }
//            }
//        });

        return graph;
    }

    @RequestMapping(value = "/saveGraphNode", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    void saveNode(@RequestParam Integer nodeId, @RequestParam Integer graphId) throws Exception {
        if (!semanticGraphDao.isGraphNodeSaved(graphId, nodeId))
            semanticGraphDao.saveGraphNode(graphId, nodeId);
    }

    @RequestMapping(value = "/saveNode/{nodeId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    void saveNode(@PathVariable(value = "nodeId") Integer nodeId) throws Exception {
        if (!semanticGraphDao.isUserNodeSaved(1, nodeId))
            semanticGraphDao.saveUserNode(1, nodeId);
    }

    @RequestMapping(value = "/removeGraphNode", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    void removeNode(@RequestParam Integer nodeId, @RequestParam Integer graphId) throws Exception {
        if (semanticGraphDao.isGraphNodeSaved(graphId, nodeId))
            semanticGraphDao.removeGraphNode(graphId, nodeId);
    }

    @RequestMapping(value = "/removeNode/{nodeId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    void removeNode(@PathVariable(value = "nodeId") Integer nodeId) throws Exception {
        if (semanticGraphDao.isUserNodeSaved(1, nodeId))
            semanticGraphDao.removeUserNode(1, nodeId);
    }

    @RequestMapping(value = "/getSavedGraph/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getSavedGraph(@PathVariable Integer id) throws Exception {
        List<Node> nodes = semanticGraphDao.getGraphNodes(id);
        List<Link> links = semanticGraphDao.getLinksForSourceNodes(nodes);

        for (Link link : links) {
            if (!nodes.contains(link.getTarget())) {
                nodes.add(link.getTarget());
            } else {
                link.getTarget().setSaved(true);
            }

            if (nodes.contains(link.getSource())) {
                link.getSource().setSaved(true);
            }
        }

        return new Graph(id, nodes, links);
    }

    @RequestMapping(value = "/getUserGraph/", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getUserGraph() throws Exception {
        List<Node> nodes = semanticGraphDao.getUserNodes(1);
        List<Link> links = semanticGraphDao.getLinksForSourceNodes(nodes);

        for (Link link : links) {
            if (!nodes.contains(link.getTarget())) {
                nodes.add(link.getTarget());
            } else {
                link.getTarget().setSaved(true);
            }

            if (nodes.contains(link.getSource())) {
                link.getSource().setSaved(true);
            }
        }

        return new Graph(nodes, links);
    }

    @RequestMapping(value = "/getAlbumReleases/{mbid}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getUserGraph(@PathVariable String mbid) throws Exception {
        List<Release> releases = musicbrainz.releases(mbid);

        return releases;
    }

    @RequestMapping(value = "/lookup/{prefix}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object lookup(@PathVariable String prefix) throws Exception {
        List<LookupResponseItem> items = musicGraph.lookup(prefix, null, null);

        return items;
    }

//    @RequestMapping(value = "/albums/{id}", method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object albums(@PathVariable String id) throws Exception {
//        List<Album> albums = musicGraph.albums(id);
//
//        return albums;
//    }
//
//    @RequestMapping(value = "/tracks/{id}", method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object tracks(@PathVariable String id) throws Exception {
//        List<Track> tracks = musicGraph.tracks(id);
//
//        return tracks;
//    }

    @RequestMapping(value = "/albums", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object albums(@RequestBody AlbumRequest request) throws Exception {
        List<Album> albums = lastfm.topAlbums(request.getNodeName());

        return albums;
    }

    @RequestMapping(value = "/tracks", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object tracks(@RequestParam String artist, @RequestParam String album) throws Exception {
        Album alb = lastfm.albumInfo(artist, album);

        return alb;
    }

    @RequestMapping(value = "/createGraph/{name}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object createGraph(@PathVariable String name) throws Exception {
        Integer id = semanticGraphDao.createGraph(1, name);

        return id;
    }

    @RequestMapping(value = "/listGraphs/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object listGraphs(@PathVariable Integer userId) throws Exception {
        List<Graph> graphs = semanticGraphDao.listGraphs(userId);

        return graphs;
    }
}