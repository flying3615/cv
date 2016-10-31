(function() {
    'use strict';
    angular
        .module('cvApp')
        .factory('Job', Job);

    Job.$inject = ['$resource', 'DateUtils'];

    function Job ($resource, DateUtils) {
        var resourceUrl =  'api/jobs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.list_date = DateUtils.convertLocalDateFromServer(data.list_date);
                        data.creation_time = DateUtils.convertDateTimeFromServer(data.creation_time);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.list_date = DateUtils.convertLocalDateToServer(copy.list_date);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.list_date = DateUtils.convertLocalDateToServer(copy.list_date);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
