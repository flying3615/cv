(function() {
    'use strict';

    angular
        .module('cvApp')
        .factory('SearchWordSearch', SearchWordSearch);

    SearchWordSearch.$inject = ['$resource'];

    function SearchWordSearch($resource) {
        var resourceUrl =  'api/_search/search-words/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
