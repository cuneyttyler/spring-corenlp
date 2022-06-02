var sideMenu = (function () {

    var create = function($scope) {
        var $graphsMenu = $('.graph-box-edge.graphs');
        var $albumsMenu = $('.graph-box-edge.albums');
        var $artistsMenu = $('.graph-box-edge.artists');

        $scope.sideMenus = [];
        $scope.sideMenus.push({$el: $graphsMenu, isOpen: false});
        $scope.sideMenus.push({$el: $albumsMenu, isOpen: false});
        $scope.sideMenus.push({$el: $artistsMenu, isOpen: false});

        $scope.openMenu = null;

        $scope.openGraphMenu = function () {
            putOtherMenusBack($graphsMenu);

            if (isOpen($graphsMenu)) {
                $('.graph-box-edge').animate({left: '-290px'}, 300);
            }
            else {
                $('.graph-box-edge').animate({left: '0px'}, 300);
            }

            setOpen($graphsMenu, !isOpen($graphsMenu));
        };

        $scope.albumMenuOpen = false;
        $scope.openAlbumMenu = function () {
            putOtherMenusBack($albumsMenu);

            if (isOpen($albumsMenu))
                $('.graph-box-edge').animate({left: '-290px'}, 300);
            else
                $('.graph-box-edge').animate({left: '0px'}, 300);

            setOpen($albumsMenu, !isOpen($albumsMenu));
        };

        $scope.albumMenuOpen = false;
        $scope.openArtistMenu = function () {
            putOtherMenusBack($artistsMenu);

            if (isOpen($artistsMenu))
                $('.graph-box-edge').animate({left: '-290px'}, 300);
            else
                $('.graph-box-edge').animate({left: '0px'}, 300);

            setOpen($artistsMenu, !isOpen($artistsMenu));
        };

        function putOtherMenusBack($thisMenu) {
            $thisMenu.css({'z-index': 999});

            _.each($scope.sideMenus, function (menu) {
                if ($thisMenu[0] != menu.$el[0]) {
                    menu.$el.css({'z-index': 1});
                    menu.isOpen = false;
                }
            });
        }

        function setOpen($el, isOpen) {
            _.each($scope.sideMenus, function (item) {
                if (item.$el[0] == $el[0]) {
                    item.isOpen = isOpen;
                }
            });
        }

        function isOpen($el) {
            var ret = false;
            _.each($scope.sideMenus, function (item) {
                if (item.$el[0] == $el[0]) {
                    ret = item.isOpen;
                }
            });

            return ret;
        }
    }

    return {
        create: create
    }
})();