(function() {
var app = angular.module('app', ['ngRoute','ngResource','ui.router']);
app.config(function($routeProvider){
    $routeProvider
        .when('/cc/auth',{
            templateUrl: '/index.html',
            controller: 'AltimetrikController'
        })
        .otherwise(
            { redirectTo: '/'}
        );
});

})();