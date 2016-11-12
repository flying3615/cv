(function() {
    'use strict';

    angular
        .module('cvApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('tech-word', {
            parent: 'entity',
            url: '/tech-word',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'TechWords'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tech-word/tech-words.html',
                    controller: 'TechWordController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('tech-word-detail', {
            parent: 'entity',
            url: '/tech-word/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'TechWord'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tech-word/tech-word-detail.html',
                    controller: 'TechWordDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'TechWord', function($stateParams, TechWord) {
                    return TechWord.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'tech-word',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('tech-word-detail.edit', {
            parent: 'tech-word-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tech-word/tech-word-dialog.html',
                    controller: 'TechWordDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TechWord', function(TechWord) {
                            return TechWord.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tech-word.new', {
            parent: 'tech-word',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tech-word/tech-word-dialog.html',
                    controller: 'TechWordDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                language: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('tech-word', null, { reload: 'tech-word' });
                }, function() {
                    $state.go('tech-word');
                });
            }]
        })
        .state('tech-word.edit', {
            parent: 'tech-word',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tech-word/tech-word-dialog.html',
                    controller: 'TechWordDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TechWord', function(TechWord) {
                            return TechWord.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tech-word', null, { reload: 'tech-word' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tech-word.delete', {
            parent: 'tech-word',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tech-word/tech-word-delete-dialog.html',
                    controller: 'TechWordDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['TechWord', function(TechWord) {
                            return TechWord.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tech-word', null, { reload: 'tech-word' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
