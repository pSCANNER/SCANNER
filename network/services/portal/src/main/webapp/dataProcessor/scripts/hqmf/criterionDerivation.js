/* 
 * The HQMF JSON Criterion Derivation class.
 */
define([], function () 
{
    'use strict';
    return {
        // Factory method to create instances.
        getInstance: function() 
        {
            var result = {
                title:                     '',
                type:                      'derived',
                definition:                'derived',
                negation:                  false,
                source_data_criteria:      '',
                children_criteria:         [],
                derivation_operator:       null,
                subset_operators:          [],
                negation_code_list_id:     null,

                // these fields must be empty values or eqmxlate will complain:
                description:               '',
                standard_category:         '',
                qds_data_type:             '',
                hard_status:               '',
                temporal_references:       [],
                field_values:              {},
                property:                  '',
                status:                    '',
                specific_occurrence:       '',
                specific_occurrence_const: '',
                value:                     ''
            };
            return result;
        }
    };
});


