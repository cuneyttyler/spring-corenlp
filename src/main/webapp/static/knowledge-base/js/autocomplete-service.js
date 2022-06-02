var autocompleteService = (function () {

    var cache = {};

    var configureEntity = function ($scope, $http, $q, callback) {
        $("#entity-input").autocomplete({
            minLength: 2,
            source: function (request, response) {
                var term = request.term;
                if (term in cache) {
                    response(cache[ term ]);
                    return;
                }

                dbpedia.prefixSearch($http, request.term, function (results) {
                    cache[ term ] = response;
                    response(results);
                });

            }, select: function (event, ui) {
                this.value = ui.item.value;
                $scope.searchValue = ui.item.value;

                $scope.selectedEntities = [];

                var e = _.find($scope.entities, {dbpediaUri: ui.item.uri}); // is property(entity) included in database?

                $scope.propertiesLoading = true;
                $('body').css('cursor', 'wait');
                if (e) {
                    $http.get('knowledgeBase/api/getEntityById/' + e.id)
                        .then(function (response) {
                            callback(response.data);
                        }, function (err) {
                            printError(err);
                        });
                    return;
                } else {
                    lodService.getItem($http, $q, ui.item.resource[0], callback);
                }

            }, change: function (event, ui) { // not-selected
                if (ui.item === null) {
                    $scope.$apply(function () {
                        if (isUrl($("#entity-input").val())) {
                            $scope.selectedEntity = {
                                name: '',
                                webUri: $("#entity-input").val(),
                                entityType: 'web-page'
                            };
                        } else {
                            $scope.selectedEntity = {
                                name: $("#entity-input").val(),
                                entityType: 'non-semantic-web'
                            }
                        }
                    });
                }
            }
        });
    };

    var configureCustomObject = function (entities, $http, $q, callback, changeCallback) {
        $("#custom-object-input").autocomplete({
            minLength: 2,
            source: function (request, response) {
                var term = request.term;
                if (term in cache) {
                    response(cache[ term ]);
                    return;
                }

                dbpedia.prefixSearch($http, request.term, function (results) {
                    cache[ term ] = response;
                    response(results);
                });

            }, select: function (event, ui) {
                this.value = ui.item.value;

                var e = _.find(entities, {dbpediaUri: ui.item.uri}); // is property(entity) included in database?

                if (e) {
                    $http.get('knowledgeBase/api/getEntityById/' + e.id)
                        .then(function (response) {
                            callback(response.data);
                        }, function (err) {
                            printError(err);
                        });
                    return;
                } else {
                    lodService.getItem($http, $q, ui.item.uri, callback);
                }

            }, change: function (event, ui) { // not-selected
                if (ui.item === null) {
                    var r = {name: $('#custom-object-input').val(), entityType: 'non-semantic-web'};

                    callback(r.name == '' ? null : r);
                } else {
                }
            }
        });
    };

    var configureCustomProperty = function ($http, $q, callback, changeCallback) {
        $("#custom-property-input").autocomplete({
            minLength: 2,
            source: function (request, response) {
                var term = request.term;
                if (term in cache) {
                    response(cache[ term ]);
                    return;
                }

                $http.get('knowledgeBase/api/autocompleteProperty/' + request.term)
                    .then(function (dbResponse) {
                        cache[ term ] = dbResponse.data;

                        var results = [];
                        for (var i = 0; i < dbResponse.data.length; i++) {
                            results.push({id: dbResponse.data[i].id, value: dbResponse.data[i].name, object: dbResponse.data[i]});
                        }

                        response(results);

                    }, function (err) {
                        printError(err);
                    });
                return;

            }, select: function (event, ui) {
                this.value = ui.item.value;

                callback(ui.item.object);

            }, change: function (event, ui) { // not-selected
                if (ui.item === null) {
                    var r = {name: $('#custom-property-input').val(), source: 'custom'};
                    callback(r.name == '' ? null : r);
                } else {
                }
            }
        });
    };

    var configureCategory = function ($scope, $ss) {

        $scope.$watch(function () {
                if (angular.element("#popup-entity-topic-input").is(':visible')
                    || angular.element("#entity-topic-input").is(':visible')
                    || angular.element("#side-entity-topic-input").is(':visible')) {

                    $("#popup-entity-topic-input, #entity-topic-input, #side-entity-topic-input").val(function () {
                        if (!$scope.isEntitySaved() && $scope.selectedTopicToShow.id != -1) {
                            $scope.selectedTopic = $scope.selectedTopicToShow;
                            return $scope.selectedTopic.name;
                        } else if ($scope.isEntitySaved() && $scope.selectedEntity.topics.length > 0) {
                            return $scope.selectedEntity.topics[0].name;
                        }
                    });

                    $("#popup-entity-topic-input, #entity-topic-input, #side-entity-topic-input").autocomplete({
                        minLength: 2,
                        source: function (request, response) {
                            var topics = [];
                            for (var i = 0; i < $scope.topics.length; i++) {
                                var t = $scope.topics[i];

                                if (t.name.toLowerCase().indexOf(request.term.toLowerCase()) != -1)
                                    topics.push({id: t.id, label: t.name, value: t.name, categories: t.categories});
                            }

                            response(topics);
                        }, select: function (event, ui) {
                            if (!_.find($scope.topics, {name: ui.item.value})) {
                                ui.item.value = _.find($scope.topics, {name: 'Other'}).name;
                            }

                            $("#category-input").val(ui.item.value);
                            this.value = ui.item.value;

                            if ($(this).attr("id") == "entity-topic-input" || $(this).attr("id") == "popup-entity-topic-input")
                                $scope.selectedTopic = {id: ui.item.id, value: ui.item.value, categories: ui.item.categories};
                            else
                                $ss.selectedSideTopic = {id: ui.item.id, value: ui.item.value, categories: ui.item.categories};


                            return false;
                        }, change: function (event, ui) { // not-selected
                        }
                    });
                }
                return;
            }
            , function () {
            });


        $scope.$watch(function () {
                $("#popup-entity-category-input, #entity-category-input, #side-entity-category-input").val(function () {
                    if (!$scope.isEntitySaved() && $scope.selectedTopicToShow.id != -1
                        && $scope.selectedTopicToShow.selectedCategory
                        && $scope.selectedTopicToShow.selectedCategory.id != -1
                        && $scope.selectedTopicToShow.selectedCategory.id != -2) {
                        $scope.selectedTopic = $scope.selectedTopicToShow;
                        return $scope.selectedTopic.selectedCategory.name;
                    } else if ($scope.isEntitySaved() && $scope.selectedEntity.categories.length > 0) {
                        return $scope.selectedEntity.categories[0].name;
                    }
                });

                if (angular.element("#popup-entity-category-input").is(':visible')
                    || angular.element("#entity-category-input").is(':visible')
                    || angular.element("#side-entity-category-input").is(':visible')) {

                    $("#popup-entity-category-input, #entity-category-input, #side-entity-category-input").autocomplete({
                        minLength: 2,
                        source: function (request, response) {
                            var topic;
                            if ($($(this)[0].element).attr("id") == "entity-category-input" || $($(this)[0].element).attr("id") == "popup-entity-category-input")
                                topic = $scope.selectedTopic;
                            else
                                topic = $ss.selectedSideTopic;

                            var cats = [];
                            if (!topic || !topic.selectedCategory) {
                                _.each($scope.topics, function (t) {
                                    _.each(t.categories, function (c) {
                                        if (c.name.toLowerCase().indexOf(request.term.toLowerCase()) != -1 && c.id != -1 && c.id != -2) {
                                            cats.push({id: c.id, label: c.name, value: c.name, subCategories: c.subCategories});
                                        }
                                    });
                                });
                            } else {
                                for (var i = 0; i < topic.categories.length; i++) {
                                    var c = topic.categories[i];

                                    if (c.name.toLowerCase().indexOf(request.term.toLowerCase()) != -1)
                                        cats.push({id: c.id, label: c.name, value: c.name, subCategories: c.subCategories});
                                }
                            }

                            response(cats);
                        }, select: function (event, ui) {
                            var topic;
                            if ($(this).attr("id") == "entity-category-input" || $(this).attr("id") == "popup-entity-category-input")
                                topic = $scope.selectedTopic;
                            else
                                topic = $ss.selectedSideTopic;

                            if (!_.find(topic.categories, {name: ui.item.value})) {
                                ui.item.value = _.find(topic.categories, {name: 'Other'}).name;
                            }

                            $("#category-input").val(ui.item.value);
                            this.value = ui.item.value;

                            topic.selectedCategory = {id: ui.item.id, value: ui.item.value, subCategories: ui.item.subCategories};

                            return false;
                        }, change: function (event, ui) { // not-selected
                            $("#category-input").val($("#entity-category-input").val());
                        }
                    });
                }
                return;
            }
            , function () {
            });

        $scope.$watch(function () {
                $("#popup-entity-subcategory-input, #entity-subcategory-input, #side-entity-subcategory-input").val(function () {
                    if (!$scope.isEntitySaved() && $scope.selectedTopicToShow.id != -1
                        && $scope.selectedTopicToShow.selectedCategory
                        && $scope.selectedTopicToShow.selectedCategory.selectedSubCategory
                        && $scope.selectedTopicToShow.selectedCategory.selectedSubCategory.id != -1
                        && $scope.selectedTopicToShow.selectedCategory.selectedSubCategory.id != -2) {
                        $scope.selectedTopic = $scope.selectedTopicToShow;
                        return $scope.selectedTopic.selectedCategory.selectedSubCategory.name;
                    } else if ($scope.isEntitySaved() && $scope.selectedEntity.subCategories.length > 0) {
                        return $scope.selectedEntity.subCategories[0].name;
                    }
                });

                if (angular.element("#popup-entity-subcategory-input").is(':visible')
                    || angular.element("#entity-subcategory-input").is(':visible')
                    || angular.element("#side-entity-subcategory-input").is(':visible')) {

                    $("#popup-entity-subcategory-input, #entity-subcategory-input, #side-entity-subcategory-input").autocomplete({
                        minLength: 2,
                        source: function (request, response) {
                            var topic;
                            if ($($(this)[0].element).attr("id") == "entity-subcategory-input" || $($(this)[0].element).attr("id") == "popup-entity-subcategory-input")
                                topic = $scope.selectedTopic;
                            else
                                topic = $ss.selectedSideTopic;

                            var subcats = [];
                            if (!topic || !topic.selectedCategory) {
                                _.each($scope.topics, function (t) {
                                    _.each(t.categories, function (c) {
                                        c.topic = t;
                                        _.each(c.subCategories, function (sc) {
                                            if (sc.name.toLowerCase().indexOf(request.term.toLowerCase()) != -1 && c.id != -1 && c.id != -2) {
                                                subcats.push({id: sc.id, label: sc.name, value: sc.name, category: c});
                                            }
                                        });
                                    });
                                });
                            } else {
                                for (var i = 0; i < topic.selectedCategory.subCategories.length; i++) {
                                    var sc = topic.selectedCategory.subCategories[i];

                                    var c = topic.selectedCategory;
                                    c.topic = t;
                                    if (sc.categoryId == topic.selectedCategory.id && sc.name.toLowerCase().indexOf(request.term.toLowerCase()) != -1)
                                        subcats.push({id: sc.id, label: sc.name, value: sc.name, category: c});
                                }
                            }

                            response(subcats);
                        }, select: function (event, ui) {
                            var topic;
                            if ($(this).attr("id") == "entity-subcategory-input" || $(this).attr("id") == "popup-entity-subcategory-input")
                                topic = $scope.selectedTopic;
                            else
                                topic = $ss.selectedSideTopic;

                            if (!topic) {
                                topic = ui.item.category.topic;
                                topic.selectedCategory = ui.item.category;
                            } else if (!topic.selectedCategory){
                                topic.selectedCategory = ui.item.category;
                            }

                            if (!_.find(topic.selectedCategory.subCategories, {name: ui.item.value})) {
                                ui.item.value = _.find(topic.selectedCategory.subCategories, {name: 'Other'}).name;
                            }

                            $("#subcategory-input").val(ui.item.value);
                            this.value = ui.item.value;

                            topic.selectedCategory.selectedSubCategory = {id: ui.item.id, value: ui.item.value};

                            return false;

                        }, change: function (event, ui) { // not-selected
                            $("#subcategory-input").val($("#entity-subcategory-input").val());
                        }
                    });
                }
                return;
            }
            , function () {
            });
    };

    var configureNewEntityCategory = function ($scope) {

        $("#new-category-input").autocomplete({
            minLength: 2,
            source: function (request, response) {
                var cats = [];
                for (var i = 0; i < $scope.categories.length; i++) {
                    var c = $scope.categories[i];

                    if (c.name.toLowerCase().indexOf(request.term.toLowerCase()) != -1)
                        cats.push({id: c.id, label: c.name, value: c.name, subCategories: c.subCategories});
                }

                response(cats);
            }, select: function (event, ui) {
                this.value = ui.item.value;

                $scope.selectedCategory = {id: ui.item.id, value: ui.item.value, subCategories: ui.item.subCategories};

                console.log(JSON.stringify(ui.item));

                return false;
            }, change: function (event, ui) { // not-selected
            }
        });

        $("#new-subcategory-input").autocomplete({
            minLength: 2,
            source: function (request, response) {
                var subcats = [];
                for (var i = 0; i < $scope.selectedCategory.subCategories.length; i++) {
                    var sc = $scope.selectedCategory.subCategories[i];

                    if (sc.categoryId == $scope.selectedCategory.id && sc.name.toLowerCase().indexOf(request.term.toLowerCase()) != -1)
                        subcats.push({id: sc.id, label: sc.name, value: sc.name});
                }

                response(subcats);
            }, select: function (event, ui) {
                this.value = ui.item.value;

                $scope.selectedSubCategory = {id: ui.item.id, value: ui.item.value};

                console.log(JSON.stringify(ui.item));

                return false;
            }, change: function (event, ui) { // not-selected
            }
        });


    }

    return {
        configureCategory: configureCategory,
        configureEntity: configureEntity,
        configureCustomObject: configureCustomObject,
        configureCustomProperty: configureCustomProperty,
        configureNewEntityCategory: configureNewEntityCategory
    }
})();