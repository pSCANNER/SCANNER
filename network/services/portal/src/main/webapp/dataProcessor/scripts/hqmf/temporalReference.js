/* 
 * The HQMF JSON Temporal Reference class.
 */
define([], function () 
{
    'use strict';
    return  {
        // Factory method to create instances.
        getInstance: function() {
            var result = {
                type: 'CONCURRENT', // "CONCURRENT" | "DURING" |  "SDU" | "EDU" |  "SBS" | "SAS" | "SBE" | "SAE" |  "EBS" | "EAS" | "EBE" | "EAE"
                reference: null, // Criterion | TemporalReference | ConstantText,
                range: null, // RangeConstraint | null, defaults to null if absent
                title: null, // ConstantText | null, defaults to null if absent
                initialize: function(type, reference, range, title)
                {
                    this.type = type; 
                    this.reference = reference; 
                    this.range = range; 
                    this.title= title; 
                }
            };
            return result;
        },
        lookupTable: {
            'concurrent with': 'CONCURRENT',
            'during': 'DURING',
            'starts during': 'SDU',
            'ends during': 'EDU',
            'starts before start of': 'SBS',
            'starts after start of': 'SAS',
            'starts before end of': 'SBE',
            'starts after end of': 'SAE',
            'ends before start of': 'EBS',
            'ends after start of': 'EAS',
            'ends before or during': 'EBE',
            'ends after end of': 'EAE'
        },
        lookup: function(operatorName) {
            return this.lookupTable[operatorName];
        }
    };
});


