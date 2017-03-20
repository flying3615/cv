(function() {
    'use strict';

    angular
        .module('cvApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('search-word', {
            parent: 'entity',
            url: '/search-word',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'SearchWords'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/search-word/search-words.html',
                    controller: 'SearchWordController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('search-word-detail', {
            parent: 'entity',
            url: '/search-word/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'SearchWord'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/search-word/search-word-detail.html',
                    controller: 'SearchWordDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'SearchWord', function($stateParams, SearchWord) {
                    return SearchWord.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'search-word',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('search-word-detail.edit', {
            parent: 'search-word-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/search-word/search-word-dialog.html',
                    controller: 'SearchWordDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SearchWord', function(SearchWord) {
                            return SearchWord.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('search-word.new', {
            parent: 'search-word',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/search-word/search-word-dialog.html',
                    controller: 'SearchWordDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                wordName: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('search-word', null, { reload: 'search-word' });
                }, function() {
                    $state.go('search-word');
                });
            }]
        })
        .state('search-word.edit', {
            parent: 'search-word',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/search-word/search-word-dialog.html',
                    controller: 'SearchWordDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SearchWord', function(SearchWord) {
                            return SearchWord.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('search-word', null, { reload: 'search-word' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('search-word.delete', {
            parent: 'search-word',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/search-word/search-word-delete-dialog.html',
                    controller: 'SearchWordDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SearchWord', function(SearchWord) {
                            return SearchWord.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('search-word', null, { reload: 'search-word' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
