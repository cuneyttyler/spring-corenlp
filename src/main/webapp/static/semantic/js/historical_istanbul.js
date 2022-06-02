var app = angular.module('app', []);

app.controller('Controller', function ($scope, $http) {

    $scope.places = [];
    $http.get('historicalIstanbul/api/getAllPlaces')
        .then(function (response) {
            response.data.forEach(function (item) {
                var marker = L.marker([item.lat, item.lon]).addTo(map)
                    .bindPopup(item.name)
                    .openPopup();

                marker.data = item;
                marker.index = $scope.places.length;

                marker.on('click', function (e) {
                    $scope.selectedPlace = item;
                    $scope.$digest();
                });

                $scope.places.push(item);
            });

            $scope.selectedPlace = $scope.places[0];
        }, printError);

    var cache = {};
    $("#searchinput").autocomplete({
        minLength: 2,
        source: function (request, response) {
            var term = request.term;
            if (term in cache) {
                response(cache[ term ]);
                return;
            }

            $.ajax({
                url: "http://lookup.dbpedia.org/api/search/PrefixSearch?QueryString=" + request.term,
                type: "GET",
                success: function (data, status) {
                    console.log("Data: " + data + "\nStatus: " + status);
                    cache[ term ] = response;

                    data = $.xml2json(data);

                    if (!data['#document'].ArrayOfResult || !data['#document'].ArrayOfResult.Result) return;

                    if (data['#document'].ArrayOfResult.Result.constructor === Object) {
                        var arr = [];
                        arr.push(jQuery.extend(true, {}, data['#document'].ArrayOfResult.Result));
                        data['#document'].ArrayOfResult.Result = arr;
                    }

                    var results = [];
                    for (var i = 0; i < data['#document'].ArrayOfResult.Result.length; i++) {
                        var r = data['#document'].ArrayOfResult.Result[i];

                        results.push(
                            {
                                uri: r.URI,
                                id: r.Label,
                                label: r.Label,
                                value: r.Label,
                                description: r.Description
                            });
                    }

                    response(results);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("ERROR: " + textStatus);
                }
            });
        }, select: function (event, ui) {
            this.value = ui.item.value;
            $scope.searchValue = ui.item.value;

            var jsonLDUri = 'http://live.dbpedia.org/sparql?default-graph-uri=' +
                'http%3A%2F%2Fdbpedia.org&query=DESCRIBE%20' +
                '%3C' + encodeURIComponent(ui.item.uri) + '%3E' + // <dbpedia.org/resource/item>
                '&' +
                'format=application%2Fjson-ld';

            $.getJSON(jsonLDUri,
                function (data) {
                    var item = data[ui.item.uri];
                    var lat = item['http://www.w3.org/2003/01/geo/wgs84_pos#lat'][0].value.toPrecision(7);
                    var long = item['http://www.w3.org/2003/01/geo/wgs84_pos#long'][0].value.toPrecision(7);
                    var abstract = '';

                    var abstracts = _.find(item['http://dbpedia.org/ontology/abstract'], {lang: 'en'}).value;
                    for (var i = 0; i < abstracts.length; i++) {
                        if (abstracts[i].lang == 'en') {
                            abstract = abstracts[i].value;
                        }
                    }

                    var place = {
                        name: $scope.searchValue,
                        description: _.find(item['http://dbpedia.org/ontology/abstract'], {lang: 'en'}).value,
                        dbpediaUri: ui.item.uri,
                        type: null,
                        typeUri: null,
                        lat: item['http://www.w3.org/2003/01/geo/wgs84_pos#lat'][0].value.toPrecision(7),
                        lon: item['http://www.w3.org/2003/01/geo/wgs84_pos#long'][0].value.toPrecision(7),
                        properties: []
                    };

                    if (item['http://dbpedia.org/ontology/thumbnail'] != null && item['http://dbpedia.org/ontology/thumbnail'].length > 0 ) {
                        place.smallImage = item['http://dbpedia.org/ontology/thumbnail'][0].value;
                    }

                    if (item['http://xmlns.com/foaf/0.1/depiction'] != null && item['http://xmlns.com/foaf/0.1/depiction'].length > 0) {
                        place.image = item['http://xmlns.com/foaf/0.1/depiction'][0].value;
                    }


                    for (var property in item) {
                        if (item.hasOwnProperty(property)) {
                            for (var i = 0; i < item[property].length; i++) {
                                var p = {
                                    name: lodUtils.getTypeNameFromUri(property),
                                    lang: item[property][i].lang ? item[property][i].lang : null,
                                    type: item[property][i].type ? item[property][i].type : null,
                                    datatype: item[property][i].datatype ? item[property][i].datatype : null,
                                    value: item[property][i].value ? item[property][i].value : null,
                                    uri: property
                                };

                                console.log('datatype : ' + p.datatype);
                                place.properties.push(p);
                            }
                        }
                    }

                    var marker = L.marker([lat, long])
                        .addTo(map)
                        .bindPopup(ui.item.value)
                        .openPopup();

                    marker.on('click', function (e) {
                        $scope.selectedPlace = place;
                        $scope.$digest();
                    });

                    marker.data = place;
                    marker.index = $scope.places.length;

                    $scope.selectedPlace = place;
                    $scope.places.push(place);

                    $scope.$digest();

                });
            return false;
        }
    });

    $scope.imgLocal = function (img) {
        return !img.includes('http');
    };

    $scope.savePlace = function () {
        $http.post('historicalIstanbul/api/savePlace', $scope.selectedPlace)
            .then(function (response) {
                $scope.saveResponse = 'Eklendi';
            }, function (err) {
                printError(err);
                $scope.saveResponse = err;
            });
    }
});