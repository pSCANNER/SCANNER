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
                var lowValue = {type:'TS', value: this.formatDateTime(app.initialPopulation.models[i].attributes.name), 'inclusive?':true, 'derived?':false};
                var highValue = {type:'TS', value: this.formatDateTime(app.initialPopulation.models[j].attributes.name), 'inclusive?':true, 'derived?':false};
                period = RangeConstraint.getInstance();
                period.initialize('IVL_TS', highValue, lowValue);
            }
            return period;
        },
        // Converts dateTimeString to HQMF format (YYYYMMDDhhmm).
        formatDateTime: function(dateTimeString) 
        {
                var d = new Date(dateTimeString);
                var year = d.getFullYear().toString();
                var month = (d.getMonth()+1).toString();
                // Pad with leading zero if less than 2 digits.
                var day = d.getDate().toString();
                if ( day.length === 1 )
                    day = '0'+day;
                // Pad with leading zero if less than 2 digits.
                var hours = d.getHours().toString();
                if ( hours.length === 1 )
                    hours = '0'+hours;
                // Pad with leading zero if less than 2 digits.
                var minutes = d.getMinutes().toString();
                if ( minutes.length === 1 )
                    minutes = '0'+minutes;
                
            return year+month+day+hours+minutes;
        }
    };
    return result;
});


