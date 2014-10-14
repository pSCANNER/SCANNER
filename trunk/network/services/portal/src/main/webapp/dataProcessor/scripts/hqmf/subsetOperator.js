/* 
 * The HQMF JSON Subset Operator class.
 */
define([], function () 
{
    'use strict';
    return {
        // Factory method to create instances.
        getInstance: function() 
        {
            var result = {
                type: 'COUNT',   //"COUNT" | "FIRST" | "SECOND" | "MOST RECENT"
                value: '',       // ValueConstraint
                initialize: function(type, value)
                {
                    this.type = type;   //"COUNT" | "FIRST" | "SECOND" | "MOST RECENT"
                    this.value = value; // ValueConstraint
                }
            };
            return result;
        }
    };
});


