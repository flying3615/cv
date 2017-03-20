(function() {
    'use strict';

    angular
        .module('cvApp')
        .controller('SearchWordDialogController', SearchWordDialogController);

    SearchWordDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'SearchWord'];

    function SearchWordDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, SearchWord) {
        var vm = this;

        vm.searchWord = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.searchWord.id !== null) {
                SearchWord.update(vm.searchWord, onSaveSuccess, onSaveError);
            } else {
                SearchWord.save(vm.searchWord, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cvApp:searchWordUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
