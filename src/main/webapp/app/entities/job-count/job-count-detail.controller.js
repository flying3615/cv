(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('JobCountDetailController', JobCountDetailController);

    JobCountDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'JobCount', 'SearchWord'];

    function JobCountDetailController($scope, $rootScope, $stateParams, previousState, entity, JobCount, SearchWord) {
        var vm = this;

        vm.jobCount = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cvApp:jobCountUpdate', function(event, result) {
            vm.jobCount = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
