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
                initialize: function(type, high, low)
                {
                    this.type = type;
                    if ( high )
                        this.high = high;
                    if ( low )
                        this.low = low;
                }
            };
            return result;
        }
    };
});


