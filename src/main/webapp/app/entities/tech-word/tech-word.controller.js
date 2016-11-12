(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('TechWordController', TechWordController);

    TechWordController.$inject = ['$scope', '$state', 'TechWord', 'TechWordSearch'];

    function TechWordController ($scope, $state, TechWord, TechWordSearch) {
        var vm = this;
        
        vm.techWords = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            TechWord.query(function(result) {
                vm.techWords = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            TechWordSearch.query({query: vm.searchQuery}, function(result) {
                vm.techWords = result;
            });
        }    }
})();
