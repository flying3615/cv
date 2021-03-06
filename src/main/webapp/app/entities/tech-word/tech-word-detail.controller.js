(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('TechWordDetailController', TechWordDetailController);

    TechWordDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'TechWord', 'User'];

    function TechWordDetailController($scope, $rootScope, $stateParams, previousState, entity, TechWord, User) {
        var vm = this;

        vm.techWord = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cvApp:techWordUpdate', function(event, result) {
            vm.techWord = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
