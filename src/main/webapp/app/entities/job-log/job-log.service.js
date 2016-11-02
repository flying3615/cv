(function() {
    'use strict';
    angular
        .module('cvApp')
        .factory('JobLog', JobLog);

    JobLog.$inject = ['$resource', 'DateUtils'];

    function JobLog ($resource, DateUtils) {
        var resourceUrl =  'api/job-logs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.logDate = DateUtils.convertLocalDateFromServer(data.logDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.logDate = DateUtils.convertLocalDateToServer(copy.logDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.logDate = DateUtils.convertLocalDateToServer(copy.logDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
