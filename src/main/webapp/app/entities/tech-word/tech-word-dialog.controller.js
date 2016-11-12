(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('TechWordDialogController', TechWordDialogController);

    TechWordDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'TechWord', 'User'];

    function TechWordDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, TechWord, User) {
        var vm = this;

        vm.techWord = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.techWord.id !== null) {
                TechWord.update(vm.techWord, onSaveSuccess, onSaveError);
            } else {
                TechWord.save(vm.techWord, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cvApp:techWordUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
