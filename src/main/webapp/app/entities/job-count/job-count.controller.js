(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('JobCountController', JobCountController);

    JobCountController.$inject = ['$scope', '$state', 'JobCount', 'JobCountSearch'];

    function JobCountController ($scope, $state, JobCount, JobCountSearch) {
        var vm = this;
        
        vm.jobCounts = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            JobCount.query(function(result) {
                vm.jobCounts = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            JobCountSearch.query({query: vm.searchQuery}, function(result) {
                vm.jobCounts = result;
            });
        }    }
})();
