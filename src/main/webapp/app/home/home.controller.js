(function () {
    'use strict';

    angular
        .module('cvApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'NgMap', '$http', 'ParseLinks', 'AlertService','LineOption','PieOption'];

    function HomeController($scope, Principal, LoginService, $state, NgMap, $http, ParseLinks, AlertService,LineOption,PieOption) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        $scope.$on('authenticationSuccess', function () {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }

        function register() {
            $state.go('register');
        }

        function onClick(params) {
            console.log(params);
            //go to job list
            $state.go('job');
        };


        // JobSearch.query({
        //     query: vm.currentSearch,
        //     page: vm.page,
        //     size: 20,
        //     // sort: sort()
        // }, onJobSuccess, onError);


        function onJobSuccess(data, headers) {
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            for (var i = 0; i < data.length; i++) {
                vm.jobs.push(data[i]);
            }
        }

        function onError(error) {
            AlertService.error(error.data.message);
        }


        $scope.lineConfig = {
            theme: 'macarons',
            event: [{click: onClick}],
            dataLoaded: true
        };


        LineOption.then(function(option){
            $scope.lineOption = option;
        });


        PieOption.then(function(option){
            $scope.keyword_pie = option;
        });

        $scope.PieConfig = {
            theme: 'macarons',
            event: [{click: searchByKey}],
            dataLoaded: true
        };

        var markers = [];

        function searchByKey(params) {
            console.log("searchByKey",params)
            NgMap.getMap().then(function (map) {

                //fucking stupid solution!!!!
                //springMVC will drop params with special char
                if('.Net'==params.name){
                    params.name = 'Net'
                }

                $http.get(encodeURI('/api/jobs_map/'+params.name)).then(function (response) {
                    console.log(response.data)
                    markers.forEach(function(m){m.setMap(null)});
                    markers.lenght=0;
                    response.data.forEach(function(data){
                        var marker=new google.maps.Marker({
                            position:new google.maps.LatLng(data.lat,data.lon),
                            animation: google.maps.Animation.DROP,
                            map: map,
                            opacity: 0.7,
                        });

                        markers.push(marker);

                        var infowindow = new google.maps.InfoWindow({
                            content: data.job_count+" "+data.search_word+" jobs @"+data.location
                        });

                        marker.addListener('click', function() {
                            map.setZoom(10);
                            map.setCenter(marker.getPosition());
                            infowindow.open(map,marker);
                        });
                    });

                });

            });
        }




        var data = [
            [[28604, 77, 17096869, 'Australia', 1990], [31163, 77.4, 27662440, 'Canada', 1990], [1516, 68, 1154605773, 'China', 1990], [13670, 74.7, 10582082, 'Cuba', 1990], [28599, 75, 4986705, 'Finland', 1990], [29476, 77.1, 56943299, 'France', 1990], [31476, 75.4, 78958237, 'Germany', 1990], [28666, 78.1, 254830, 'Iceland', 1990], [1777, 57.7, 870601776, 'India', 1990], [29550, 79.1, 122249285, 'Japan', 1990], [2076, 67.9, 20194354, 'North Korea', 1990], [12087, 72, 42972254, 'South Korea', 1990], [24021, 75.4, 3397534, 'New Zealand', 1990], [43296, 76.8, 4240375, 'Norway', 1990], [10088, 70.8, 38195258, 'Poland', 1990], [19349, 69.6, 147568552, 'Russia', 1990], [10670, 67.3, 53994605, 'Turkey', 1990], [26424, 75.7, 57110117, 'United Kingdom', 1990], [37062, 75.4, 252847810, 'United States', 1990]],
            [[44056, 81.8, 23968973, 'Australia', 2015], [43294, 81.7, 35939927, 'Canada', 2015], [13334, 76.9, 1376048943, 'China', 2015], [21291, 78.5, 11389562, 'Cuba', 2015], [38923, 80.8, 5503457, 'Finland', 2015], [37599, 81.9, 64395345, 'France', 2015], [44053, 81.1, 80688545, 'Germany', 2015], [42182, 82.8, 329425, 'Iceland', 2015], [5903, 66.8, 1311050527, 'India', 2015], [36162, 83.5, 126573481, 'Japan', 2015], [1390, 71.4, 25155317, 'North Korea', 2015], [34644, 80.7, 50293439, 'South Korea', 2015], [34186, 80.6, 4528526, 'New Zealand', 2015], [64304, 81.6, 5210967, 'Norway', 2015], [24787, 77.3, 38611794, 'Poland', 2015], [23038, 73.13, 143456918, 'Russia', 2015], [19360, 76.5, 78665830, 'Turkey', 2015], [38225, 81.4, 64715810, 'United Kingdom', 2015], [53354, 79.1, 321773631, 'United States', 2015]]
        ];
        $scope.bubbleOption = {
            backgroundColor: new echarts.graphic.RadialGradient(0.3, 0.3, 0.8, [{
                offset: 0,
                color: '#f7f8fa'
            }, {
                offset: 1,
                color: '#cdd0d5'
            }]),
            title: {
                text: 'GDP'
            },
            legend: {
                right: 10,
                data: ['1990', '2015']
            },
            xAxis: {
                splitLine: {
                    lineStyle: {
                        type: 'dashed'
                    }
                }
            },
            yAxis: {
                splitLine: {
                    lineStyle: {
                        type: 'dashed'
                    }
                },
                scale: true
            },
            series: [{
                name: '1990',
                data: data[0],
                type: 'scatter',
                symbolSize: function (data) {
                    return Math.sqrt(data[2]) / 5e2;
                },
                label: {
                    emphasis: {
                        show: true,
                        formatter: function (param) {
                            return param.data[3];
                        },
                        position: 'top'
                    }
                },
                itemStyle: {
                    normal: {
                        shadowBlur: 10,
                        shadowColor: 'rgba(120, 36, 50, 0.5)',
                        shadowOffsetY: 5,
                        color: new echarts.graphic.RadialGradient(0.4, 0.3, 1, [{
                            offset: 0,
                            color: 'rgb(251, 118, 123)'
                        }, {
                            offset: 1,
                            color: 'rgb(204, 46, 72)'
                        }])
                    }
                }
            }, {
                name: '2015',
                data: data[1],
                type: 'scatter',
                symbolSize: function (data) {
                    return Math.sqrt(data[2]) / 5e2;
                },
                label: {
                    emphasis: {
                        show: true,
                        formatter: function (param) {
                            return param.data[3];
                        },
                        position: 'top'
                    }
                },
                itemStyle: {
                    normal: {
                        shadowBlur: 10,
                        shadowColor: 'rgba(25, 100, 150, 0.5)',
                        shadowOffsetY: 5,
                        color: new echarts.graphic.RadialGradient(0.4, 0.3, 1, [{
                            offset: 0,
                            color: 'rgb(129, 227, 238)'
                        }, {
                            offset: 1,
                            color: 'rgb(25, 183, 207)'
                        }])
                    }
                }
            }]
        };

        // google map show
        NgMap.getMap().then(function (map) {
            $http.get('/api/jobs_map/All').then(function (response) {
                console.log(response.data)
                markers.forEach(function(m){m.setMap(null)});
                markers.lenght=0;
                response.data.forEach(function(data){
                    var marker=new google.maps.Marker({
                        position:new google.maps.LatLng(data.lat,data.lon),
                        map: map,
                        opacity: 0.7,
                    });

                    markers.push(marker);

                    var infowindow = new google.maps.InfoWindow({
                        content: data.job_count+" "+data.search_word+" jobs @"+data.location
                    });

                    marker.addListener('click', function() {
                        map.setZoom(10);
                        map.setCenter(marker.getPosition());
                        infowindow.open(map,marker);
                    });
                });

            });

        });


    }
})();
