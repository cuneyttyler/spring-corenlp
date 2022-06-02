var newEntityPopup = (function () {

    var open = function($scope,$http,$q, $timeout,ngDialog) {
        ngDialog.open({
            template: '../static/knowledge-base/pop-ups/new-entity-pop-up.html',
            controller: ['$scope', function ($ss) {

                $ss.categories = $scope.categories;
                $ss.entity = {properties: [], propertyGroups: []};

                $ss.showCustomProperties = function () {
                    $('.add-custom-properties').hide();
                    $('.show-custom-properties').show();

                    $('.custom-property-add-title .show').removeClass('not-selected');
                    $('.custom-property-add-title .add').addClass('not-selected');
                };

                $ss.addCustomProperties = function () {
                    $('.show-custom-properties').hide();
                    $('.add-custom-properties').show();

                    $('.custom-property-add-title .add').removeClass('not-selected');
                    $('.custom-property-add-title .show').addClass('not-selected');
                };

                $ss.openImage = function (entity, image) {
                    if (!entity || !(entity.image || entity.smallImage))
                        return;

                    ngDialog.open({
                        template: '../static/knowledge-base/pop-ups/image-pop-up.html',
                        className: 'ngdialog-image-popup',
                        controller: ['$scope', function ($sss) {
                            $sss.isEntitySaved = function () {
                                if (!$sss.selectedEntity) {
                                    return true;
                                }

                                var e = _.find($scope.entities, {dbpediaUri: $sss.selectedEntity.dbpediaUri});

                                return e !== null && e !== undefined;
                            };

                            $sss.selectedEntity = entity;
                            $sss.image = image;

                            $scope.$on('ngDialog.opened', function (e, $dialog) {
                                $('.ngdialog-image-popup').css('cursor', 'wait');
                                $timeout(function () {

                                    $(".ngdialog-image-popup .pop-up img").on('load', function () {
                                        $('.ngdialog-image-popup').css('cursor', 'default');
                                        var margin = $(window).width() / 2 - $(".ngdialog-image-popup .pop-up img").width() / 2;
                                        $(".ngdialog-image-popup .ngdialog-content img").css('margin-left', margin);
                                        $(".ngdialog-image-popup .pop-up img").show();
                                    });
                                });
                            });

                            $sss.close = function () {
                                $sss.closeThisDialog();
                            }
                        }]});
                };

                function setEditTopicMargin() {
                    $timeout(function () {
                        $(".sub-topic .info-image img").on('load', function () {
                            var marginTop = $('.sub-topic').height() - $('.edit-topic').height() - $('.topic').height();
                            if (marginTop < 0) marginTop = 0;
                            $('.edit-topic').css('margin-top', marginTop);
                        });
                    }, 0);
                };

                $scope.$on('ngDialog.opened', function (e, $dialog) {
                    $ss.addCustomProperties();

                    autocompleteService.configureCustomObject($scope.entities, $http, $q, function (response) {
                        setEditTopicMargin();

                        if (response == null || response.entityType == 'non-semantic-web') {
                            $scope.$apply(function () {
                                $ss.customObject = response;
                                $ss.selectedSideEntity = response;
                                $ss.sideEntityImage = $ss.selectedSideEntity.image;
                                $('.pop-up .block.new-entity').css('display', 'inline-flex');
                            });
                        } else {
                            $ss.customObject = response;
                            $ss.selectedSideEntity = response;
                            $ss.sideEntityImage = $ss.selectedSideEntity.image;
                            $('.pop-up .block.new-entity').css('display', 'inline-flex');
                        }
                    });

                    autocompleteService.configureCustomProperty($http, $q, function (response) {
                        $scope.$apply(function () {
                            $ss.customProperty = response;

                            _.each($ss.entity.propertyGroups, function (pg) {
                                if ($ss.customProperty.uri == pg.uri) {
                                    $ss.selectedCustomProperty = pg;
                                }
                            });
                        });
                    });

                    autocompleteService.configureNewEntityCategory($ss);

                    $ss.saveEntity = function () {
                        var e = _.find($scope.entities, {name: $ss.entity.name});
                        if (e) {
                            ngDialog.open({
                                template: '../static/knowledge-base/pop-ups/prompt-pop-up.html',
                                controller: ['$scope', function ($sss) {
                                    $sss.message = 'An item with this name already exists.';
                                    setTimeout(function () {
                                        $sss.closeThisDialog();
                                    }, 1500);

                                }]});

                            return;
                        }

                        if (!$ss.entity.name || $ss.entity.length == 0 || !$ss.entity.description || $ss.entity.description.length == 0) {
                            ngDialog.open({
                                template: '../static/knowledge-base/pop-ups/prompt-pop-up.html',
                                controller: ['$scope', function ($sss) {
                                    $sss.message = 'Title and description field must not be empty.';
                                    setTimeout(function () {
                                        $sss.closeThisDialog();
                                    }, 1500);

                                }]});

                            return;
                        }

                        var pg = $ss.entity.propertyGroups;
                        delete $ss.entity.propertyGroups;

                        if ($scope.selectedCategory) {
                            $ss.entity.categoryId = $scope.selectedCategory.id;
                            $ss.entity.categoryName = $scope.selectedCategory.value;
                        } else {
                            $ss.entity.categoryId = 1;
                            $ss.entity.categoryName = "Other";
                        }

                        $ss.entity.source = "custom-memory-item";

                        if (isUrl($ss.entity.name)) {
                            $ss.entity.entityType = 'web-page';
                        } else {
                            $ss.entity.entityType = 'custom';
                        }

                        _.each($ss.entity.properties, function (p) {
                            delete p.sameAs;
                            delete p.externalSourceUri;
                        });


                        $ss.propertiesLoading = true;
                        $http.post('knowledgeBase/api/saveEntity', $ss.entity)
                            .then(function (entityResponse) {
                                _.each($ss.customProperties, function (cp) {
                                    cp.subject.id = entityResponse.data.id;
                                });

                                $http.post('knowledgeBase/api/saveCustomProperties', $ss.customProperties)
                                    .then(function (response) {
                                        $ss.saveResponse = 'Saved';
                                        $ss.propertiesLoading = false;

                                        $scope.items.push(entityResponse.data);
                                        $scope.entities.push(entityResponse.data);

                                        $ss.entity = entityResponse.data;
                                        $ss.entity.saved = true;
                                        $ss.entity.propertyGroups = pg;

                                        $scope.getEntitiesByCategory();
                                        $scope.getEntitiesBySubCategory();

                                        $ss.closeThisDialog();
                                    }, function (err) {
                                        printError(err);
                                        $ss.propertiesLoading = false;
                                    });
                            }, function (err) {
                                printError(err);
                                $ss.propertiesLoading = false;
                            });
                    };

                    $ss.customProperties = [];
                    $ss.addCustomProperty = function () {
                        var subject = $.extend({}, $ss.entity);
                        delete subject.propertyGroups;
                        delete subject.saved;

                        var object = $.extend({}, $ss.customObject);
                        delete object.saved;

                        var property = $.extend({}, $ss.customProperty);
                        property.source = 'custom';

                        if ($ss.customObject.source == 'dbpedia') {
                            property.value = $ss.customObject.dbpediaUri;
                            property.valueLabel = $ss.customObject.name;
                        } else if ($ss.customObject.source == 'wikidata') {
                            property.value = $ss.customObject.wikidataId;
                            property.valueLabel = $ss.customObject.name;
                        } else {
                            property.value = $ss.customObject.name;
                            property.valueLabel = $ss.customObject.name;
                        }

                        $('#custom-property-input').val('');
                        $('#custom-object-input').val('');

                        $ss.entity.properties.push(property); //{subject: subject, predicate: $ss.customProperty, object: object}
                        $ss.customProperties.push({subject: subject, predicate: $ss.customProperty, object: object});

                        groupProperties($ss.entity);

                        $ss.showCustomProperties();
                        $ss.showAllProperties();
                    };

                    $ss.isAddCustomPropertyDisabled = function () {
                        var exists = false;
                        _.each($ss.entity.propertyGroups, function (pg) {
                            _.each(pg.values, function (v) {
                                if ($ss.customProperty && pg.uri == $ss.customProperty.uri && $ss.customObject &&
                                    (v.value == $ss.customObject.dbpediaUri || v.value == $ss.customObject.wikidataId))
                                    exists = true;
                            });
                        });

                        return !($ss.customProperty && $ss.customObject) || exists;
                    };

                    $ss.showProperties = 0; // 0: filtered, 1: all, 2: custom
                    $ss.filteredProperties = function () {
                        if (!$ss.entity || !$ss.entity.propertyGroups) {
                            return [];
                        }

                        var props = [];
                        for (var i = 0; i < $ss.entity.propertyGroups.length; i++) {
                            var prop = $ss.entity.propertyGroups[i];

                            var push = false;
                            if ($ss.showProperties == 1 && prop.visibility != 0)
                                push = true;
                            else if ($ss.showProperties == 0 && prop.visibility == 3)
                                push = true;
                            else if (prop.source == 'custom')  // 2
                                push = true;

                            var p = $scope.filterValuesBySearch(prop, $ss.propertySearchFilter);
                            if (push && p)
                                props.push(p);
                        }

                        return props;
                    };

                    $ss.showAllProperties = function () {
                        $ss.showProperties = 1;
                    };
                });

            }]
        });
    };

    return {
        open: open
    }
})();