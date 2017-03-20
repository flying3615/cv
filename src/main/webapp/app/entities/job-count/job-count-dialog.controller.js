(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('JobCountDialogController', JobCountDialogController);

    JobCountDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'JobCount', 'SearchWord'];

    function JobCountDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, JobCount, SearchWord) {
        var vm = this;

        vm.jobCount = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.searchwords = SearchWord.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.jobCount.id !== null) {
                JobCount.update(vm.jobCount, onSaveSuccess, onSaveError);
            } else {
                JobCount.save(vm.jobCount, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cvApp:jobCountUpdate', result);
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
