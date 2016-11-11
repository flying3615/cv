/**
 * Created by liuyufei on 4/11/16.
 */
(function() {
    'use strict';
    angular
        .module('cvApp')
        .factory('LineOption', LineOption);

    LineOption.$inject = ['$q','$http'];

    function LineOption ($q,$http) {
        var option =  {
            title: {
                text: 'Trend',
                subtext: 'Data from SEEK NZ'
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: []
            },
            toolbox: {
                show: true,
                feature: {
                    mark: {show: true},
                    dataView: {show: true, readOnly: false},
                    magicType: {show: true, type: ['line', 'bar']},
                    restore: {show: true},
                    saveAsImage: {show: true}
                }
            },
            dataZoom: [
                {
                    type: 'slider',
                    show: true,
                    xAxisIndex: [0],
                    start: 1,
                    end: 35
                },
                {
                    type: 'inside',
                    xAxisIndex: [0],
                    start: 1,
                    end: 35
                }
            ],
            calculable: true,
            xAxis: [
                {
                    type: 'category',
                    boundaryGap: false,
                    data: []
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    axisLabel: {
                        formatter: '{value}'
                    }
                }
            ],
            series: []
        };

        function asyncGreet(url) {
            var deferred = $q.defer();
            $http.get(url).then(function (response) {
                // deferred.notify('About to greet ' + name + '.');
                var legend_data=[];
                var dates = [];
                var series = [];
                response.data.forEach(function(server_data){
                    legend_data.push(server_data.name)
                    dates = server_data.date;
                    series.push( {
                        name: server_data.name,
                        type: 'line',
                        data: server_data.jobNum,
                        markPoint: {
                            data: [
                                {type: 'max', name: '最大值'},
                                {type: 'min', name: '最小值'}
                            ]
                        },
                        markLine: {
                            data: [
                                {type: 'average', name: '平均值'}
                            ]
                        }
                    })

                });

                if (1==1) {
                    //build response based on response
                    console.log('legend_data',legend_data);

                    option.legend.date= legend_data;
                    option.xAxis = [
                        {
                            type: 'category',
                            boundaryGap: false,
                            data: dates
                        }
                    ]
                    option.series = series;
                    deferred.resolve(option);
                } else {
                    deferred.reject('no data');
                }
            });

            return deferred.promise;

        }

        return asyncGreet('api/jobs_trend')
    }
})();
