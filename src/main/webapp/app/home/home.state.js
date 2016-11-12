(function() {
    'use strict';

    angular
        .module('cvApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('home', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeController',
                    controllerAs: 'vm'
                }
            }
        }).state('slides', {
            parent: 'app',
            url: '/slides',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/home/slides.html',
                    // controller: 'HomeController',
                    // controllerAs: 'vm'
                }
            }
        });
    }
})();
