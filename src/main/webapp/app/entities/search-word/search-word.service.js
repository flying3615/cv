(function() {
    'use strict';
    angular
        .module('cvApp')
        .factory('SearchWord', SearchWord);

    SearchWord.$inject = ['$resource'];

    function SearchWord ($resource) {
        var resourceUrl =  'api/search-words/:id';

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
