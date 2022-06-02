var lodUtils = (function () {

    // Keep this variable private inside this closure scope

    var getTypeNameFromUri = function (uri) {
        if (uri.includes('w3.org')) {
            return uri.split('#')[1];
        } else {
            var arr = uri.split('/');
            return arr[arr.length - 1];
        }
    };


    return {
        getTypeNameFromUri: getTypeNameFromUri
    }
})();
