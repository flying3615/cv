(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('TechWordDeleteController',TechWordDeleteController);

    TechWordDeleteController.$inject = ['$uibModalInstance', 'entity', 'TechWord'];

    function TechWordDeleteController($uibModalInstance, entity, TechWord) {
        var vm = this;

        vm.techWord = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            TechWord.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
