/* 
 * The HQMF JSON Criterion Code List class.
 */
define([], function () 
{
    'use strict';
    return    {
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
                            initialize: function(scanner_var)
                            {
                                this.title = scanner_var.name;
                                this.description = scanner_var.name;
                                this.source_data_criteria = scanner_var.name;
                                this.code_list_id = (scanner_var.conceptCode)? scanner_var.conceptCode : '';
//                                this.code_list_id = scanner_var.conceptId;
                                this.property = scanner_var.type;
                            },
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
                            }
            };
            return result;
        }
    };
});


