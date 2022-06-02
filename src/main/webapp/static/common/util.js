function replaceTurkishCharacters(str) {
    //Convert Characters
    var returnString = str.replace(/ö/g, 'o');
    returnString = returnString.replace(/Ö/g, 'O');
    returnString = returnString.replace(/ç/g, 'c');
    returnString = returnString.replace(/Ç/g, 'c');
    returnString = returnString.replace(/ş/g, 's');
    returnString = returnString.replace(/Ş/g, 'S');
    returnString = returnString.replace(/ı/g, 'i');
    returnString = returnString.replace(/İ/g, 'I');
    returnString = returnString.replace(/ğ/g, 'g');
    returnString = returnString.replace(/Ğ/g, 'G');
    returnString = returnString.replace(/ü/g, 'u');
    returnString = returnString.replace(/Ü/g, 'U');

    return returnString;
}

function sendErrorReport($http, data) {
    $http.post('knowledgeBase/api/unexpectedError', datadata)
        .then(function (response) {
        }, function(err){
            printError('Error occured while trying to send error report : ' + err);
        });
}

function printError(err) {
    console.log(err);
}

function isUrl(s) {
    var urlPattern = new RegExp("((http|ftp|https)://)*[\w-]+(\.[\w-]+)+([\w.,@?^=%&amp;:/~+#-]*[\w@?^=%&amp;/~+#-])?");
    var matcher = /^(?:\w+:)?\/\/([^\s\.]+\.\S{2}|localhost[\:?\d]*)\S*$/;

    return urlPattern.test(s) || matcher.test(s);
}

function formatDate(date) {
    var monthNames = [
        "January", "February", "March",
        "April", "May", "June", "July",
        "August", "September", "October",
        "November", "December"
    ];

    var day = date.getDate();
    var monthIndex = date.getMonth();
    var year = date.getFullYear();

    return day + ' ' + monthNames[monthIndex] + ' ' + year;
}

function firstLetterUppercase(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function firstLettersUppercase(string) {
    var returnStr = '';
    var strs = string.split(' ');
    for (var i = 0; i < strs.length; i++) {
        returnStr += firstLetterUppercase(strs[i]);
        if (i != strs.length - 1)  returnStr += ' ';
    }

    return returnStr;
}

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

function insertParam(key, value) {
    key = encodeURI(key);
    value = encodeURI(value);

    var kvp = document.location.search.substr(1).split('&');

    var i = kvp.length;
    var x;
    while (i--) {
        x = kvp[i].split('=');

        if (x[0] == key) {
            x[1] = value;
            kvp[i] = x.join('=');
            break;
        }
    }

    if (i < 0) {
        kvp[kvp.length] = [key, value].join('=');
    }

    //this will reload the page, it's likely better to store this until finished
    document.location.search = kvp.join('&');
}

var getBlogUsername = function () {
    return window.location.href.split('blog/')[1].split('/')[0];
};

var convertJavaDateToJavascriptDate = function (jdate) {
    return new Date(jdate.year, jdate.monthOfYear - 1, jdate.dayOfMonth, jdate.hourOfDay, jdate.minuteOfHour, jdate.secondOfMinute);
}

var printError = function (err) {
    console.log(err);
}