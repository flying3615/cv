(function() {
    'use strict';

    angular
        .module('cvApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('job-log', {
            parent: 'entity',
            url: '/job-log',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'JobLogs'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/job-log/job-logs.html',
                    controller: 'JobLogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('job-log-detail', {
            parent: 'entity',
            url: '/job-log/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'JobLog'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/job-log/job-log-detail.html',
                    controller: 'JobLogDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'JobLog', function($stateParams, JobLog) {
                    return JobLog.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'job-log',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('job-log-detail.edit', {
            parent: 'job-log-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-log/job-log-dialog.html',
                    controller: 'JobLogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['JobLog', function(JobLog) {
                            return JobLog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('job-log.new', {
            parent: 'job-log',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-log/job-log-dialog.html',
                    controller: 'JobLogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                type: null,
                                logDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('job-log', null, { reload: 'job-log' });
                }, function() {
                    $state.go('job-log');
                });
            }]
        })
        .state('job-log.edit', {
            parent: 'job-log',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-log/job-log-dialog.html',
                    controller: 'JobLogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['JobLog', function(JobLog) {
                            return JobLog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('job-log', null, { reload: 'job-log' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('job-log.delete', {
            parent: 'job-log',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/job-log/job-log-delete-dialog.html',
                    controller: 'JobLogDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['JobLog', function(JobLog) {
                            return JobLog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('job-log', null, { reload: 'job-log' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
