/* 
 * The HQMF JSON Measure object.
 */
define([ ], function () 
{
    'use strict';
    return {
        // Factory method to create instances.
        getInstance: function() 
        {
            var result = {
                id: '',         
                title: '',                
                description: '',
                population_criteria: {},  
                data_criteria: {},
                source_data_criteria: {},
                populations: [],
                measure_period: {}
            };
            return result;
        }
    };
});


