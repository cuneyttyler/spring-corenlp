var app = angular.module('app', ['ngDialog']);

app.controller('Controller', function ($scope, $http, $q, $sce, $timeout, ngDialog) {

        $scope.items = [];
        $scope.entities = [];
        $scope.webPages = [];
        $scope.selectedEntities = [];

        $http.get('knowledgeBase/api/getAllMetaproperties')
            .then(function (response) {
                $scope.metaProperties = response.data;
            }, printError);

        $scope.selectedEntityHidden = true;

        $scope.hideSelectedEntity = function () {
            $scope.selectedEntityHidden = true;

            $('.property-box-column').hide();
            $('.right').hide();
            $('.memory-box').removeClass('col-md-8');
            $('.memory-box').addClass('col-md-11');
            $('.space').animate({width: '100%'});
        };

        $scope.showSelectedEntity = function () {
            $scope.selectedEntityHidden = false;

            $('.property-box-column').show();
            $('.right').show();
            $('.memory-box').addClass('col-md-8');
            $('.memory-box').removeClass('col-md-11');
            $('.space').animate({width: '80%'});
        };

        $http.get('knowledgeBase/api/getLoggedInUser')
            .then(function (response) {
                $scope.user = response.data;

                $http.get('knowledgeBase/api/getEntityList/' + $scope.user.id)
                    .then(function (response) {
                        response.data.forEach(function (item) {

                            $scope.items.push(item);

                            if (item.entityType == 'web-page') {
                                $scope.webPages.push(item);
                                $scope.entities.push(item);
                            } else {
                                $scope.entities.push(item);
                            }
                        });

                        $http.get('knowledgeBase/api/getAllTopics/' + $scope.user.id)
                            .then(function (response) {
                                $scope.topics = response.data;
                                $scope.topics.unshift({id: -1, name: 'Other', categories: [], entities: []});
                                $scope.topics[0].selected = 'selected';

                                _.each($scope.topics, function (t) {
                                    if (t.id != -1) {
                                        t.categories.unshift({id: -2, name: 'Other', subCategories: [], entities: []}); // second element
                                        t.categories.unshift({id: -1, name: 'All', subCategories: [], entities: []}); // second element
                                        t.categories[0].selected = 'selected';
                                    }

                                    for (var i = 0; i < t.categories.length; i++) {
                                        if (t.categories[i].id != -1 && t.categories[i].id != -2) {
                                            t.categories[i].subCategories.unshift({id: -2, name: 'Other'}); // second element
                                            t.categories[i].subCategories.unshift({id: -1, name: 'All'}); // first element
                                        }
                                    }
                                });

                                $scope.categorizeEntities();

                                // Initially context menu items are set to all topics, because initial selected topic is other
                                // and has no subcategories...
                                $timeout(function () {
                                    $scope.memoryBoxContextMenuItems = [];
                                    _.each($scope.topics, function (t) {
                                        if (t.id != -1) {
                                            $scope.memoryBoxContextMenuItems.push({type: 'topic', item: t});
                                        }
                                    });

                                    configureMemoryBoxContextMenu();
                                }, 0);

                                $scope.selectedTopicToShow = $scope.topics[0];
//                                $scope.selectedTopicToShow.selectedCategory = $scope.topics[0].categories[0];
//                                $scope.selectedTopicToShow.selectedCategory.selectedSubCategory = $scope.topics[0].categories[0].subCategories[0];

                            }, printError);
                    }, printError);
            }, printError);

        $scope.logout = function () {
            $http.get('knowledge-base/knowledgeBase/j_spring_security_logout')
                .then(function (response) {
                    window.location.href = 'knowledge-base';
                }, printError);
        };

        $scope.categorizeEntities = function () {
            $scope.getEntitiesByTopic();
            $scope.getEntitiesByCategory();
            $scope.getEntitiesBySubCategory();
        };

        $scope.getEntities = function () {
            if (!$scope.selectedTopicToShow) return;
            if ($scope.selectedTopicToShow.id == -1) {
                return $scope.selectedTopicToShow.entities;
            } else if ($scope.selectedTopicToShow.selectedCategory.id == -1 || $scope.selectedTopicToShow.selectedCategory.id == -2) {
                return $scope.selectedTopicToShow.selectedCategory.entities;
            } else {
                return $scope.selectedTopicToShow.selectedCategory.selectedSubCategory.entities;
            }

        };

        function setSideMenuItemWidths() {

            $('.side-menu-item-wrapper a span').css('margin-left', function () {
                var lineHeight = $(this).height();

                if (lineHeight > 55) {
                    var margin = $(this).parent().parent().width() -
                        ($(this).parent().parent().width() - lineHeight);
                    margin = -margin / 4;

                    return margin + 'px';
                } else {
                    return '0px';
                }
            });

            $('.side-menu-item-wrapper a ').css('line-height', function () {
                if ($(this).find('span').html() == '+')
                    return 0;
                else
                    return $(this).find('span').height() + 'px';
            });

        }

        $timeout(function () {
            setSideMenuItemWidths();
        }, 1000);

        autocompleteService.configureEntity($scope, $http, $q, afterEntityResponseFetched);
        autocompleteService.configureCategory($scope);

        function afterEntityResponseFetched(response) {
            $scope.propertiesLoading = false;
            $('body').css('cursor', 'default');

            $scope.selectedEntity = response;
            for (var i = 0; i < $scope.selectedEntity.properties.length; i++) {
                var p = $scope.selectedEntity.properties[i];

                var mp = _.find($scope.metaProperties, {uri: p.uri});
                if (mp)
                    p.visibility = mp.visibility; // Add filter to the view
                else
                    p.visibility = 0;
            }

            $scope.selectedEntity.propertyGroups = [];
            groupProperties($scope.selectedEntity);

            $scope.selectedEntities.push($scope.selectedEntity);
            $scope.items.push($scope.selectedEntity);

            $scope.existingElements = getExistingProperties();

            $scope.infoSummary = $scope.getInfoSummary($scope.selectedEntity);

            if ($scope.selectedSubCategory)
                $('#entity-subcategory-input').val($scope.selectedSubCategory.name);
            if ($scope.selectedCategory)
                $('#entity-category-input').val($scope.selectedCategory.name);

            $scope.showSelectedEntity();
        }

        $scope.getBirthDate = function (entity) {
            var birthDateArray = _.filter(entity.properties, function (p) {
                return p.uri == 'http://dbpedia.org/property/birthDate'; // http://dbpedia.org/ontology/birthDate https://www.wikidata.org/wiki/Property:P569
            });

            if (birthDateArray.length == 0) {
                birthDateArray = _.filter(entity.properties, function (p) {
                    return p.uri == 'https://www.wikidata.org/wiki/Property:P569'; // http://dbpedia.org/ontology/birthDate https://www.wikidata.org/wiki/Property:P569
                });
            }

            var betterBirthDateString = '';
            if (birthDateArray.length > 0) {
                if (new Date(birthDateArray[0].value) == 'Invalid Date') {
                    betterBirthDateString = birthDateArray[0].value;
                } else {
                    var birthDate = new Date(birthDateArray[0].value);
                    betterBirthDateString = formatDate(birthDate);
                }
            }

            return betterBirthDateString;
        }

        $scope.getBirthPlace = function (entity) {
            var birthPlaceArray = _.filter(entity.properties, function (p) {
                return p.uri == 'https://www.wikidata.org/wiki/Property:P19'
            });

            var birthPlace = '';
            if (birthPlaceArray.length > 0)
                birthPlace = birthPlaceArray[0].valueLabel;

            return birthPlace;
        }

        $scope.isPerson = function (entity) {
            var personProperty = _.filter(entity.properties, function (p) {
                return p.uri == 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type' &&
                    (p.value == 'http://xmlns.com/foaf/0.1/Person' || p.value == 'http://dbpedia.org/ontology/Person');
            });

            return personProperty && personProperty.length > 0;
        }

        $scope.getInfoSummary = function (entity) {
            if ($scope.isPerson(entity)) {

                var birthDate = $scope.getBirthDate(entity);
                var birthPlace = $scope.getBirthPlace(entity);

                var infoSummary = birthDate;
                if (infoSummary.length > 0 && birthPlace.length > 0)
                    infoSummary += ', ';

                if (birthPlace.length > 0)
                    infoSummary += birthPlace;

                return infoSummary;
            }
        };

        $scope.addTopic = function () {
            ngDialog.open({
                template: '../static/knowledge-base/pop-ups/add-topic-pop-up.html',
                controller: ['$scope', function ($ss) {
                    $ss.add = function () {
                        $http.get('knowledgeBase/api/saveTopic/' + $ss.topicName)
                            .then(function (response) {
                                $scope.topics.push({id: response.data, name: $ss.topicName});
                                $ss.closeThisDialog();
                                $timeout(function () {
                                    setSideMenuItemWidths();
                                }, 0);
                            }, function (err) {
                                printError(err);
                            });
                    }
                }]});
        };

        $scope.addCategory = function () {
            ngDialog.open({
                template: '../static/knowledge-base/pop-ups/add-category-pop-up.html',
                controller: ['$scope', function ($ss) {
                    $ss.add = function () {
                        $http.get('knowledgeBase/api/saveCategory/' + $scope.selectedTopicToShow.id + '/' + $ss.categoryName)
                            .then(function (response) {
                                $scope.selectedTopicToShow.categories.push({id: response.data, name: $ss.categoryName, subCategories:[]});
                                $ss.closeThisDialog();
                                $timeout(function () {
                                    setSideMenuItemWidths();
                                }, 0);
                            }, function (err) {
                                printError(err);
                            });
                    }
                }]});
        };

        $scope.addSubcategory = function () {
            ngDialog.open({
                template: '../static/knowledge-base/pop-ups/add-subcategory-pop-up.html',
                controller: ['$scope', function ($ss) {
                    $ss.add = function () {
                        $http.get('knowledgeBase/api/saveSubcategory/' + $scope.selectedTopicToShow.selectedCategory.id + '/' + $ss.subcategoryName)
                            .then(function (response) {
                                $scope.selectedTopicToShow.selectedCategory.subCategories.push(
                                    {id: response.data, categoryId: $scope.selectedTopicToShow.selectedCategory.id, name: $ss.subcategoryName});
                                $ss.closeThisDialog();
                            }, function (err) {
                                printError(err);
                            });
                    }
                }]});
        };

        $scope.entityDoubleClicked = function (e) {
            $scope.doubleClicked = true;

            $scope.showEntity(e, $scope.openDetailPopup);

            setTimeout(function () {
                $scope.doubleClicked = false;
            }, 300);
        };

        $scope.categoryName = function () {
            if (!$scope.selectedEntity.categories || $scope.selectedEntity.categories.length == 0)
                return "Other";
            else
                return $scope.selectedEntity.categories[1];
        };

        $scope.entityClicked = function (e) {
            setTimeout(function () {
                if (!$scope.doubleClicked) {
                    if ($scope.previousSelectedEntity && e.id == $scope.previousSelectedEntity.id) {
                        $scope.hideSelectedEntity();
                        $scope.previousSelectedEntity = null;
                        return;
                    }

                    $scope.previousSelectedEntity = e;

                    $scope.showEntity(e);
                    $scope.showSelectedEntity();
                }
            }, 300);
        };

        $scope.showEntity = function (e, callback) {
            $scope.showProperties = 0;

            $http.get('knowledgeBase/api/getEntityById/' + e.id)
                .then(function (response) {

                    var entity = response.data;

                    if (entity.properties && entity.properties.length > 0)
                        groupProperties(entity);

                    $scope.selectedEntity = entity;
                    $scope.selectedEntities = [entity];

                    if ($scope.selectedEntity.entityType == 'web-page') {
                        $scope.existingEntities = getExistingEntities();
                    } else {
                        $scope.existingElements = getExistingProperties();
                    }

                    if (callback)
                        callback(e);
                }, function (err) {
                    printError(err);
                });
        };

        $scope.saveEntity = function (callFromSubScope) {
            var pg = $scope.selectedEntity.propertyGroups;
            delete $scope.selectedEntity.propertyGroups;

            $scope.selectedEntity.topics = [];
            $scope.selectedEntity.categories = [];
            $scope.selectedEntity.subCategories = [];

            if ($scope.selectedTopic)
                $scope.selectedEntity.topics.push({id: $scope.selectedTopic.id, name: $scope.selectedTopic.value}); // id , value

            if ($scope.selectedTopic && $scope.selectedTopic.selectedCategory && $scope.selectedTopic.selectedCategory.id != -1 && $scope.selectedTopic.selectedCategory.id != -2)
                $scope.selectedEntity.categories.push({id: $scope.selectedTopic.selectedCategory.id, name: $scope.selectedTopic.selectedCategory.value}); // id , value

            if ($scope.selectedTopic && $scope.selectedTopic.selectedCategory && $scope.selectedTopic.selectedCategory.selectedSubCategory && $scope.selectedTopic.selectedCategory.selectedSubCategory.id != -1 && $scope.selectedTopic.selectedCategory.selectedSubCategory.id != -2)
                $scope.selectedEntity.subCategories.push({id: $scope.selectedTopic.selectedCategory.selectedSubCategory.id, name: $scope.selectedTopic.selectedCategory.selectedSubCategory.value}); // id , value

            $scope.selectedEntity.source = "memory-item";

            $scope.propertiesLoading = true;
            setTimeout(function () {
                $scope.propertiesLoading = false;
            }, 1000);
            $http.post('knowledgeBase/api/saveEntity', $scope.selectedEntity)
                .then(function (response) {
                    $scope.saveResponse = 'Saved';

                    $scope.items.push(response.data);
                    $scope.entities.push(response.data);

                    $scope.selectedEntity = response.data;
                    $scope.selectedEntity.saved = true;
                    $scope.selectedEntity.propertyGroups = pg;

                    $scope.entityImage = $scope.selectedEntity.image;

                    if (callFromSubScope)
                        callFromSubScope($scope.selectedEntity);

                    $scope.categorizeEntities();
                }, function (err) {
                    printError(err);
                    $scope.saveResponse = err;
                    $scope.propertiesLoading = false;
                });
        };

        $scope.isPropertyLink = function (property) {
            return (property.source == "wikidata" && (property.datatype == "wikibase-item") || (property.datatype == "external-id"))
                || (property.source == "dbpedia" && isUrl(property.value))
                || (property.source == "custom" && isUrl(property.value));
        };

        $scope.isDbpediaResource = function (property) {
            return property.value.indexOf('dbpedia') != -1;
        };

        $scope.goTo = function (property) {
            if (property.source == "wikidata" && property.datatype == "wikibase-item") {
                return "https://www.wikidata.org/wiki/" + property.value;
            } else if (property.source == "dbpedia" && isUrl(property.value)) {
                return property.value;
            } else {
            }
        };

        $scope.openProperty = function (property) {
            $scope.showProperties = 0;
            $scope.selectedEntity = {name: property.valueLabel};

            var e = _.find($scope.entities, {dbpediaUri: property.value}); // is property(entity) included in database?
            e = e ? e : _.find($scope.entities, {wikidataId: property.value});

            var ee = _.find($scope.selectedEntities, {dbpediaUri: property.value}); // is it included in selected list?
            ee = ee ? ee : _.find($scope.selectedEntities, {wikidataId: property.value});

            if (ee) {
                var i = _.indexOf($scope.selectedEntities, ee);

                for (var j = 0; j < $scope.selectedEntities.length - i; j++) {
                    $scope.selectedEntities.pop();
                }

                $scope.selectedEntity = ee;

                return;
            } else if (e) {
                $http.get('knowledgeBase/api/getEntityById/' + e.id)
                    .then(function (response) {
                        var entity = response.data;
                        groupProperties(entity);

                        $scope.selectedEntity = entity;
                        $scope.selectedEntities.push(entity);

                        $scope.existingElements = getExistingProperties();
                    }, function (err) {
                        printError(err);
                    });
                return;
            } else {
                $scope.propertiesLoading = true;

                lodService.fetchPropertyFromRelevantSource($http, $q, property, afterEntityResponseFetched, function () {
                    $scope.propertiesLoading = false;
                });
            }
        };

        $scope.isEntitySaved = function () {
            if (!$scope.selectedEntity) {
                return true;
            }

            var e = _.find($scope.entities, {dbpediaUri: $scope.selectedEntity.dbpediaUri});

            return e !== null && e !== undefined;
        };

        $scope.openLastEntity = function () {
            $scope.selectedEntity = $scope.selectedEntities[$scope.selectedEntities.length - 2];
            $scope.selectedEntities.pop();
        };

        $scope.openLastSecondEntity = function () {
            $scope.selectedEntity = $scope.selectedEntities[$scope.selectedEntities.length - 3];
            $scope.selectedEntities.pop();
            $scope.selectedEntities.pop();
        };

        $scope.propertyValue = function (property) {
            if (!property.valueLabel.isEmpty) {
                return property.valueLabel;
            } else {
                return property.value;
            }
        };

        $scope.showOnlyCustomProperties = function () {
            $('.property-filter .filter a.custom').addClass('selected');
            $('.property-filter .filter a.filtered').removeClass('selected');
            $('.property-filter .filter a.all').removeClass('selected');

            $scope.showProperties = 2;
        };

        $scope.showOnlyFilteredProperties = function () {
            $('.property-filter .filter a.custom').removeClass('selected');
            $('.property-filter .filter a.filtered').addClass('selected');
            $('.property-filter .filter a.all').removeClass('selected');

            $scope.showProperties = 0;
        };

        $scope.showAllProperties = function () {
            $('.property-filter .filter a.custom').removeClass('selected');
            $('.property-filter .filter a.filtered').removeClass('selected');
            $('.property-filter .filter a.all').addClass('selected');

            $scope.showProperties = 1;
        };

        $scope.showProperties = 0; // 0: filtered, 1: all, 2: custom
        $scope.filteredProperties = function () {
            if (!$scope.selectedEntity || !$scope.selectedEntity.propertyGroups) {
                return [];
            }

            var props = [];
            for (var i = 0; i < $scope.selectedEntity.propertyGroups.length; i++) {
                var prop = $scope.selectedEntity.propertyGroups[i];

                var push = false;
                if ($scope.showProperties == 1 && prop.visibility != 0)
                    push = true;
                else if ($scope.showProperties == 0 && prop.visibility == 3)
                    push = true;
                else if (prop.source == 'custom')  // 2
                    push = true;

                var p = $scope.filterValuesBySearch(prop, $scope.propertySearchFilter);
                if (push && p)
                    props.push(p);

            }

            return props;
        };

        $scope.setExternalUrls = function (propertyGroup) {
            for (var j = 0; j < propertyGroup.values.length; j++) {
                propertyGroup.values[j].externalUrl = $scope.getExternalUrl(propertyGroup.values[j]);
            }
        };

        $scope.getExternalUrl = function (propertyValue) {
            if (!propertyValue.metaProperty) { // not fetched from database
                propertyValue.metaProperty = _.find($scope.metaProperties, {uri: propertyValue.uri});
            }

            if (propertyValue.metaProperty)
                return propertyValue.metaProperty.externalSourceUri + propertyValue.value;
            else
                return'';
        };

        $scope.filterValuesBySearch = function (pg, filter) {
            if (!filter || filter.length == 0)
                return pg;

            var filter = filter.toLowerCase();

            var property = {
                name: pg.name,
                source: pg.source,
                uri: pg.uri,
                values: [],
                visibility: pg.visibility
            };
            for (var j = 0; j < pg.values.length; j++) {
                var isUri = pg.values[j].uri ? pg.values[j].uri.toLowerCase().includes(filter) : false;
                var isValue = pg.values[j].value ? pg.values[j].value.toLowerCase().includes(filter) : false;
                var isValueLabel = pg.values[j].valueLabel ? pg.values[j].valueLabel.toLowerCase().includes(filter) : false;
                var isDescription = pg.values[j].description ? pg.values[j].description.toLowerCase().includes(filter) : false;
                var isSource = pg.values[j].source ? pg.values[j].source.toLowerCase().includes(filter) : false;
                var isName = pg.values[j].name ? pg.values[j].name.toLowerCase().includes(filter) : false;

                if (isUri || isValue || isValueLabel || isDescription || isSource || isName) {
                    property.values.push(pg.values[j]);
                }
            }

            if (property.values.length > 0) {
                return property;
            } else {
                return null
            }
        };

        $scope.image = function (e) {
            <!--<img ng-if="!isEntitySaved() && selectedEntity.image"-->
            <!--ng-src="{{selectedEntity.image}}"/>-->
            <!--<img ng-if="isEntitySaved() && selectedEntity.image"-->
            <!--ng-src="knowledgeBase/api/image/{{selectedEntity.image}}"/>-->
            <!--<img ng-if="!selectedEntity.image && selectedEntity.smallImage"-->
            <!--ng-src="knowledgeBase/api/image/{{selectedEntity.smallImage}}"/>-->

            if (!$scope.isEntitySaved() && selectedEntity.image) {
                return e.image;
            } else if ($scope.isEntitySaved() && selectedEntity.image) {
                return "knowledgeBase/api/image/" + e.image;
            } else if (!selectedEntity.image && selectedEntity.smallImage) {
                return "knowledgeBase/api/image/" + e.smallImage;
            }
        };

        $scope.showCategory = function (c) {
            if (c.hidden) {
                $('#entity-row-' + c.id).slideDown();
                c.hidden = false;
            }
            else {
                $('#entity-row-' + c.id).slideUp();
                c.hidden = true;
            }
        };

        $scope.removeEntityFromCategory = function (category) {
            var st = $scope.selectedTopicToShow;
            var sc = st.selectedCategory;
            var ssc = sc.selectedSubCategory;

            if (st.id != -1 && (!sc || sc.id == 0 || sc.id == -1 || sc.id == -2)) { // if not belonging to any category
                $http.get('knowledgeBase/api/removeEntityFromTopic?userEntityId=' + $scope.clickedEntity.userEntityId + '&topicId=' + st.id)
                    .then(function (response) {

                        var index;
                        for (var i = 0; i < $scope.clickedEntity.categories.length; i++) {
                            if ($scope.clickedEntity.categories[i].id == sc.id)
                                index = i;
                        }

                        $scope.clickedEntity.topics.splice(index, 1);

                        $scope.categorizeEntities();
                        $('.custom-context-menu.memory-box').fadeOut(200);
                    }, function (err) {
                        printError(err);
                    });
            } else if (st.id != -1 && sc.id != -1 && sc.id != -2 && (!ssc || ssc.id == 0 || ssc.id == -1 || ssc.id == -2)) { // if not belonging to any subcategory
                $http.get('knowledgeBase/api/removeEntityFromCategory?userEntityId=' + $scope.clickedEntity.userEntityId + '&categoryId=' + sc.id)
                    .then(function (response) {

                        var index;
                        for (var i = 0; i < $scope.clickedEntity.categories.length; i++) {
                            if ($scope.clickedEntity.categories[i].id == sc.id)
                                index = i;
                        }

                        $scope.clickedEntity.categories.splice(index, 1);

                        $scope.categorizeEntities();
                        $('.custom-context-menu.memory-box').fadeOut(200);
                    }, function (err) {
                        printError(err);
                    });
            } else if (sc.id != -1 && sc.id != -2 && (ssc && ssc.id != 0 && ssc.id != -1 && ssc.id != -2)) { // if belonging to any subcategory
                $http.get('knowledgeBase/api/removeEntityFromSubCategory?userEntityId=' + $scope.clickedEntity.userEntityId + '&subCategoryId=' + ssc.id)
                    .then(function (response) {
                        var index;
                        for (var i = 0; i < $scope.clickedEntity.subCategories.length; i++) {
                            if ($scope.clickedEntity.subCategories[i].id == ssc.id)
                                index = i;
                        }

                        $scope.clickedEntity.subCategories.splice(index, 1);

                        $scope.categorizeEntities();
                        $('.custom-context-menu.memory-box').fadeOut(200);
                    }, function (err) {
                        printError(err);
                    });
            } else {
                console.log("ERROR category is already in the other group!!");
            }
        };

        $scope.addEntityToCategory = function (category) {

            if (category.type == 'topic') {
                $http.get('knowledgeBase/api/addEntityToTopic?userEntityId=' + $scope.clickedEntity.userEntityId + "&topicId=" + category.item.id)
                    .then(function (response) {

                        $scope.clickedEntity.topics.push(category.item);

                        $scope.categorizeEntities();
                        $('.custom-context-menu.memory-box').fadeOut(200);
                    }, function (err) {
                        printError(err);
                    });
            } else if (category.type == 'category') {
                $http.get('knowledgeBase/api/addEntityToCategory?userEntityId=' + $scope.clickedEntity.userEntityId + "&categoryId=" + category.item.id)
                    .then(function (response) {

                        $scope.clickedEntity.categories.push(category.item);

                        $scope.categorizeEntities();
                        $('.custom-context-menu.memory-box').fadeOut(200);
                    }, function (err) {
                        printError(err);
                    });
            } else if (category.type == 'subcategory') {
                $http.get('knowledgeBase/api/addEntityToSubCategory?userEntityId=' + $scope.clickedEntity.userEntityId + "&subCategoryId=" + category.item.id)
                    .then(function (response) {
//                        $scope.selectedCategoryToShow.subCategories.push(category.item.id);
                        $scope.clickedEntity.subCategories.push(category.item);

                        $scope.categorizeEntities();
                        $('.custom-context-menu.memory-box').fadeOut(200);
                    }, function (err) {
                        printError(err);
                    });
            } else {
                console.log("ERROR memory box item does not have category type !!!!");
            }
        };

        $scope.showTopicItems = function ($event, t) {
            _.each($scope.topics, function (tt) {
                tt.selected = undefined;
            });

//            t.selected = 'selected';
            $scope.selectedTopicToShow = t;
            if (t.id != -1) {
                $scope.selectedTopicToShow.selectedCategory = t.categories[0];
                $scope.selectedTopicToShow.selectedCategory.selectedSubCategory = $scope.selectedTopicToShow.selectedCategory.subCategories[0];
            }

            // set up initial memory box context menu items as all subcategories below this category
            $scope.memoryBoxContextMenuItems = [];
            _.each($scope.selectedTopicToShow.categories, function (c) {
                if (c.id != -1 && c.id != -2) {
                    $scope.memoryBoxContextMenuItems.push({type: 'category', item: c});
                }
            });

            $timeout(function () {
                configureMemoryBoxContextMenu();
                setSideMenuItemWidths();
            }, 0);
        };

        $scope.showTopics = function () {
            $scope.selectedTopicToShow = $scope.topics[0];
            $scope.topics[0].selected = true;

            $timeout(function () {
                $scope.memoryBoxContextMenuItems = [];
                _.each($scope.topics, function (t) {
                    if (t.id != -1) {
                        $scope.memoryBoxContextMenuItems.push({type: 'topic', item: t});
                    }
                });

                configureMemoryBoxContextMenu();
            }, 0);

            $timeout(function () {
                setSideMenuItemWidths();
            }, 300);
        };

        $scope.showCategoryItems = function ($event, c) {
            $scope.selectedTopicToShow.categories.forEach(function (cc) {
                cc.selected = undefined;
            });

            c.selected = 'selected';
            $scope.selectedTopicToShow.selectedCategory = c;
            $scope.selectedTopicToShow.selectedCategory.selectedSubCategory = c.subCategories[0];

            // set up initial memory box context menu items as all subcategories below this category
            $scope.memoryBoxContextMenuItems = [];
            if ($scope.selectedTopicToShow.selectedCategory.id != -1 && $scope.selectedTopicToShow.selectedCategory.id != -2) {
                _.each($scope.selectedTopicToShow.selectedCategory.subCategories, function (sc) {
                    if (sc.id != -1 && sc.id != -2) {
                        $scope.memoryBoxContextMenuItems.push({type: 'subcategory', item: sc});
                    }
                });
            } else {
                _.each($scope.selectedTopicToShow.categories, function (c) {
                    if (c.id != -1 && c.id != -2) {
                        $scope.memoryBoxContextMenuItems.push({type: 'category', item: c});
                    }
                });
            }

            $timeout(function () {
                configureMemoryBoxContextMenu();
            }, 0);
        };

        $scope.memoryBoxContextMenuItems = [];

        function configureMemoryBoxContextMenu() {
            var selectionX, selectionY;
            $(".entity").on("mousedown", function (e) {
                selectionX = e.pageX;
                selectionY = e.pageY;
            });

            $(".entity").on("contextmenu", function (e) {
                return false;
            });

            $(".entity").bind("mousedown", function (e) {

                var id = parseInt($(this).attr('id'));
                _.each($scope.entities, function (e) {
                    if (e.id == id) {
                        $scope.clickedEntity = e;
                    }
                });

                if (e.ctrlKey || e.button == 2 || e.which == 2) {
                    $('.custom-context-menu.memory-box').css({
                        'left': selectionX + 5,
                        'top': selectionY
                    }).fadeIn(200);

                    var l = $scope.memoryBoxContextMenuItems.length;
                    var height = 26 + 26 * l;

                    if ($scope.memoryBoxContextMenuItems.length == 0 && $scope.selectedTopicToShow.categories.length > 2)
                        height -= 21;

                    if ($scope.selectedTopicToShow.id != 1 && $scope.selectedTopicToShow.categories.length > 2)
                        height += 26;

                    $('.custom-context-menu.memory-box').css('height', height);

                }


            });

            $(".entity").bind("mouseleave", function (e) {
                var t = $(".custom-context-menu.memory-box").position().top;
                var l = $(".custom-context-menu.memory-box").position().left;

                if (e.pageX < l || e.pageY < t) {
                    $('.custom-context-menu.memory-box').fadeOut(200);
                }
            });

            $(".custom-context-menu.memory-box").bind("mouseleave", function (e) {
                $('.custom-context-menu.memory-box').fadeOut(200);
            });
        }

        $scope.openNewEntityPopup = function () {
            newEntityPopup.open($scope, $http, $q, $timeout, ngDialog);
        };

        $scope.showSubCategoryItems = function ($event, sc) {

            $scope.selectedTopicToShow.selectedCategory.subCategories.forEach(function (cc) {
                cc.selected = undefined;
            });

            sc.selected = 'selected';
            $scope.selectedTopicToShow.selectedCategory.selectedSubCategory = sc;

            $scope.memoryBoxContextMenuItems = [];
            if ($scope.selectedTopicToShow.selectedCategory.id != -1 && $scope.selectedTopicToShow.selectedCategory.id != -2
                && ($scope.selectedTopicToShow.selectedCategory.selectedSubCategory.id == -1 || $scope.selectedTopicToShow.selectedCategory.selectedSubCategory.id == -2)) {
                _.each($scope.selectedTopicToShow.selectedCategory.subCategories, function (sc) {
                    if (sc.id != -1 && sc.id != -2) {
                        $scope.memoryBoxContextMenuItems.push({type: 'subcategory', item: sc});
                    }
                });
            }

            $timeout(function () {
                configureMemoryBoxContextMenu();
            }, 0);
        };

        $scope.openDetailPopup = function () {
//            if (!$scope.selectedEntity.id) {
//                return;
//            }

            detailPopup.open($scope, $http, $q, $timeout, ngDialog)
        };

        function groupProperties(entity) {
            entity.propertyGroups = [];
            for (var i = 0; i < entity.properties.length; i++) {
                var p = entity.properties[i];

                var pp = _.find(entity.propertyGroups, {uri: p.uri});
                if (pp) {
                    pp.values.push(p);
                } else {
                    entity.propertyGroups.push({uri: p.uri, name: p.name, visibility: p.visibility, values: [p], source: p.source, datatype: p.datatype});
                }
            }


            entity.propertyGroups = sortPropertyGroups(entity.propertyGroups);
        };

        function sortPropertyGroups(propertyGroups) {
            var ordered = [], lastCustomIndex = 0, lastDbpediaIndex = 0, lastWikidataIndex = 0;

            for (var i = 0; i < propertyGroups.length; i++) {
//            for (var j = i; j < propertyGroups.length; j++) {
                if (propertyGroups[i].source == 'custom') {
                    ordered.splice(lastCustomIndex++, 0, propertyGroups[i]);
                    lastDbpediaIndex++;
                    lastWikidataIndex++;
                } else if (propertyGroups[i].source == 'dbpedia') {
                    ordered.splice(lastDbpediaIndex++, 0, propertyGroups[i]);
                    lastWikidataIndex++;
                } else if (propertyGroups[i].source == 'wikidata') {
                    ordered.splice(lastWikidataIndex++, 0, propertyGroups[i]);
                }
//            }
            }

            return ordered;
        }

        function getExistingEntities() {
            if (!$scope.selectedEntity) {
                return [];
            }

            var elements = [];
            for (var i = 0; i < $scope.selectedEntity.annotationEntities.length; i++) {
                var ae = $scope.selectedEntity.annotationEntities[i];

                for (var k = 0; k < $scope.items.length; k++) {
                    var item = $scope.items[k];

                    if (ae.name == item.name) {
                        elements.push(item);
                    }
                }
            }

            return elements;
        }

        function getExistingProperties() {
            if (!$scope.selectedEntity || !$scope.selectedEntity.propertyGroups) {
                return [];
            }

            var elements = [];
            for (var i = 0; i < $scope.selectedEntity.propertyGroups.length; i++) {
                var pg = $scope.selectedEntity.propertyGroups[i];
                var newPg = {uri: pg.uri, name: pg.name, visibility: pg.visibility, values: []};

                for (var j = 0; j < pg.values.length; j++) {
                    var p = pg.values[j];

                    for (var k = 0; k < $scope.entities.length; k++) {
                        var item = $scope.entities[k];

                        if (p.value && ((p.source == 'dbpedia' && item.dbpediaUri == p.value) || (p.source == 'wikidata' && item.wikidataId == p.value))) {
                            newPg.values.push(p);
                        }
                    }
                }

                if (newPg.values.length > 0) {
                    elements.push(newPg);
                }
            }

            return elements;
        };

        $scope.getEntitiesByTopic = function () {
            $scope.topics[0].entities = [];
            // First add entities not belonging to any topic to the 'Other' topic
            for (var j = 0; j < $scope.entities.length; j++) {
                var e = $scope.entities[j];

                if (!e.topics || e.topics.length == 0)
                    $scope.topics[0].entities.push(e); // push to 'Other'

            }
        };

        $scope.getEntitiesByCategory = function () {
            for (var i = 0; i < $scope.topics.length; i++) {
                var t = $scope.topics[i];

                for (var k = 0; k < t.categories.length; k++) {
                    var c = t.categories[k];
                    c.entities = [];

                    for (var j = 0; j < $scope.entities.length; j++) {
                        var e = $scope.entities[j];

                        var includedInTopic = false;
                        _.each(e.topics, function (et) {
                            if (et.id == t.id) includedInTopic = true;
                        });

//                        if (e.topics.length == 0 && t.id == -1)
//                            includedInTopic = true;

                        var includedInCategory = false;
                        _.each(e.categories, function (ec) {
                            if (ec.id == c.id) includedInCategory = true;
                        });

                        if (c.id == -2 && includedInTopic && (!e.categories || e.categories.length == 0)) { // Other
                            c.entities.push(e);
                        } else if (c.id == -1 && includedInTopic) { // All
                            c.entities.push(e);
                        } else if (includedInCategory) {
                            c.entities.push(e);
                        }
                    }
                }
            }
        };

        $scope.getEntitiesBySubCategory = function () {

            // Then put entities to their corresponding subcategory
            for (var l = 0; l < $scope.topics.length; l++) {
                var t = $scope.topics[l];

                for (var i = 0; i < t.categories.length; i++) {
                    var c = t.categories[i];

                    for (var k = 0; k < c.subCategories.length; k++) {
                        var sc = c.subCategories[k];
                        sc.entities = [];

                        for (var j = 0; j < $scope.entities.length; j++) {
                            var e = $scope.entities[j];

                            var includedInTopic = false;
                            _.each(e.topics, function (et) {
                                if (et.id == t.id) includedInTopic = true;
                            });

                            var includedInCategory = false;
                            _.each(e.categories, function (ec) {
                                if (ec.id == c.id) includedInCategory = true;
                            });

                            if (e.categories.length == 0 && c.id == -1)
                                includedInCategory = true;

                            var includedInSubCategory = false;
                            _.each(e.subCategories, function (esc) {
                                if (esc.id == sc.id) includedInSubCategory = true;
                            });

                            if (sc.id == -2 && includedInCategory && includedInTopic && (!e.subCategories || e.subCategories.length == 0)) { // Other
                                sc.entities.push(e);
                            } else if (sc.id == -1 && includedInCategory && includedInTopic) { // All  // && includedInCategory
                                sc.entities.push(e);
                            } else if (includedInSubCategory) {
                                sc.entities.push(e);
                            }
                        }
                    }
                }
            }
        };

        $scope.getEntitiesBySubCategoryOld = function () {
            for (var l = 0; l < $scope.topics.length; l++) {
                var t = $scope.topics[l];

                for (var i = 0; i < t.categories.length; i++) {
                    var c = t.categories[i];

                    for (var k = 0; k < c.subCategories.length; k++) {
                        var sc = c.subCategories[k];
                        sc.entities = [];

                        for (var j = 0; j < $scope.entities.length; j++) {
                            var e = $scope.entities[j];

                            var includedInCategory = false;
                            _.each(e.categories, function (ec) {
                                if (ec.id == c.id) includedInCategory = true;
                            });

                            if (e.categories.length == 0 && c.id == -1)
                                includedInCategory = true;

                            var includedInSubCategory = false;
                            _.each(e.subCategories, function (esc) {
                                if (esc.id == sc.id) includedInSubCategory = true;
                            });

                            if (sc.id == -2 && includedInCategory && (!e.subCategories || e.subCategories.length == 0)) { // Other
                                sc.entities.push(e);
                            } else if (sc.id == -1 && includedInCategory) { // All
                                sc.entities.push(e);
                            } else if (includedInSubCategory) {
                                sc.entities.push(e);
                            }
                        }
                    }
                }
            }
        };

        $scope.getImageUrl = function (e) {
            var prefix = "";

            if (!$scope.isEntitySaved() && e.image) {
                return e.image;
            } else if ($scope.isEntitySaved() && e.smallImage) {
                if (!isUrl(e.smallImage))
                    prefix = "knowledgeBase/api/image/";

                return prefix + e.smallImage;
            } else if (!e.smallImage && e.image) {
                if (!isUrl(e.image))
                    prefix = "knowledgeBase/api/image/";

                return prefix + e.image;
            }
        };


        $scope.mouseOver = function ($event, entity) {
            var $img = $($event.target);

            var w = $img[0].width;
            var h = $img[0].height;

            var hShrink = Math.round(h * 2 / 100);
            var wShrink = Math.round(w * 2 / 100);
            $img.animate({
                width: '-=' + wShrink,
                height: '-=' + hShrink,
                'margin-left': '+=' + wShrink,
                'margin-top': '+=' + hShrink / 2
            }, {
                duration: 300
            });

            if (entity.smallImage) {
                var $text = $img.parent().parent().find('.entity-text');
                $text.fadeIn(300);
            }

            var $overlay = $img.parent().find('.overlay');
            $overlay.css('left', '15px');
            $overlay.css('top', hShrink);
            $overlay.css('width', w);
            $overlay.css('height', h);
//        $overlay.show();
        };

        $scope.mouseLeave = function ($event, entity) {
            var $img = $($event.target);

            var w = $img[0].width;
            var h = $img[0].height;

            var hShrink = Math.round(h * 2 / 100);
            var wShrink = Math.round(w * 2 / 100);
            $img.animate({
                width: '+=' + wShrink,
                height: '+=' + hShrink,
                'margin-left': '-=' + wShrink,
                'margin-top': '-=' + hShrink / 2
            }, {
                duration: 300
            });

            if (entity.smallImage) {
                var $text = $img.parent().parent().find('.entity-text');
                $text.fadeOut(300);
            }

//        $overlay = $($event.target).parent().find('.overlay').hide();
        };
    }
);

app.config(function ($sceDelegateProvider, $sceProvider) {
    $sceProvider.enabled(false);
});

$(document).ready(function () {
    $('.wrapper').show();
});

directives.create(app)