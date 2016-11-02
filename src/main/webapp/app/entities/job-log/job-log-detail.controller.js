(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('JobLogDetailController', JobLogDetailController);

    JobLogDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'JobLog', 'Job'];

    function JobLogDetailController($scope, $rootScope, $stateParams, previousState, entity, JobLog, Job) {
        var vm = this;

        vm.jobLog = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cvApp:jobLogUpdate', function(event, result) {
            vm.jobLog = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
