/* 
 * The HQMF JSON Conjunction class.
 */
define([], function () 
{
    'use strict';
    return {
        // Factory method to create instances.
        getInstance: function() 
        {
            var result = {
                conjunction_code : 'allTrue',
                'conjunction?' : true,
                preconditions:    [],
                negation:         false,
                id:               null
            };
            return result;
        }
    };
});


