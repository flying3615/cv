(function() {
    'use strict';

    angular
        .module('cvApp')
        .factory('JobLogSearch', JobLogSearch);

    JobLogSearch.$inject = ['$resource'];

    function JobLogSearch($resource) {
        var resourceUrl =  'api/_search/job-logs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
