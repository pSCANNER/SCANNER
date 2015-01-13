/*
 * Builds the HQMF JSON Populations.
 */
define(['hqmf/populations'], function (Populations) 
{
    'use strict';
    var result =  
    { 
        buildPopulations: function(app, measure) 
        {
            // Create a population for each DENOM found in the current measure.
            var counter = 1;
            for ( var pc in measure.population_criteria )
            {
                if ( pc.indexOf("DENOM") >= 0 )
                {
					var count = counter++;
                    var population = Populations.getInstance();
                    population.id = 'Population' + count;
                    population.DENOM = pc;
                    population.title = 'Population ' + count;
                    measure.populations.push(population);
                }
            }
        }
    };
    return result;
});
