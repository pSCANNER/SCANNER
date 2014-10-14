/* 
 * The HQMF JSON Populations class.
 */
define([ ], function () 
{
    'use strict';
    return {
        // Factory method to create instances.
        getInstance: function() 
        {
            var result = {
                IPP: 'IPP',         
                DENOM: 'DENOM',                
                NUMER: 'NUMER',
//                DENEX: 'DENEX',  
                id: null,
                title: null
            };
            return result;
        }
    };
});


