/* 
 * The HQMF JSON Precondition Reference class
 */
define([], function () 
{
    'use strict';
    return {
        // Factory method to create instances.
        getInstance: function() 
        {
            var result = {
                id: 0,
                reference: '',
                initialize: function(id, referenceName)
                {
                    this.id = id; 
                    this.reference = referenceName + "_precondition_" + id; 
                }
            };
            return result;
        }
    };
});


