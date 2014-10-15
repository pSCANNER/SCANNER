/* 
 * Builds the HQMF JSON Measure Period.
 */
define(['hqmf/rangeConstraint'], function (RangeConstraint) 
{
    'use strict';
    var result =  
    { 
        buildMeasurePeriod: function(app) 
        {
            var period = null;
            if ( app )
            {
                // Look for startDate and endDate.
                var i = -1, j = -1;
                for (var k=0; k < app.initialPopulation.models.length && (i < 0 || j < 0); ++k)
                {
                    if ( app.initialPopulation.models[k].attributes.type === 'startDate' )
                        i = k;
                    else if ( app.initialPopulation.models[k].attributes.type === 'endDate' )
                        j = k;
                }
                var lowValue = {type:'TS', value: app.initialPopulation.models[i].attributes.name, 'inclusive?':true, 'derived?':false};
                var highValue = {type:'TS', value: app.initialPopulation.models[j].attributes.name, 'inclusive?':true, 'derived?':false};
                period = RangeConstraint.getInstance();
                period.initialize('IVL_TS', highValue, lowValue);
            }
            return period;
        }
    };
    return result;
});


