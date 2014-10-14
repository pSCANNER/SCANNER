/* 
 * The HQMF JSON Population Rule class.
 */
define([ ], function () {
    'use strict';
    return {
        // Factory method to create instances.
        getInstance: function() 
        {
            var result = {
                type: '',         
                title: '',                
                hqmf_id: '',
                conjunction_code: 'allTrue',  // defaults to 'allTrue' if absent
                'conjunction?': true,
                preconditions: [],
                negation: false,
                id: null
            };
            return result;
        }
    };
});


