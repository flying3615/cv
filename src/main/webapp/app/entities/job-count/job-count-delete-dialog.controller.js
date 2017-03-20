(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('JobCountDeleteController',JobCountDeleteController);

    JobCountDeleteController.$inject = ['$uibModalInstance', 'entity', 'JobCount'];

    function JobCountDeleteController($uibModalInstance, entity, JobCount) {
        var vm = this;

        vm.jobCount = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            JobCount.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
