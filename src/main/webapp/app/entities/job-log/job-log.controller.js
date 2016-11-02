(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('JobLogController', JobLogController);

    JobLogController.$inject = ['$scope', '$state', 'JobLog', 'JobLogSearch'];

    function JobLogController ($scope, $state, JobLog, JobLogSearch) {
        var vm = this;
        
        vm.jobLogs = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            JobLog.query(function(result) {
                vm.jobLogs = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            JobLogSearch.query({query: vm.searchQuery}, function(result) {
                vm.jobLogs = result;
            });
        }    }
})();
