(function () {
    'use strict';

    angular
        .module('cvApp')
        .controller('JobController', JobController);

    JobController.$inject = ['$scope', '$state', 'Job', 'JobSearch', 'ParseLinks', 'AlertService', '$stateParams'];

    function JobController($scope, $state, Job, JobSearch, ParseLinks, AlertService, $stateParams) {
        var vm = this;

        vm.jobs = [];
        vm.loadPage = loadPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;
        vm.clear = clear;
        vm.loadAll = loadAll;
        vm.search = search;

        loadAll();

        function loadAll() {

            if (vm.currentSearch) {
                //goes to ES
                JobSearch.query({
                    query: vm.currentSearch,
                    page: vm.page,
                    size: 20,
                    sort: sort()
                }, onSuccess, onError);

            } else {
                if ($stateParams.param) {
                    var parse = JSON.parse($stateParams.param);
                    //comes from home page
                    var queryStr = "";
                    angular.forEach(parse, function (value, key) {
                        queryStr += key + "=" + value + "&"
                    })

                    var queryStr2 = queryStr.substring(0, queryStr.lastIndexOf('&'));
                    Job.query({
                        query: queryStr2,
                        page: vm.page,
                        size: 20,
                        sort: sort()
                    }, onSuccess, onError);
                }else{

                    //goes to DB
                    Job.query({
                        query:'',
                        page: vm.page,
                        size: 20,
                        sort: sort()
                    }, onSuccess, onError);
                }

            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                for (var i = 0; i < data.length; i++) {
                    vm.jobs.push(data[i]);
                }
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function reset() {
            vm.page = 0;
            vm.jobs = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }

        function clear() {
            vm.jobs = [];
            vm.links = {
                last: 0
            };
            vm.page = 0;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.searchQuery = null;
            vm.currentSearch = null;
            vm.loadAll();
        }

        function search(searchQuery) {
            if (!searchQuery) {
                return vm.clear();
            }
            vm.jobs = [];
            vm.links = {
                last: 0
            };
            vm.page = 0;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.loadAll();
        }
    }
})();
