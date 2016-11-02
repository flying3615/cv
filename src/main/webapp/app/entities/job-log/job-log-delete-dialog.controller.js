(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('JobLogDeleteController',JobLogDeleteController);

    JobLogDeleteController.$inject = ['$uibModalInstance', 'entity', 'JobLog'];

    function JobLogDeleteController($uibModalInstance, entity, JobLog) {
        var vm = this;

        vm.jobLog = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            JobLog.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
