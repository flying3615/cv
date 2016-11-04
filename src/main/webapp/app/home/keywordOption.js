/**
 * Created by liuyufei on 4/11/16.
 */
(function() {
    'use strict';
    angular
        .module('cvApp')
        .factory('KeywordOption', KeywordOption);

    KeywordOption.$inject = ['$resource', 'DateUtils'];

    function KeywordOption ($resource, DateUtils) {
        var option =  {
            title: {
                text: 'Trend',
                subtext: 'Data from SEEK NZ'
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['Java', '.Net', 'Ruby', 'Python','JavaScript']
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
                    data: ['29/10', '30/10', '31/10', '1/11', '2/11', '3/11', '4/11']
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
            series: [
                {
                    name: 'Java',
                    type: 'line',
                    data: [199, 220, 165, 180, 240, 138, 167],
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
                },
                {
                    name: '.Net',
                    type: 'line',
                    data: [269, 220, 253, 267, 299, 276, 284],
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
                },
                {
                    name: '.JavaScript',
                    type: 'line',
                    data: [362, 319, 299, 276, 343, 310, 267],
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
                },
                {
                    name: 'Ruby',
                    type: 'line',
                    data: [44, 41, 38, 46, 49, 52, 39],
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
                },
                {
                    name: 'Python',
                    type: 'line',
                    data: [94, 92, 90, 100, 103, 89, 94],
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
                }
            ]
        };
        return option
    }
})();
