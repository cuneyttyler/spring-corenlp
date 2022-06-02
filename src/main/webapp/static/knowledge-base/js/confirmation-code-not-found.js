var app = angular.module('app', ['ngDialog']);

app.controller('Controller', function ($scope, $http, $q, $sce, $timeout, ngDialog) {

    $scope.timer = 5;
    setInterval(function () {
        $scope.$apply(function () {
            if ($scope.timer-- == 0) {
                window.location.href = 'knowledge-base';
            }
        });
    }, 1000);

    app.config(function ($sceDelegateProvider, $sceProvider) {
        $sceProvider.enabled(false);
    });

    $(document).ready(function () {
        $('.wrapper').show();
    });
});