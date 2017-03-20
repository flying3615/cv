(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('SearchWordDetailController', SearchWordDetailController);

    SearchWordDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'SearchWord'];

    function SearchWordDetailController($scope, $rootScope, $stateParams, previousState, entity, SearchWord) {
        var vm = this;

        vm.searchWord = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cvApp:searchWordUpdate', function(event, result) {
            vm.searchWord = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
