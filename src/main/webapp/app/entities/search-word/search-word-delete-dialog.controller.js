(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('SearchWordDeleteController',SearchWordDeleteController);

    SearchWordDeleteController.$inject = ['$uibModalInstance', 'entity', 'SearchWord'];

    function SearchWordDeleteController($uibModalInstance, entity, SearchWord) {
        var vm = this;

        vm.searchWord = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            SearchWord.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
