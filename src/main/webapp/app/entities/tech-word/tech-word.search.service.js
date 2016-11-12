(function() {
    'use strict';

    angular
        .module('cvApp')
        .factory('TechWordSearch', TechWordSearch);

    TechWordSearch.$inject = ['$resource'];

    function TechWordSearch($resource) {
        var resourceUrl =  'api/_search/tech-words/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
