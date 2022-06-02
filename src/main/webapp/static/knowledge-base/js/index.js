var app = angular.module('app', ['ngDialog']);

app.controller('Controller', function ($scope, $http, $q, $sce, $timeout, ngDialog) {

        $scope.openSignup = function () {
            ngDialog.open({
                template: '../static/knowledge-base/pop-ups/signup-pop-up.html',
                controller: ['$scope', function ($ss) {
                    $ss.signupUser = function () {

                        $ss.validationMessage = '';
                        if (!$ss.user || !$ss.user.username || !$ss.password || !$ss.user.email || !$ss.user.firstName || !$ss.user.lastName) {
                            $ss.validationMessage = 'All fields must be filled.';
                        }

                        if (!$ss.validationMessage && $ss.password.length < 8) {
                            $ss.validationMessage = 'Password length should be at least 8.';
                        }

                        if ($ss.validationMessage) {
                            $('.validation-message').css('display', 'block');
                            return;
                        }

                        $('.full-screen-overlay').css('display','block');
                        $ss.user.password = CryptoJS.SHA256($ss.password).toString();
                        $http.post('knowledgeBase/api/signup', $ss.user)
                            .then(function (response) {
                                $('.full-screen-overlay').css('display','none');
                                if (response.data == -1) {
                                    $ss.validationMessage = 'A user with this username already exist.';
                                    $('.validation-message').css('display', 'block');
                                    return;
                                } else if (response.data == -2) {
                                    $ss.validationMessage = 'A user with this email already exist.';
                                    $('.validation-message').css('display', 'block');
                                    return;
                                } else {
                                    $ss.successMessage = 'A confirmation email is sent to your address.';
                                    $('.validation-message').css('display', 'none');
                                    $('.success-message').css('display', 'block');

                                    setTimeout(function () {
                                        window.location.reload();
                                    }, 1500);
                                }

                            }, function(err){
                                $('.full-screen-overlay').display('none');
                                $ss.validationMessage = 'An unexpected error has occured.';
                                $('.validation-message').css('display', 'block');

                                sendErrorReport($http,{
                                    uri : 'knowledgeBase/api/signup',
                                    data : $ss.user
                                });
                                printError("Error on signup " + err);
                            });
                    }
                }]});
        };

        $scope.openLogin = function () {
            ngDialog.open({
                template: '../static/knowledge-base/pop-ups/login-pop-up.html',
                controller: ['$scope', function ($ss) {

                    $scope.$on('ngDialog.opened', function (e, $dialog) {
                        $(document).keypress(function(e) {
                            if(e.which == 13) {
                                $ss.login();
                            }
                        });
                    });


                    $ss.login = function () {
                        $ss.validationMessage = '';
                        if (!$ss.username || !$ss.password) {
                            $ss.validationMessage = 'All fields must be filled.';
                        }

                        if ($ss.validationMessage) {
                            $('.validation-message').css('display', 'block');
                            return;
                        }

                        $.ajax({
                            type: "POST",
                            url: 'knowledge-base/knowledgeBase/j_spring_security_check',
                            data: jQuery("#login-form").serialize(), // serializes the form's elements.
                            success: function (data) {
                                window.location = "/knowledge-base/memory";
                            },
                            error: function (data, textStatus, jqXHR) {
                                if (data.status == 410) {
                                } else if (data.status == 409) {
                                } else if (data.status == 406) {
                                } else {
                                }

                                $scope.$digest();

                                printError(textStatus);
                            }
                        });

                    }
                }]});
        };

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

        $http.get('knowledgeBase/api/getMainPageEntityList')
            .then(function (response) {
                response.data.forEach(function (item) {
//                groupProperties(item);

                    $scope.items.push(item);

                    if (item.entityType == 'web-page') {
                        $scope.webPages.push(item);
                        $scope.entities.push(item);
                    } else {
                        $scope.entities.push(item);
                    }


                });
            }, printError);

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
        }, 0);

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

            $scope.showSelectedEntity();
        }

        function getBirthDate(entity) {
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

        function getBirthPlace(entity) {
            var birthPlaceArray = _.filter(entity.properties, function (p) {
                return p.uri == 'https://www.wikidata.org/wiki/Property:P19'
            });

            var birthPlace = '';
            if (birthPlaceArray.length > 0)
                birthPlace = birthPlaceArray[0].valueLabel;

            return birthPlace;
        }

        function isPerson(entity) {
            var personProperty = _.filter(entity.properties, function (p) {
                return p.uri == 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type' &&
                    (p.value == 'http://xmlns.com/foaf/0.1/Person' || p.value == 'http://dbpedia.org/ontology/Person');
            });

            return personProperty && personProperty.length > 0;
        }

        $scope.getInfoSummary = function (entity) {
            if (isPerson(entity)) {

                var birthDate = getBirthDate(entity);
                var birthPlace = getBirthPlace(entity);

                var infoSummary = birthDate;
                if (infoSummary.length > 0 && birthPlace.length > 0)
                    infoSummary += ', ';

                if (birthPlace.length > 0)
                    infoSummary += birthPlace;

                return infoSummary;
            }
        };


        $scope.entityDoubleClicked = function (e) {
            $scope.doubleClicked = true;

            $scope.showEntity(e, $scope.openDetailPopup);

            setTimeout(function () {
                $scope.doubleClicked = false;
            }, 300);
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


        $scope.showCategoryItems = function ($event, c) {
            $scope.categories.forEach(function (cc) {
                cc.selected = undefined;
            });

            c.selected = 'selected';
            $scope.selectedCategoryToShow = c;
            $scope.selectedCategoryToShow.selectedSubCategory = c.subCategories[0];

            // set up initial memory box context menu items as all subcategories below this category
            $scope.memoryBoxContextMenuItems = [];
            if ($scope.selectedCategoryToShow.id != 1) {
                _.each($scope.selectedCategoryToShow.subCategories, function (sc) {
                    if (sc.id != -1 && sc.id != -2) {
                        $scope.memoryBoxContextMenuItems.push({type: 'subcategory', item: sc});
                    }
                });
            } else {
                _.each($scope.categories, function (c) {
                    if (c.id != 1) {
                        $scope.memoryBoxContextMenuItems.push({type: 'category', item: c});
                    }
                });
            }

        };

        $scope.openDetailPopup = function () {
//            if (!$scope.selectedEntity.id) {
//                return;
//            }

            ngDialog.open({
                template: '../static/knowledge-base/pop-ups/detail-pop-up.html',
                controller: ['$scope', function ($ss) {
                    $ss.selectedEntity = $scope.selectedEntity;
                    $ss.entityImage = $scope.selectedEntity.image;
                    $ss.isEntitySaved = $scope.isEntitySaved;

                    $ss.isPropertyLink = $scope.isPropertyLink;
                    $ss.isDbpediaResource = $scope.isDbpediaResource;
                    $ss.getExternalUrl = $scope.getExternalUrl;

                    $ss.selectedSideEntities = [];

                    $ss.isWebPageEntity = function () {
                        return $ss.selectedEntity.entityType == 'web-page';
                    };

                    $ss.getInfoSummary = function (entity) {
                        if (isPerson(entity)) {

                            var birthDate = getBirthDate(entity);
                            var birthPlace = getBirthPlace(entity);
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
                                    'top': selectionY - 55
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
                                    'top': selectionY - 55
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

                            var add = i == 0 ? 0 : 43;

                            var bugAdd = i >= 11 ? 1 : 0;

                            var before = $ss.description.slice(0, $ss.annotationEntities[i].offset + bugAdd * (i - 10) + add * i);
                            var after = $ss.description.slice($ss.annotationEntities[i].offset + bugAdd * (i - 10) + $ss.annotationEntities[i].surfaceForm.length + add * i, $ss.description.length);

                            $ss.description = before + '<a href="#" ng-click="openProperty(' + i + ')">' + $ss.annotationEntities[i].surfaceForm + '</a>' + after;
                        }

                        $ss.descriptionShown = $ss.description;

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


                }]});
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

        function getEntitiesByCategory() {
            for (var i = 0; i < $scope.categories.length; i++) {
                var c = $scope.categories[i];
                c.entities = [];

                for (var j = 0; j < $scope.entities.length; j++) {
                    var e = $scope.entities[j];

                    if (e.categoryId == c.id) {
                        c.entities.push(e);
                    }
                }
            }

            // Initially context menu items are set to all categories, because initial selected category is other
            // and has no subcategories...
            $timeout(function () {
                $scope.memoryBoxContextMenuItems = [];
                _.each($scope.categories, function (c) {
                    if (c.id != 1) {
                        $scope.memoryBoxContextMenuItems.push({type: 'category', item: c});
                    }
                });

            }, 0);
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

        function getEntitiesBySubCategory() {
            for (var i = 0; i < $scope.categories.length; i++) {
                var c = $scope.categories[i];

                for (var k = 0; k < c.subCategories.length; k++) {
                    var sc = c.subCategories[k];
                    sc.entities = [];

                    for (var j = 0; j < $scope.entities.length; j++) {
                        var e = $scope.entities[j];

                        if (sc.id == -2 && c.id == e.categoryId && (e.subCategoryId == 0 || e.subCategoryId == null)) {
                            sc.entities.push(e);
                        } else if (sc.id == -1 && c.id == e.categoryId) {
                            sc.entities.push(e);
                        } else if (e.subCategoryId == sc.id && c.id == e.categoryId) {
                            sc.entities.push(e);
                        }
                    }
                }
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
)
;

app.config(function ($sceDelegateProvider, $sceProvider) {
    $sceProvider.enabled(false);
});

$(document).ready(function () {
    $('.wrapper').show();
});

app.directive('orientable', function () {
    return {
        link: function (scope, element, attrs) {

            element.bind("load", function (e) {
                var RATIO = 0.75;
                var DISPLAY_HEIGHT = 240;

                var parent = jQuery(this).parent().parent();
                if (this.height <= DISPLAY_HEIGHT) {
                    var pad = (DISPLAY_HEIGHT - this.height) / 2;
                    parent.css("padding-top", pad);
                    parent.css("padding-bottom", pad);
                } else {
                    //jQuery(this).css("height", 234);
                }

                if (this.naturalHeight > this.naturalWidth) {
                    if (this.className.indexOf('vertical') == -1)
                        this.className += " vertical";

                    if (this.naturalWidth / this.naturalHeight < RATIO) {
                        var expectedWidth = this.naturalHeight * RATIO;
                        var naturalPad = (expectedWidth - this.naturalWidth) / 2;
                        var pad = DISPLAY_HEIGHT / this.naturalHeight * naturalPad;

                        parent.css("padding-right", pad);
                        parent.css("padding-left", pad);

                        if (this.height <= DISPLAY_HEIGHT) {
                            var pad = (DISPLAY_HEIGHT - this.height);
                            parent.css("padding-top", pad);
                            parent.css("padding-bottom", 0);
                        } else {
                            //jQuery(this).css("height", 234);
                        }
                    }
                } else {
                    if (this.className.indexOf('horizontal') == -1)
                        this.className += " horizontal";
                }
            });
        }
    }
});

app.directive('orientablediv', function () {
    return {
        link: function (scope, element, attrs) {

            element.bind("load", function (e) {
                var RATIO = 0.75;
                var DISPLAY_HEIGHT = 240;

                var parent = jQuery(this).parent();
                if (this.height <= DISPLAY_HEIGHT) {
                    var pad = (DISPLAY_HEIGHT - this.height) / 2;
                    parent.css("padding-top", pad);
                    parent.css("padding-bottom", pad);
                } else {
                    //jQuery(this).css("height", 234);
                }

                if (this.naturalHeight > this.naturalWidth) {
                    if (this.className.indexOf('vertical') == -1)
                        this.className += " vertical";

                    if (this.naturalWidth / this.naturalHeight < RATIO) {
                        var expectedWidth = this.naturalHeight * RATIO;
                        var naturalPad = (expectedWidth - this.naturalWidth) / 2;
                        var pad = DISPLAY_HEIGHT / this.naturalHeight * naturalPad;

                        parent.css("padding-right", pad);
                        parent.css("padding-left", pad);

                        if (this.height <= DISPLAY_HEIGHT) {
                            var pad = (DISPLAY_HEIGHT - this.height);
                            parent.css("padding-top", pad);
                            parent.css("padding-bottom", 0);
                        } else {
                            //jQuery(this).css("height", 234);
                        }
                    }
                } else {
                    if (this.className.indexOf('horizontal') == -1)
                        this.className += " horizontal";
                }
            });
        }
    }
});

app.directive('compile', ['$compile', function ($compile) {
    return function (scope, element, attrs) {
        scope.$watch(
            function (scope) {
                return scope.$eval(attrs.compile);
            },
            function (value) {
                element.html(value);
                $compile(element.contents())(scope);
            }
        )
    };
}])