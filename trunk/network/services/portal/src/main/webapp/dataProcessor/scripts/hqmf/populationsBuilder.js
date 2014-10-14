/*
 * Builds the HQMF JSON Populations.
 */
define(['hqmf/populations'], function (Populations) 
{
    'use strict';
    var result =  
    { 
        buildPopulations: function(app) 
        {
            // Currently just one population.
            var populations = Populations.getInstance();
            populations.id = 'SCANNER';
            populations.title = 'Population1';
            
            return populations;
        }
    };
    return result;
});
