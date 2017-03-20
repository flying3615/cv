(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('SearchWordController', SearchWordController);

    SearchWordController.$inject = ['$scope', '$state', 'SearchWord', 'SearchWordSearch'];

    function SearchWordController ($scope, $state, SearchWord, SearchWordSearch) {
        var vm = this;
        
        vm.searchWords = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            SearchWord.query(function(result) {
                vm.searchWords = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            SearchWordSearch.query({query: vm.searchQuery}, function(result) {
                vm.searchWords = result;
            });
        }    }
})();
