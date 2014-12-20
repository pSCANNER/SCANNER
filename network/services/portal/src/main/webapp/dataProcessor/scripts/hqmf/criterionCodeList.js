/* 
 * The HQMF JSON Criterion Code List class.
 */
define([], function () 
{
    'use strict';
    return  {
        typeMappingTable: 
        {
//          'User Defined Variables': {},
            'Drugs': {qds: 'medication_order', st_cat: 'medication', status: 'ordered', type: 'medications'},
            'Demographics': {qds: 'individual_characteristic', st_cat: 'individual_characteristic', type: 'characteristic'},
            'Clinical Findings': {qds: 'diagnosis_active', st_cat: 'diagnosis_condition_problem', status: 'active', type: 'conditions'}
        },
        // Factory method to create instances.
        getInstance: function()
        {
            var result = {
                            title:                     '',
                            description:               '',
                            standard_category:         '',
                            qds_data_type:             '',
                            type:                      '',
                            definition:                '',
                            hard_status:               false,
                            negation:                  false,
                            source_data_criteria:      '',
                            code_list_id:              '',
                            temporal_references:       [],
                            subset_operators:          [],
                            field_values:              {},
                            property:                  null,
                            status:                    null,
                            negation_code_list_id:     null,
                            specific_occurrence:       null,
                            specific_occurrence_const: null,
                            value:                     null,
                            inline_code_list:          {}, // this field is ignored by eqmxlate
                            copy: function(criterionCodeList)
                            {
                                this.title = criterionCodeList.title;
                                this.description = criterionCodeList.description;
                                this.standard_category = criterionCodeList.standard_category;
                                this.qds_data_type = criterionCodeList.qds_data_type;
                                this.type = criterionCodeList.type;
                                this.definition = criterionCodeList.definition;
                                this.hard_status = criterionCodeList.hard_status;
                                this.negation = criterionCodeList.negation;
                                this.source_data_criteria = criterionCodeList.source_data_criteria;
                                this.code_list_id = criterionCodeList.code_list_id;
                                this.temporal_references = criterionCodeList.temporal_references;
                                this.subset_operators = criterionCodeList.subset_operators;
                                this.field_values = criterionCodeList.field_values;
                                this.property = criterionCodeList.property;
                                this.status = criterionCodeList.status;
                                this.negation_code_list_id = criterionCodeList.negation_code_list_id;
                                this.specific_occurrence = criterionCodeList.specific_occurrence;
                                this.specific_occurrence_const = criterionCodeList.specific_occurrence_const;
                                this.value = criterionCodeList.value;
                            },
                            // Initializes this object with data from the given scanner variable.
                            initialize: function(scanner_var, typeMappingTable)
                            {
                                this.title = scanner_var.name;
                                this.description = scanner_var.name;
                                this.source_data_criteria = scanner_var.name;
                                if ( scanner_var.conceptCode )
                                    this.code_list_id = scanner_var.conceptCode;
                                else if ( scanner_var.conceptId && !isNaN(scanner_var.conceptId) )
                                    this.code_list_id = "omop_concept_" + scanner_var.conceptId;
                                if ( typeMappingTable )
                                {
                                    var hqmfProps = typeMappingTable[scanner_var.type];
                                    if ( hqmfProps )
                                    {
                                        this.qds_data_type = hqmfProps.qds;
                                        this.standard_category = hqmfProps.st_cat;
                                        this.status = hqmfProps.status;
                                        this.type = hqmfProps.type;
                                    }
                                }
                            }
            };
            return result;
        }
    };
});


