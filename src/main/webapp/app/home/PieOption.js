/**
 * Created by liuyufei on 4/11/16.
 */
(function () {
    'use strict';
    angular
        .module('cvApp')
        .factory('PieOption', PieOption);

    PieOption.$inject = ['$q', '$http'];

    function PieOption($q, $http) {
        var wordArray = [];
        function asyncGreet(url) {
            var deferred = $q.defer();
            $http.get(url).then(function (response) {
                // deferred.notify('About to greet ' + name + '.');
                console.log('PieOption', response.data);
                response.data.forEach(function (entry) {
                    wordArray.push(entry.name);
                })
                if (1 == 1) {
                    //build response based on response
                    var option = {
                        title: {
                            text: 'Programming Languages Job Count',
                            subtext: 'From SEEK',
                            x: 'center'
                        },
                        tooltip: {
                            trigger: 'item',
                            formatter: "{a} <br/>{b} : {c} ({d}%)"
                        },
                        legend: {
                            orient: 'vertical',
                            left: 'left',
                            data: wordArray
                        },
                        series: [
                            {
                                name: 'Job Opportunities',
                                type: 'pie',
                                radius: '55%',
                                center: ['50%', '60%'],
                                data: response.data,
                                itemStyle: {
                                    emphasis: {
                                        shadowBlur: 10,
                                        shadowOffsetX: 0,
                                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                                    }
                                }
                            }
                        ]
                    };
                    deferred.resolve(option);
                } else {
                    deferred.reject('no data');
                }
            });

            return deferred.promise;

        }

        return asyncGreet('api/jobs_count_word')
    }
})();
