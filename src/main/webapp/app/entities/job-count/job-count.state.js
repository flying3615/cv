(function() {
    'use strict';

    angular
        .module('cvApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('job-count', {
            parent: 'entity',
            url: '/job-count',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'JobCounts'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/job-count/job-counts.html',
                    controller: 'JobCountController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('job-count-detail', {
            parent: 'entity',
            url: '/job-count/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'JobCount'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/job-count/job-count-detail.html',
                    controller: 'JobCountDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'JobCount', function($stateParams, JobCount) {
                    return JobCount.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'job-count',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('job-count-detail.edit', {
            parent: 'job-count-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-count/job-count-dialog.html',
                    controller: 'JobCountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['JobCount', function(JobCount) {
                            return JobCount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('job-count.new', {
            parent: 'job-count',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-count/job-count-dialog.html',
                    controller: 'JobCountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                logDate: null,
                                jobNumber: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('job-count', null, { reload: 'job-count' });
                }, function() {
                    $state.go('job-count');
                });
            }]
        })
        .state('job-count.edit', {
            parent: 'job-count',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-count/job-count-dialog.html',
                    controller: 'JobCountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['JobCount', function(JobCount) {
                            return JobCount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('job-count', null, { reload: 'job-count' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('job-count.delete', {
            parent: 'job-count',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-count/job-count-delete-dialog.html',
                    controller: 'JobCountDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['JobCount', function(JobCount) {
                            return JobCount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('job-count', null, { reload: 'job-count' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
