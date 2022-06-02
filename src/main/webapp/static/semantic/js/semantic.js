var app = angular.module('app', ['ngDialog']);

app.controller('Controller', function ($scope, $http, $q, $sce, $timeout, ngDialog) {

        $scope.searchInput = 'Led Zeppelin';

        $http.get("/semantic/api/listGraphs/1")
            .then(function (response) {
                $scope.graphs = response.data;

            }, printError);

        $scope.nodes = [];
        $scope.search = function () {
            $('.album-box-edge').animate({left: '-290px'}, 300);
            $scope.page = $scope.page == 'graph' ? 'graph' : 'search';

            if ($scope.graph && $scope.graph.nodes) {
                var name = null;
                for (var i = 0; i < $scope.graph.nodes.length; i++) {
                    if ($scope.searchInput.toLowerCase() == $scope.graph.nodes[i].name.toLowerCase()) {
                        name = $scope.graph.nodes[i].name;
                        break;
                    }
                }

                if (name) {
                    $.event.trigger('goToNodePosition', name.toLowerCase());
                } else {
                    fetchData($scope.searchInput.toLowerCase());
                }
            } else {
                fetchData($scope.searchInput.toLowerCase());
            }
        };

        $(document).bind('nodeClicked', function (e, nodeName) {
            $('.album-box-edge').animate({left: '-290px'}, 300);

            if (nodeName != $scope.selectedNode.name) {
                fetchData(nodeName.toLowerCase());
//            $scope.selectedNode = _.find($scope.graph.nodes, {name: nodeName});
            }
        });

        $scope.showSaveButton = function() {
            return $scope.page == 'graph' && $scope.selectedNode;
        }

        $scope.getSavedNodes = function () {
            if (!$scope.graph || !$scope.graph.nodes)
                return [];

            var savedNodes = [];
            _.each($scope.graph.nodes, function (n) {
                if (n.saved)
                    savedNodes.push(n);
            });

            return savedNodes;
        }

        $scope.openRegisterWindow = function () {
                    ngDialog.open({
                        template: 'register-popup',
                        controller: ['$scope', function ($ss) {

                            var bcrypt = dcodeIO.bcrypt;

                            $ss.closeWindow = function () {
                                $ss.closeThisDialog();
                            };

                            $ss.register = function () {
                                var salt = bcrypt.genSaltSync(10);
                                var password = bcrypt.hashSync($ss.password, salt);
                                $http.post('/semantic/api/register/',
                                    {
                                    email: $ss.email,
                                    username: $ss.username,
                                    lastfmUsername: $ss.lastfmUsername,
                                    password: password
                                    })
                                    .then(function (response) {
                                        $ss.closeThisDialog();
                                    }, function (err) {
                                    });
                            };

                        }]});
                };

        $scope.openLastFmGraphWindow = function () {
            ngDialog.open({
                template: 'lastfm-popup',
                controller: ['$scope', function ($ss) {

                    $ss.closeWindow = function () {
                        $ss.closeThisDialog();
                    };

                    $ss.show = function() {
                        $scope.getLastfmGraph($ss.lastfmUsername);
                        $ss.closeThisDialog();
                    };

                }]});
        };

        $scope.openLoginWindow = function () {
            ngDialog.open({
                template: 'login-popup',
                controller: ['$scope', function ($ss) {

                    var bcrypt = dcodeIO.bcrypt;

                    $ss.closeWindow = function () {
                        $ss.closeThisDialog();
                    };

                    $ss.login = function () {
                        var salt = bcrypt.genSaltSync(10);
                        var password = bcrypt.hashSync($ss.password, salt);

                        // 'j_username='+$ss.username+'&j_password='+password
                        $http.post('/semantic/api/login', jQuery("#login-form").serialize())
                            .then(function (response) {
                                if(response != null && response.password == password) {
                                    var authdata = bcrypt.hashSync($ss.username + ":" + password, salt);

                                    $rootScope.globals = {
                                        currentUser: {
                                            username: $ss.username,
                                            authdata: authdata
                                        }
                                    };

                                    var cookieExp = new Date();
                                    cookieExp.setDate(cookieExp.getDate() + 7);
                                    $cookies.putObject('globals', $scope.globals, { expires: cookieExp });

                                    $ss.closeThisDialog();
                                } else {
                                    $ss.message = 'Username or password is incorrect';
                                }
                            }, function (err) {
                            });
                    };

                }]});
        };

        $scope.newGraph = function () {
            ngDialog.open({
                template: 'new-graph-popup',
                controller: ['$scope', function ($ss) {

                    $ss.closeWindow = function () {
                        $ss.closeThisDialog();
                    };

                    $ss.createGraph = function () {

                        $http.get('/semantic/api/createGraph/' + $ss.graphName)
                            .then(function (response) {
                                $scope.graphs.push({name: $ss.graphName, id: response.data});

                                $ss.closeThisDialog();
                            }, function (err) {
                            });
                    };

                }]});
        };

        $scope.detectCommunities = function () {
            var nodes = [], links = [];

            for (var i = 0; i < $scope.graph.links.length; i++) {
                var l = $scope.graph.links[i];
                if (l.source.saved && l.target.saved) {
                    links.push({
                        source: l.source.id,
                        target: l.target.id,
                        weight: 1
                    });
                }
            }

            for (var i = 0; i < $scope.graph.nodes.length; i++) {
                var n = $scope.graph.nodes[i];
                if (n.saved) {
                    nodes.push(n.id);
                }
            }

            var community = jLouvain().nodes(nodes).edges(links);
            var result = community();
        };

        var fetchData = function (node) {
            $http.post("/semantic/api/getGraph", {nodeName: node})
                .then(function (response) {
//                $scope.graph = response.data; // modify this part to append nodes and links instead of directly assigning new values...

                    var incomingGraph = response.data;

                    if ($scope.graph == null) {
                        $scope.graph = {nodes: [], links: []};
                    }

                    for (var i = 0; i < response.data.nodes.length; i++) {
                        var n = _.find($scope.graph.nodes, {dbId: response.data.nodes[i].dbId});

                        if (!n) {
                            $scope.graph.nodes.push(response.data.nodes[i]);
                        }
                    }

                    $scope.graph.links.push(response.data.links);

                    for (var i = 0; i < $scope.graph.nodes.length; i++) {
                        var n = $scope.graph.nodes[i];

                        if (n.name.toLowerCase() == node) {
                            n.selected = true;
                            $scope.selectedNode = n;
                            incomingGraph.selectedNode = n;
                        }
                    }

                    semantic.getInstance().processIncomingData(incomingGraph);

                    $http.post("/semantic/api/albums", {nodeName: $scope.selectedNode.name})
                        .then(function (response) {
                            $scope.albums = response.data;
                            $scope.selectedNode.albums = response.data;

                            $.event.trigger('dataFetched');
                            $timeout(function () {
                                setupAccordion();
                                $('.info-text').find('a').attr('target', '_blank');
                            });

                        }, printError);
                }, printError);
        };

        function setupAccordion() {
            var acc = document.getElementsByClassName("accordion");
            var i;

            for (i = 0; i < acc.length; i++) {
                acc[i].onclick = function () {
                    /* Toggle between adding and removing the "active" class,
                     to highlight the button that controls the panel */
                    this.classList.toggle("active");

                    /* Toggle between hiding and showing the active panel */
                    var panel = this.nextElementSibling;
                    if (panel.style.display === "block") {
                        panel.style.display = "none";
                    } else {
                        panel.style.display = "block";
                    }
                }
            }
        }

        sideMenu.create($scope);

        var count = 0;

        $scope.isGraphActive = function($index) {
            count++

            var isSelected = $scope.selectedGraph && $scope.graphs[$index].id==$scope.selectedGraph.id;
            if(isSelected) {
                var isActive;

                if(count <= $scope.graphs.length) {
                    isActive = $scope.graphs[$index].active = !$scope.graphs[$index].active;
//                    isActive = $scope.graphs[$index].active ? false : true;
                } else {
                    isActive = $scope.graphs[$index].active;
                }
                if(count > $scope.graphs.length)
                    setTimeout(function() {count = 0;},100);

                return {
                    active : isActive
                }
            } else {
                if(count > $scope.graphs.length)
                    setTimeout(function() {count = 0;},100);
                return { active : false};
            }

        }

        $scope.getSavedGraph = function (graph, $el) {
            $scope.page = $scope.page == 'graph' ? 'search' : 'graph';
            $('.album-box-edge').animate({left: '-290px'}, 300);
            count = 0;

            $scope.graph = {nodes: [], links: []};
            $scope.selectedGraph = graph;
            for(var i in $scope.graphs) {
                if($scope.graphs[i].id == graph.id && $scope.graphs[i].active) {
                    $scope.selectedNode = {};
                    semantic.createNewInstance();
                    semantic.getInstance().processIncomingData({nodes:{}, links:{}});
                    return;
                }
            }

            $http.get("/semantic/api/getSavedGraph/" + graph.id)
                .then(function (response) {
                    semantic.createNewInstance();

                    $scope.selectedGraph = response.data;
                    $scope.graph = response.data;
                    if (!response.data.nodes || response.data.nodes.length == 0) {
                        return;
                    }

                    $scope.selectedNode = response.data.nodes[0];
                    $scope.selectedNode.selected = true;

                    var incomingGraph = response.data;
                    incomingGraph.selectedNode = $scope.selectedNode;

                    semantic.getInstance().processIncomingData(incomingGraph);

                }, printError);
        };

        $scope.showTracks = function (release, artist) {
            if (release.clicked) {
                release.clicked = false;
                return;
            }

            $http.get("/semantic/api/tracks?artist=" + encodeURIComponent(artist.name) + "&album=" + encodeURIComponent(release.name))
                .then(function (response) {
                    release.tracks = response.data;

                }, printError);
        };

        $scope.saveNode = function () {
            if ($scope.selectedGraph) {
                $http.get("/semantic/api/saveGraphNode?nodeId="
                    + $scope.selectedNode.dbId
                    + "&graphId=" + $scope.selectedGraph.id)
                    .then(function (response) {
                        $.event.trigger('nodeSaved', $scope.selectedNode.name);
                        $scope.selectedNode.saved = true;
                    }, printError);
            } else {
                $http.get("/semantic/api/saveNode/" + $scope.selectedNode.dbId)
                    .then(function (response) {
                        $.event.trigger('nodeSaved', $scope.selectedNode.name);
                        $scope.selectedNode.saved = true;
                    }, printError);
            }
        };

        $scope.removeNode = function () {
            if ($scope.selectedGraph) {
                $http.get("/semantic/api/removeGraphNode?graphId= " + $scope.selectedGraph.id + "&nodeId="
                    + $scope.selectedNode.dbId)
                    .then(function (response) {
                        $.event.trigger('nodeRemoved', $scope.selectedNode.name);
                    }, printError);
            } else {
                $http.get("/semantic/api/removeNode/" + $scope.selectedNode.dbId)
                    .then(function (response) {
                        $.event.trigger('nodedRemoved', $scope.selectedNode.name);
                    }, printError);
            }
        };

        $scope.getUserGraph = function () {
            $http.get("/semantic/api/getUserGraph/")
                .then(function (response) {
                    $scope.graph = response.data;

                    $scope.selectedNode = response.data.nodes[0];

                    semantic.getInstance().processIncomingData(response.data);

                }, printError);
        };

        $scope.getLastfmGraph = function (lastfmUsername) {
            $scope.allscreenLoading = true;
            $scope.page = 'lastfm';

            $http.get("/semantic/api/getLastfmGraph/"+lastfmUsername)
                .then(function (response) {
                    $scope.allscreenLoading = false;

                    var incomingGraph = response.data;
                    $scope.selectedNode = response.data.nodes[0];
                    incomingGraph.selectedNode = $scope.selectedNode;

                    if ($scope.graph == null) {
                        $scope.graph = {nodes: [], links: []};
                    }

                    for (var i = 0; i < response.data.nodes.length; i++) {
                        var n = _.find($scope.graph.nodes, {dbId: response.data.nodes[i].dbId});

                        if (!n) {
                            $scope.graph.nodes.push(response.data.nodes[i]);
                        }
                    }

                    for (var i = 0; i < $scope.graph.nodes.length; i++) {
                        var n = $scope.graph.nodes[i];

                        if (n.name.toLowerCase() == $scope.selectedNode.name.toLowerCase()) {
                            n.selected = true;
                        }
                    }

                    $scope.graph.links.push(response.data.links);

                    semantic.getInstance().processIncomingData(incomingGraph,true);

                    $http.post("/semantic/api/albums", {nodeName: $scope.selectedNode.name})
                        .then(function (response) {
                            $scope.albums = response.data;
                            $scope.selectedNode.albums = response.data;

                            $.event.trigger('dataFetched');
                            $timeout(function () {
                                setupAccordion();
                                $('.info-text').find('a').attr('target', '_blank');
                            });

                        }, printError);

                }, printError);
        };

        $scope.isNodeSaved = function () {
            return $scope.selectedNode.saved;
        };

        $scope.showHideSimilars = function () {
            $.event.trigger('showHideSimilars');
        };

        $scope.showHideNames = function () {
            $.event.trigger('showHideNames', $scope.showNames);
        };

        $("#search-input").autocomplete({
            minLength: 2,
            source: function (request, response) {
                var term = request.term;

                $http.get("/semantic/api/lookup/" + encodeURIComponent(term))
                    .then(function (resp) {
                        var results = [];
                        for (var i = 0; i < resp.data.length; i++) {
                            var d = resp.data[i];

                            results.push({
                                id: d.id,
                                musicgraphId: d.id,
                                label: d.name,
                                value: d.name
                            })
                        }

                        response(results);
                    }, printError);

            }, select: function (event, ui) {
                this.value = ui.item.value;
                $scope.searchInput = ui.item.value;

            }, change: function (event, ui) { // not-selected
                if (ui.item === null) {

                }
            }
        });

        setInterval(function () {
            $('body')
                .animate({
                    backgroundColor: '#2affc'
                }, 2000)
                .animate({
                    backgroundColor: '#8987ff'
                }, 2000);
        }, 4);

//    yourNumber.toString(16);
    }
)
;

app.config(function ($sceDelegateProvider, $sceProvider) {
    $sceProvider.enabled(false);
});

$(document).ready(function () {
    $('#wrapper').show();
});