(function() {
    'use strict';
    angular
        .module('cvApp')
        .factory('TechWord', TechWord);

    TechWord.$inject = ['$resource'];

    function TechWord ($resource) {
        var resourceUrl =  'api/tech-words/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
