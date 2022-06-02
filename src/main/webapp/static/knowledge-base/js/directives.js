var directives = (function () {

   var create = function(app) {

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
       }]);

       app.directive('myEnter', function () {
           return function (scope, element, attrs) {
               element.bind("keydown keypress", function (event) {
                   if(event.which === 13) {
                       scope.$apply(function (){
                           scope.$eval(attrs.myEnter);
                       });

                       event.preventDefault();
                   }
               });
           };
       });
   }

    return {
        create: create
    }
})();