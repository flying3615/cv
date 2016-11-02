(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('JobLogDialogController', JobLogDialogController);

    JobLogDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'JobLog', 'Job'];

    function JobLogDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, JobLog, Job) {
        var vm = this;

        vm.jobLog = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.jobs = Job.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.jobLog.id !== null) {
                JobLog.update(vm.jobLog, onSaveSuccess, onSaveError);
            } else {
                JobLog.save(vm.jobLog, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cvApp:jobLogUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.logDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
