var lodService = (function () {

    // to revert image change, delete "response.secondaryImage = item.image;" and decomment 15 and 18. lines, same for below 'fetch' method
    var getItem = function ($http, $q, uri, callback) {
        dbpedia.getItem($http, uri, function (item) {
            var response = item;

            if (item)
                response.secondaryImage = item.image;

            if (item.wikidataId)
                wikidata.getItem($http, $q, item.wikidataId, function (wikidataItem) {
                    if (wikidataItem) {
                        response.properties = response.properties.concat(wikidataItem.properties);
                        response.shortDescription = wikidataItem.description;

//                    if (!response.image)
                        response.image = wikidataItem.image;

//                    response.secondaryImage = wikidataItem.image;
                    }

                    callback(response);
                });
        });
    };

    function parseWikidataResponse($scope, wikidataItem) {
        if (wikidataItem) {
            $scope.selectedEntity.properties = $scope.selectedEntity.properties.concat(wikidataItem.properties);
            $scope.selectedEntity.shortDescription = wikidataItem.description;

            if (!$scope.selectedEntity.image)
                $scope.selectedEntity.image = wikidataItem.image;

//            if (wikidataItem.image)
//                $scope.selectedEntity.image = wikidataItem.image;
        }
    }

    var fetchPropertyFromRelevantSource = function ($http, $q, property, callback, errCallback) {
        if (property.source == 'wikidata') {
            wikidata.getItem($http, $q, property.value, function (wikidataItem) {
                if (!wikidataItem) {
                    errCallback();
                    return;
                }

                callback(wikidataItem);

            });
        } else {
            dbpedia.getItem($http, property.value, function (item) {
                if (!item) {
                    errCallback();
                    return;
                }

                var response = item;

                if (item)
                    response.secondaryImage = item.image;

                wikidata.getItem($http, $q, item.wikidataId, function (wikidataItem) {
                    if (wikidataItem) {
                        response.properties = response.properties.concat(wikidataItem.properties);
                        response.shortDescription = wikidataItem.description;

//                        if (!response.image)
                        response.image = wikidataItem.image;

//                        response.secondaryImage = wikidataItem.image;
                    }

                    callback(response);
                });

            });
        }
    };

    return {
        getItem: getItem,
        fetchPropertyFromRelevantSource: fetchPropertyFromRelevantSource
    }
})();