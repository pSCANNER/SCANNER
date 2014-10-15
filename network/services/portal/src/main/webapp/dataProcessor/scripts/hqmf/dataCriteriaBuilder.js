/* 
 * Builds the HQMF JSON Data Criteria.
 */
define([
    'hqmf/criterionCodeList', 
    'hqmf/temporalReference',
    'hqmf/rangeConstraint'
], function (CriterionCodeList, TemporalReference, RangeConstraint) 
{
    'use strict';
    var result =  { 
        buildDataCriteria: function(app) 
        {
            var allDataCriteria = {};
            var dataCriteria = {};
            var srcDataCriteria = {};
            var eventVars = {};
            var timeSegmentVars = {};
            var startEndTemporalReferences = {};
            var indexedVar = null;
            if ( app )
            {
                // Convert the initial population variables into data criteria.
                for (var k=0; k < app.initialPopulation.models.length; ++k)
                {
                    var ipVar = app.initialPopulation.models[k].attributes;
                    if ( ipVar.type !== 'Event' && ipVar.type !== 'endDate' && ipVar.type !== 'startDate' )
                    {
                        // Create HQMF data_criteria.
                        var criterionCodeList = CriterionCodeList.getInstance();
                        criterionCodeList.initialize(ipVar, CriterionCodeList.typeMappingTable);
                        dataCriteria[ipVar.name] = criterionCodeList;
                        
                        // Create HQMF source_data_criteria.
                        criterionCodeList = CriterionCodeList.getInstance();
                        criterionCodeList.initialize(ipVar, CriterionCodeList.typeMappingTable);
                        srcDataCriteria[ipVar.name] = criterionCodeList;
                    }
                }
                // Convert the core variables into data criteria.
                for (var k=0; k < app.variables.models.length; ++k)
                {
                    var coreVar = app.variables.models[k].attributes;
                    if ( coreVar.type !== 'Event' )
                    {
                        // Create HQMF data_criteria.
                        var criterionCodeList = CriterionCodeList.getInstance();
                        criterionCodeList.initialize(coreVar, CriterionCodeList.typeMappingTable);
                        dataCriteria[coreVar.name] = criterionCodeList;
                        
                        // Create HQMF source_data_criteria.
                        criterionCodeList = CriterionCodeList.getInstance();
                        criterionCodeList.initialize(coreVar, CriterionCodeList.typeMappingTable);
                        srcDataCriteria[coreVar.name] = criterionCodeList;
                    }
                    // Save any event variables for later processing.
                    else 
                    {
                        eventVars[coreVar.name] = coreVar;
                    }
                }
                // Process the event variables, if any.
                for (var x in eventVars)
                {
                    var eventVar = eventVars[x];
                    // Convert any temporal variables into data_criteria.
                    if ( TemporalReference.lookup(eventVar.operator) )
                        this.convertTimeSequence(eventVar, dataCriteria);
                    // Keeps track of the distinct indexed variable, if defined. Currently SCANNER allows only one such variable.
                    // Also mark this variable as specific occurence for HQMF.
                    if ( eventVar.distinct && eventVar.index )
                        indexedVar = eventVar;
                    // Mark the nested logic expression event variables, if any. The non-marked ones will be used in generation
                    // of the logic expression tree.
                    if ( eventVar.operator === 'OR' || eventVar.operator === 'AND' || eventVar.operator === 'ANY' )
                        this.markNestedLogicExpressionTrees(eventVar, eventVars);
                }
                
                // Aggregate the wide dataset entries with the same segment together into a map of segment variables.
                for (var k=0; k < app.wideDataSet.models.length; ++k)
                {
                    var wideVar = app.wideDataSet.models[k].attributes;
                    if ( wideVar.interval )
                    {
                        // If interval exists, add this entry to the interval.
                        if ( timeSegmentVars[wideVar.interval] )
                        {
                            timeSegmentVars[wideVar.interval].push(wideVar);
                        }
                        // Otherwise, create a new interval array with this entry.
                        else
                        {
                            timeSegmentVars[wideVar.interval] = [wideVar];
                        }
                    }
                }
                // Convert the time segment entries into a map of segment ids to HQMF temporal reference of the indexed variable.
                // This map will be used later to insert the temporal references into the wide dataset entries that matches the
                // corresponding segment name.
                for (var k=0; k < app.timeSegments.models.length; ++k)
                {
                    var timeSegment = app.timeSegments.models[k].attributes;
                    // Create an array of temporal references (start and end) to the indexed variable and add to the map.
                    startEndTemporalReferences[timeSegment.name] = this.createStartAndEndTemporalReferences(timeSegment, indexedVar);
                }
            }
            allDataCriteria['data_criteria'] = dataCriteria;
            allDataCriteria['src_data_criteria'] = srcDataCriteria;
            allDataCriteria['event_vars'] = eventVars;
            allDataCriteria['time_segment_vars'] = timeSegmentVars;
            allDataCriteria['start_end_temporal_refs'] = startEndTemporalReferences;
            return allDataCriteria;
        },
        
        // Converts time sequence variables into HQMF temporal reference relationships between variables.
        convertTimeSequence: function(timeEvent, dataCriteria)
        {
            if ( timeEvent && dataCriteria && timeEvent.variables.length === 2 )
            {
                // Find reference to left-hand-side of timeEvent within the data criteria.
                var lhs = dataCriteria[timeEvent.variables[0].attributes.name];
                // Find reference to right-hand-side of timeEvent within the data criteria.
                var rhs = dataCriteria[timeEvent.variables[1].attributes.name];
                // Add the rhs as a temporal reference in the lhs, if they exist.
                if ( lhs && rhs )
                {
                    var temporalRef = TemporalReference.getInstance();
                    temporalRef.initialize(TemporalReference.lookup(timeEvent.operator), timeEvent.variables[1].attributes.name, null, null);
                    lhs.temporal_references.push(temporalRef);
                }
            }
        },

        createStartAndEndTemporalReferences: function(timeSegment, indexedVar)
        {
            var result = [];
            
            // Create the start temporal reference.
            var type = this.determineStartTemporalReferenceType(timeSegment.start, indexedVar.indexAlignment);
            var range = this.determineStartTemporalReferenceRange(timeSegment.start);
            var startTemporalRef = TemporalReference.getInstance();
            startTemporalRef.initialize(type, indexedVar.variables.attributes.name, range, null);
            result.push(startTemporalRef);
            // Create the end temporal reference.
            type = this.determineEndTemporalReferenceType(timeSegment.endInterval, indexedVar.indexAlignment);
            range = this.determineEndTemporalReferenceRange(timeSegment.endInterval);
            var endTemporalRef = TemporalReference.getInstance();
            endTemporalRef.initialize(type, indexedVar.variables.attributes.name, range, null);
            result.push(endTemporalRef);
            
            return result;
        },
        
        // Inserts HQMF temporal references into all preconditions defined in data_criteria.
        insertTemporalReferences: function(allDataCriteria)
        {
            var timeSegmentVars = allDataCriteria.time_segment_vars;
            for ( var dcName in allDataCriteria.data_criteria)
            {
                // Check data criteria that are specific occurrences against the time segment variables.
                if ( allDataCriteria.data_criteria[dcName].specific_occurrence )
                {
                    for ( var ts in timeSegmentVars )
                    {
                        var timeSegmentVar = timeSegmentVars[ts];
                        for ( var i=0; i<timeSegmentVar.length; ++i )
                        {
                            // Look for matching time segment variable name and matching time segment interval.
                            if ( allDataCriteria.data_criteria[dcName].specific_occurrence === ts &&
                                 allDataCriteria.data_criteria[dcName].source_data_criteria === timeSegmentVar[i].variable )
                            {
                                allDataCriteria.data_criteria[dcName].temporal_references = 
                                        allDataCriteria.start_end_temporal_refs[timeSegmentVar[i].interval];
                                break
                            }
                        }
                    }
                }
                // Add temporal reference of the measure period if this data criteria is a precondition in a logic expression.
                else if ( dcName.indexOf("_precondition_") >= 0 )
                {
                    allDataCriteria.data_criteria[dcName].temporal_references = [{type: 'DURING', reference: 'measure_period'}];
                }
            }
        },
        
        determineStartTemporalReferenceType: function(startValue, indexedVarAlignment)
        {
            var result = null;
            // Determine the start temporal reference type (e.g., SBS, SBE, SDU, etc.) based on the sign of the
            // start value and the indexed variable's alignment. 
            // e.g., If negative it is "start before"; if zero, "start during"; if positive "start after"
            if ( indexedVarAlignment === "startDate" )
            {
                if ( startValue < 0 )
                    result = "SBS";
                else if ( startValue == 0 )
                    result = "SDU";
                else 
                    result = "SAS";
            }
            else
            {
                if ( startValue < 0 )
                    result = "SBE";
                else if ( startValue == 0 )
                    result = "SDU";
                else 
                    result = "SAE";
            }
            
            return result;
        },
        
        determineStartTemporalReferenceRange: function(startValue)
        {
            var result = null;
            // Determine the start temporal reference range (e.g., number of days before or after start date of indexed variable)
            if ( startValue < 0 )
            {
                result = RangeConstraint.getInstance();
                result.initialize("IVL_PQ", null, {type: "PQ", unit:"d", value: Math.abs(Number(startValue)), 'inclusive?': false, 'derived?': false});
            }
            else if ( startValue > 0 )
            {
                result = RangeConstraint.getInstance();
                result.initialize("IVL_PQ", {type: "PQ", unit:"d", value: startValue, 'inclusive?': false, 'derived?': false}, null);
            }
            
            return result;
        },
        
        determineEndTemporalReferenceType: function(endValue, indexedVarAlignment)
        {
            var result = null;
            // Determine the end temporal reference type (e.g., EBS, EBE, EDU, etc.) based on the sign of the
            // end value and the indexed variable's alignment. 
            // e.g., If negative it is "end before"; if zero, "end during"; if positive "end after"
            if ( indexedVarAlignment === "startDate" )
            {
                if ( endValue < 0 )
                    result = "EBS";
                else if ( endValue == 0 )
                    result = "EDU";
                if ( endValue > 0 )
                    result = "EAS";
            }
            else
            {
                if ( endValue < 0 )
                    result = "EBE";
                else if ( endValue == 0 )
                    result = "EDU";
                if ( endValue > 0 )
                    result = "EAE";
            }
            return result;
        },
        
        determineEndTemporalReferenceRange: function(endValue)
        {
            var result = null;
            // Determine the end temporal reference range (e.g., number of days after or before end date of indexed variable)
            if ( endValue < 0 )
            {
                result = RangeConstraint.getInstance();
                result.initialize("IVL_PQ", null, {type: "PQ", unit:"d", value: Math.abs(Number(endValue)), 'inclusive?': false, 'derived?': false});
            }
            else if ( endValue > 0 )
            {
                result = RangeConstraint.getInstance();
                result.initialize("IVL_PQ", {type: "PQ", unit:"d", value: endValue, 'inclusive?': false, 'derived?': false}, null);
            }
            return result;
        },
        // Recursively walks the event variables that contain logic operators and mark those that are nested inside others.
        markNestedLogicExpressionTrees: function(eventVar, eventVars) 
        {
            // Loop thru the variables contained in this event variable.
            for (var i=0; i < eventVar.variables.length; ++i)
            {
                var variable = eventVar.variables[i].attributes;
                if ( variable.operator === 'OR' || variable.operator === 'AND' || variable.operator === 'ANY' )
                {
                    // Reached a "non-leaf" variable. If the same event variable exists in the top-level eventVar list mark 
                    // it as "used" by this event variable. Only non-used top-level event variables will be collected as 
                    // top-level trees for later processing.
                    if ( eventVars[variable.name] )
                        eventVars[variable.name].used = true;
                    // Recursively walk the nested event variables.
                    this.markNestedLogicExpressionTrees(variable, eventVars);
                }
            }
        }
    };
    return result;
});


