app.controller('altimetrikController', function($scope, $rootScope, $state) {
    $rootScope.title = "Coding Challenge";
    
    $scope.formSubmit = function() {
        $rootScope.userName = $scope.username;
        $scope.error = '';
        $scope.username = '';
        $scope.password = '';
        $state.transitionTo('home');
    };    

}