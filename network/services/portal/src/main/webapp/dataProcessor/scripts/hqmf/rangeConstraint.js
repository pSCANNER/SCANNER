/* 
 * The HQMF JSON Range Constraint class.
 */
define([], function () 
{
    'use strict';
    return  {
        // Factory method to create instances.
        getInstance: function() 
        {
            var result = {
                type: '',
                high: null, // Value | null, defaults to null if absent
                low: null,   // Value | null, defaults to null if absent
                initialize: function(type, high, low)
                {
                    this.type = type;
                    this.high = high;
                    this.low = low;
                }
            };
            return result;
        }
    };
});


