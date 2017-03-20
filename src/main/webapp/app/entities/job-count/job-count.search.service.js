(function() {
    'use strict';

    angular
        .module('cvApp')
        .factory('JobCountSearch', JobCountSearch);

    JobCountSearch.$inject = ['$resource'];

    function JobCountSearch($resource) {
        var resourceUrl =  'api/_search/job-counts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
