var detailPopup = (function () {

    var open = function ($scope, $http, $q, $timeout, ngDialog) {
        ngDialog.open({
            template: '../static/knowledge-base/pop-ups/detail-pop-up.html',
            controller: ['$scope', function ($ss) {
                $ss.selectedEntity = $scope.selectedEntity;
                $ss.entityImage = $scope.selectedEntity.image;
                $ss.isEntitySaved = $scope.isEntitySaved;

                $ss.isPropertyLink = $scope.isPropertyLink;
                $ss.isDbpediaResource = $scope.isDbpediaResource;
                $ss.getExternalUrl = $scope.getExternalUrl;

                $ss.saveEntity = function () {
                    $('.pop-up').css('cursor', 'wait');
                    $ss.annotationEntityLoading = true;
                    $scope.saveEntity(function (entity) {
                        $('.pop-up').css('cursor', 'default');
                        $ss.annotationEntityLoading = false;
                        $ss.entityImage = entity.image;
                        $ss.selectedEntity = $scope.selectedEntity;
                        autocompleteService.configureCategory($scope,$ss);
                    });
                };

                $ss.saveSideEntity = function () {
                    var entity = $.extend({}, $ss.selectedSideEntity);

                    delete entity.propertyGroups;
                    delete entity.descriptionShown;

                    entity.topics = [];
                    entity.categories = [];
                    entity.subCategories = [];

                    if ($ss.selectedSideTopic)
                        entity.topics.push({id: $ss.selectedSideTopic.id, name: $ss.selectedSideTopic.name}); // id , value

                    if ($ss.selectedSideTopic && $ss.selectedSideTopic.selectedCategory)
                        entity.categories.push({id: $ss.selectedSideTopic.selectedCategory.id, name: $ss.selectedSideTopic.selectedCategory.name}); // id , value

                    if ($ss.selectedSideTopic && $ss.selectedSideTopic.selectedCategory && $ss.selectedSideTopic.selectedCategory.selectedSubCategory)
                        entity.subCategories.push({id: $ss.selectedSideTopic.selectedCategory.selectedSubCategory.id, name: $ss.selectedSideTopic.selectedCategory.selectedSubCategory.name}); // id , value

                    entity.source = "memory-item";

                    $ss.annotationEntityLoading = true;
                    $('.pop-up').css('cursor', 'wait');
                    $http.post('knowledgeBase/api/saveEntity', entity)
                        .then(function (response) {
                            $ss.annotationEntityLoading = false;
                            $('.pop-up').css('cursor', 'default');

                            $scope.items.push(response.data);
                            $scope.entities.push(response.data);

                            $ss.selectedSideEntity = response.data;
                            $ss.selectedSideEntity.saved = true;

                            $ss.sideEntityImage = $ss.selectedSideEntity.image;

                            $scope.categorizeEntities();
                        }, function (err) {
                            printError(err);
                            $ss.annotationEntityLoading = false;
                            $('.pop-up').css('cursor', 'default');
                        });
                };

                $ss.isSideEntitySaved = function(){
                    if (!$ss.selectedSideEntity) {
                        return true;
                    }

                    var e = _.find($scope.entities, {dbpediaUri: $ss.selectedSideEntity.dbpediaUri});

                    return e !== null && e !== undefined;
                };

                $ss.isLoggedIn =function() {
                    return $scope.user;
                }

                $ss.selectedSideEntities = [];

                $ss.isWebPageEntity = function () {
                    return $ss.selectedEntity.entityType == 'web-page';
                };

                $ss.getInfoSummary = function (entity) {
                    if ($scope.isPerson(entity)) {

                        var birthDate = $scope.getBirthDate(entity);
                        var birthPlace = $scope.getBirthPlace(entity);
                        var occupations = _.filter(entity.properties, function (p) {
                            return p.uri == 'https://www.wikidata.org/wiki/Property:P106';
                        });

                        var infoSummary = birthDate;
                        if (infoSummary.length > 0 && birthPlace.length > 0)
                            infoSummary += ', ';

                        if (birthPlace.length > 0)
                            infoSummary += birthPlace;

                        if (infoSummary.length > 0 && occupations.length > 0)
                            infoSummary += ', ';

                        var len = occupations.length < 2 ? occupations.length : 2; // only the first two
                        for (var i = 0; i < len; i++) {
                            infoSummary += firstLettersUppercase(occupations[i].valueLabel);

                            if (i != len - 1)
                                infoSummary += ', ';
                        }

                        return infoSummary;
                    }
                };

                $ss.infoSummary = $ss.getInfoSummary($scope.selectedEntity);

                $scope.$on('ngDialog.opened', function (e, $dialog) {
                    autocompleteService.configureCategory($scope,$ss);

                    autocompleteService.configureCustomObject($scope.entities, $http, $q, function (response) {
                        setEditTopicMargin();

                        if (response == null || response.entityType == 'non-semantic-web') {
                            $scope.$apply(function () {
                                $ss.customObject = response;
                                $ss.selectedSideEntity = response;
                            });
                        } else {
                            $ss.customObject = response;
                            $ss.selectedSideEntity = response;
                        }
                    });

                    autocompleteService.configureCustomProperty($http, $q, function (response) {
                        $scope.$apply(function () {
                            $ss.customProperty = response;

                            _.each($ss.selectedEntity.propertyGroups, function (pg) {
                                if ($ss.customProperty.uri == pg.uri) {
                                    $ss.selectedCustomProperty = pg;
                                }
                            });
                        });
                    });


                    if ($ss.isEntitySaved() && $ss.filteredProperties().length == 0) {
                        $ss.showCustomProperties();
                    }
                });

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

                $ss.showProperties = 0; // 0: filtered, 1: all, 2: custom
                $ss.filteredProperties = function () {
                    if (!$ss.selectedEntity || !$ss.selectedEntity.propertyGroups) {
                        return [];
                    }

                    var props = [];
                    for (var i = 0; i < $ss.selectedEntity.propertyGroups.length; i++) {
                        var prop = $ss.selectedEntity.propertyGroups[i];

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

                $ss.showOnlyCustomProperties = function () {
                    $('.show-add-custom-properties .filter a.custom').addClass('selected');
                    $('.show-add-custom-properties .filter a.filtered').removeClass('selected');
                    $('.show-add-custom-properties .filter a.all').removeClass('selected');

                    $ss.showProperties = 2;
                };

                $ss.showOnlyFilteredProperties = function () {
                    $('.show-add-custom-properties .filter a.custom').removeClass('selected');
                    $('.show-add-custom-properties .filter a.filtered').addClass('selected');
                    $('.show-add-custom-properties .filter a.all').removeClass('selected');

                    $ss.showProperties = 0;
                };

                $ss.showAllProperties = function () {
                    $('.show-add-custom-properties .filter a.custom').removeClass('selected');
                    $('.show-add-custom-properties .filter a.filtered').removeClass('selected');
                    $('.show-add-custom-properties .filter a.all').addClass('selected');

                    $ss.showProperties = 1;
                };

                $ss.selectedSideEntityIndex = -1;
                $ss.openLastProperty = function () {
                    $ss.selectedSideEntity = $ss.selectedSideEntities[--$ss.selectedSideEntityIndex];
                };

                $ss.openNextProperty = function () {
                    $ss.selectedSideEntity = $ss.selectedSideEntities[++$ss.selectedSideEntityIndex];
                };

                $ss.popItemsUntilLastSelectedEntity = function () {
                    for (var j = 0; j < $ss.selectedSideEntities.length - $ss.selectedSideEntityIndex - 1; j++) {
                        $ss.selectedSideEntities.pop();
                    }
                };

                // could be called through text (annotationitem index) or
                // through property box (property object)
                $ss.openProperty = function (parameter, callSource) {
                    var source = '';

                    if (parameter && parameter.value) {
                        var uri = parameter.value;
                        source = parameter.source;
                    } else {
                        var uri = '';
                        if (callSource && callSource == 'side')
                            uri = $ss.sideAnnotationEntities[parameter].uri;
                        else
                            uri = $ss.annotationEntities[parameter].uri;
                        source = 'dbpedia';
                    }

                    if ($ss.selectedEntity.dbpediaUri == uri) {
                        return;
                    }

                    $ss.popItemsUntilLastSelectedEntity();
                    $ss.selectedSideEntityIndex++;

                    var e = _.find($scope.entities, {dbpediaUri: uri}); // is property(entity) included in database?

                    var ee = _.find($ss.selectedSideEntities, {dbpediaUri: uri}); // is it included in selected list?
                    ee = ee ? ee : _.find($ss.selectedSideEntities, {wikidataId: uri});

                    if (ee) {
                        $ss.selectedSideEntities.push(ee);
                        $ss.selectedSideEntity = ee;

                        return;
                    } else if (e) {

                        $ss.annotationEntityLoading = true;
                        $('.pop-up').css('cursor', 'wait');
                        $http.get('knowledgeBase/api/getEntityById/' + e.id)
                            .then(function (response) {
                                var entity = response.data;

                                $ss.annotationEntityLoading = false;
                                $('.pop-up').css('cursor', 'default');
                                $ss.selectedSideEntity = entity;
                                $ss.selectedSideEntities.push(entity);

                                $ss.sideEntityImage = $ss.selectedSideEntity.image;

                                $ss.selectedSideEntity.descriptionShown = $ss.selectedSideEntity.description;
                                $http.post('knowledgeBase/api/getAnnotationEntitiesByText', {text: $ss.selectedSideEntity.description})
                                    .then(parseSideResponse, printError);

                                setEditTopicMargin();
                            }, function (err) {
                                printError(err);
                            });
                        return;
                    } else {
                        $ss.annotationEntityLoading = true;
                        $('.pop-up').css('cursor', 'wait');
                        lodService.fetchPropertyFromRelevantSource($http, $q,
                            {value: uri, source: source},
                            function (response) {
                                setEditTopicMargin();

                                $ss.annotationEntityLoading = false;
                                $('.pop-up').css('cursor', 'default');
                                $ss.selectedSideEntity = response;
                                $ss.selectedSideEntities.push(response);

                                $ss.sideEntityImage = $ss.selectedSideEntity.image;

                                $ss.selectedSideEntity.descriptionShown = $ss.selectedSideEntity.description;
                                $http.post('knowledgeBase/api/getAnnotationEntitiesByText', {text: $ss.selectedSideEntity.description})
                                    .then(parseSideResponse, printError);
                            },
                            function () {
                                $ss.annotationEntityLoading = false;
                            });
                    }

                };

                function parseSideResponse(response) {
                    $ss.sideAnnotationEntities = response.data;

                    $ss.sideAnnotationEntities = $ss.sideAnnotationEntities.sort(function (a, b) {
                        if (a.offset > b.offset)
                            return 1;
                        else if (a.offset < b.offset) {
                            return -1
                        } else {
                            return 0;
                        }
                    });

                    $ss.sideDescription = $ss.selectedSideEntity.description;
                    for (var i = 0; i < $ss.sideAnnotationEntities.length; i++) {
                        console.log('');

                        var add = i == 0 ? 0 : 50;

                        var bugAdd = i >= 11 ? 1 : 0;

                        var before = $ss.sideDescription.slice(0, $ss.sideAnnotationEntities[i].offset + bugAdd * (i - 10) + add * i);
                        var after = $ss.sideDescription.slice($ss.sideAnnotationEntities[i].offset + bugAdd * (i - 10) + $ss.sideAnnotationEntities[i].surfaceForm.length + add * i, $ss.sideDescription.length);

                        $ss.sideDescription = before + '<a href="#" ng-click="openProperty(' + i + ',\'side\')">' + $ss.sideAnnotationEntities[i].surfaceForm + '</a>' + after;
                    }

                    $ss.selectedSideEntity.descriptionShown = $ss.sideDescription;
                }

                function setEditTopicMargin() {
                    $timeout(function () {
                        $(".sub-topic .info-image img").on('load', function () {
                            var marginTop = $('.sub-topic').height() - $('.edit-topic').height() - $('.topic').height();
                            if (marginTop < 0) marginTop = 0;
                            $('.edit-topic').css('margin-top', marginTop);
                        });
                    }, 0);
                }

                $ss.isAnnotationEntitySaved = function () {
                    if (!$ss.selectedSideEntity) {
                        return true;
                    }

                    var e = _.find($scope.entities, {dbpediaUri: $ss.selectedSideEntity.dbpediaUri});

                    return e !== null && e !== undefined;
                };

                var getSelected = function () {
                    var t = '';
                    if (window.getSelection) {
                        t = window.getSelection();
                    } else if (document.getSelection) {
                        t = document.getSelection();
                    } else if (document.selection) {
                        t = document.selection.createRange().text;
                    }
                    return t;
                };

                var selectionX, selectionY, lastSelectedText = '';
                $(document).on("mousedown", function (e) {
                    selectionX = e.pageX;
                    selectionY = e.pageY;
                });

                $(document).bind("mouseup", function () {
                    var selectedText = getSelected();
                    if (selectedText != '' && selectedText.toString() != lastSelectedText) {
                        lastSelectedText = selectedText.toString();

                        dbpedia.prefixSearch($http, selectedText, function (results) {
                            $('.custom-context-menu.annotation-results').css({
                                'left': selectionX + 5,
                                'top': selectionY
                            }).fadeIn(200);

                            $ss.customAnnotationResults = results;
                        });
                    } else {
                        $('.custom-context-menu.annotation-results').fadeOut(200);
                    }
                });

                $ss.openCustomAnnotation = function (annotation) {
                    $('.custom-annotation-results').fadeOut(200);
                    $ss.openProperty({value: annotation.uri});
                };

                $ss.saveCustomAnnotation = function (item) {
                    var annotation = {
                        surfaceForm: lastSelectedText,
                        label: item.label,
                        uri: item.uri,
                        referenced_entity_id: $ss.selectedEntity.id
                    };

                    $http.post('knowledgeBase/api/saveCustomAnnotation', annotation)
                        .then(function (response) {

                        }, printError);
                };

                function parseResponse(response) {
                    $ss.annotationEntities = response.data;

                    $ss.annotationEntities = $ss.annotationEntities.sort(function (a, b) {
                        if (a.offset > b.offset)
                            return 1;
                        else if (a.offset < b.offset) {
                            return -1
                        } else {
                            return 0;
                        }
                    });

                    if ($scope.selectedEntity.entityType == 'web-page-annotation')
                        $scope.selectedEntity.description = $scope.selectedEntity.webPageText;

                    $ss.description = $ss.selectedEntity.description;
                    for (var i = 0; i < $ss.annotationEntities.length; i++) {
                        console.log('');

                        var add = 43;

                        var bugAdd = i >= 11 ? 1 : 0;

                        var before = $ss.description.slice(0, $ss.annotationEntities[i].offset + bugAdd * (i - 10) + add * i);
                        var after = $ss.description.slice($ss.annotationEntities[i].offset + bugAdd * (i - 10) + $ss.annotationEntities[i].surfaceForm.length + add * i, $ss.description.length);

                        $ss.description = before + '<a href="#" ng-click="openProperty(' + i + ')">' + $ss.annotationEntities[i].surfaceForm + '</a>' + after;
                    }

                    $ss.descriptionShown = $ss.description;

                    if ($ss.descriptionShown.indexOf("<<a href=\"#\" ng-click=\"openProperty(0)\">HTML</a>>") != -1)
                        $ss.descriptionShown = $ss.descriptionShown.split("<<a href=\"#\" ng-click=\"openProperty(0)\">HTML</a>>")[0] + $ss.descriptionShown.split("<<a href=\"#\" ng-click=\"openProperty(0)\">HTML</a>>")[1];

                    if ($ss.descriptionShown.length < 150) {
                        for (i = 0; i < 200; i++) {
                            $ss.descriptionShown += ' ';
                        }
                    }

                    console.log();
                }

                if ($ss.selectedEntity.id) {
                    $http.get('knowledgeBase/api/getAnnotationEntities/' + $ss.selectedEntity.id)
                        .then(parseResponse, printError);
                } else {
                    $ss.descriptionShown = $ss.selectedEntity.description;
                    $http.post('knowledgeBase/api/getAnnotationEntitiesByText', {text: $ss.selectedEntity.description})
                        .then(parseResponse, printError)
                }

                $ss.description = $ss.selectedEntity.description;

                $ss.isSaveCustomPropertyDisabled = function () {
                    var exists = false;
                    _.each($ss.selectedEntity.propertyGroups, function (pg) {
                        _.each(pg.values, function (v) {
                            if ($ss.customProperty && pg.uri == $ss.customProperty.uri && $ss.customObject &&
                                (v.value == $ss.customObject.dbpediaUri || v.value == $ss.customObject.wikidataId))
                                exists = true;
                        });
                    });

                    return !($ss.customProperty && $ss.customObject) || exists;
                };

                $ss.saveCustomProperty = function () {
                    var subject = $.extend({}, $ss.selectedEntity);
                    delete subject.propertyGroups;
                    delete subject.saved;

                    var object = $.extend({}, $ss.customObject);
                    delete object.saved;

                    $http.post('knowledgeBase/api/saveCustomProperty', {subject: subject, predicate: $ss.customProperty, object: object})
                        .then(function (response) {
                            $('#custom-property-input').val('');
                            $('#custom-object-input').val('');

                            $ss.selectedEntity.properties.push(response.data);

                            groupProperties($ss.selectedEntity);

                            $ss.showCustomProperties();
                            $ss.showOnlyCustomProperties();
                        }, printError)
                };

                $ss.displayShowNotes = false;
                $ss.displayHideNotes = true;

                $scope.showNotes = function () {
                    $('textarea').show();
                    $('textarea').animate({height: '100px'}, 300);

                    $ss.displayShowNotes = false;
                    $ss.displayHideNotes = true;
                };

                $scope.hideNotes = function () {
                    $('textarea').animate({height: '0px'}, 300);
                    setTimeout(function () {
                        $('textarea').show()
                    }, 300);

                    $ss.displayShowNotes = true;
                    $ss.displayHideNotes = false;
                };

                $ss.getImageUrl = $scope.getImageUrl;

                $ss.updateEntity = function () {
                    var pg = $ss.selectedEntity.propertyGroups;
                    delete $ss.selectedEntity.propertyGroups;

//                    if (!$scope.selectedEntity.categoryId || $scope.selectedEntity.categoryId == 0) {
//                        $scope.selectedEntity.categoryId = 1;
//                        $scope.selectedEntity.categoryName = "Other";
//                    }

                    $ss.propertiesLoading = true;
                    $http.post('knowledgeBase/api/updateEntity', $ss.selectedEntity)
                        .then(function (response) {
                            $ss.saveResponse = 'Saved';
                            $ss.propertiesLoading = false;

                            $ss.selectedEntity.propertyGroups = pg;
                            $scope.selectedEntity = $ss.selectedEntity;

                            $scope.getEntitiesByCategory();
                        }, function (err) {
                            printError(err);
                            $ss.saveResponse = err;
                            $scope.propertiesLoading = false;
                        });
                };

                $ss.openRemoveConfirmation = function () {
                    ngDialog.open({
                        template: '../static/knowledge-base/pop-ups/confirmation-pop-up.html',
                        controller: ['$scope', function ($sss) {

                            $sss.closeWindow = function () {
                                $sss.closeThisDialog();
                            };

                            $sss.removeEntity = function () {

                                $http.get('knowledgeBase/api/removeEntity/' + $scope.selectedEntity.id)
                                    .then(function (response) {
                                        $sss.saveResponse = 'Saved';
                                        $sss.propertiesLoading = false;

                                        var index = -1;
                                        for (var i = 0; i < $scope.entities.length; i++) {
                                            if ($scope.entities[i].id == $scope.selectedEntity.id) {
                                                index = i;
                                            }
                                        }

                                        window.location.reload(); // TODO without reloading.
                                        $scope.entities.splice(index, 1);
                                        $scope.selectedEntity = null;

                                        $scope.getEntitiesByCategory();

                                        $sss.closeThisDialog();
                                        $ss.closeThisDialog();
                                    }, function (err) {
                                        printError(err);
                                        $sss.saveResponse = err;
                                    });
                            };

                        }]});
                };

            }]});
    };

    return {
        open: open
    }
})();