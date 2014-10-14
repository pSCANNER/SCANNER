/* 
 * The HQMF JSON Global Counter instance. Returns the next available unique id.
 */
define([], function () 
{
    'use strict';
    return {
        count: -1,
        getNextValue: function() {
            this.count = this.count + 1;
            if ( this.count > Number.MAX_VALUE )
                this.count = 0;
            
            return this.count;
        }
    };
});


