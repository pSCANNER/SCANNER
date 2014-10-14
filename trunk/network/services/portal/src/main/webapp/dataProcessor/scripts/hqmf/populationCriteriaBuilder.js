/* 
 * Builds the HQMF JSON Population Criteria.
 */
define([
    'hqmf/populationRule', 
    'hqmf/preconditionReference',
    'hqmf/conjunction',
    'hqmf/globalCounter',
    'hqmf/temporalReference',
    'hqmf/criterionCodeList'
], function (PopulationRule, PreconditionReference, Conjunction, Counter, TemporalReference, CriterionCodeList)
{
    'use strict';
    var result =  
    { 
        buildPopulationCriteria: function(app, allDataCriteria) 
        {
            var populationCriteria = {};
            if ( app )
            {
                // Build each of the population criterion.
                populationCriteria['IPP'] = this.buildIPPCriteria(app, allDataCriteria);
                populationCriteria['NUMER'] = this.buildNUMERCriteria(app, allDataCriteria);
                // For DENOM, have to build one population criteria for each time segment defined, if any.
                if ( this.isEmpty(allDataCriteria.time_segment_vars) )
                    populationCriteria['DENOM'] = this.buildDENOMCriteria(allDataCriteria, null);
                else
                {
                    // Loop thru each of the time_segment_vars and build a populaiton criteria that contains the variables 
                    // belonging to the segment.
                    var i = 1;
                    for ( var tiv in allDataCriteria.time_segment_vars )
                    {
                        populationCriteria['DENOM_'+i] = this.buildDENOMCriteria(allDataCriteria, allDataCriteria.time_segment_vars[tiv]);
                        i++;
                    }
                }
            }
            return populationCriteria;
        },
        
        // Builds the Initial Population criteria.
        buildIPPCriteria: function(app, allDataCriteria)
        {
            var ippRule = PopulationRule.getInstance();
            var dataCriteria = allDataCriteria.data_criteria;
            // Construct the IPP criterion as just the single SCANNER IPP variable.
            for (var k=0; k < app.initialPopulation.models.length; ++k)
            {
                if ( app.initialPopulation.models[k].attributes.type !== 'endDate' && 
                     app.initialPopulation.models[k].attributes.type !== 'startDate' )
                {
                    var preconditionReference = PreconditionReference.getInstance();
                    preconditionReference.initialize(Counter.getNextValue(), app.initialPopulation.models[k].attributes.name);
                    this.replaceDataCriterionName(dataCriteria, app.initialPopulation.models[k].attributes.name, preconditionReference.reference);

                    var conjunction = Conjunction.getInstance();
                    conjunction.id = Counter.getNextValue();
                    conjunction.preconditions.push(preconditionReference);

                    ippRule.type = 'IPP';
                    ippRule.title = 'Initial Patient Population';
                    ippRule.preconditions.push(conjunction);
                    break;
                }
            }
            return ippRule;
        },
        
        // Builds the Numerator population criteria.
        buildNUMERCriteria: function(app, allDataCriteria)
        {
            // Currently we are not using NUMER so just create an empty object.
            var numeratorPopulationRule = PopulationRule.getInstance();
            numeratorPopulationRule.type = 'NUMER';
            numeratorPopulationRule.title = 'Numerator';
            return numeratorPopulationRule;
        },

        // Builds the Denominator population criteria.
        buildDENOMCriteria: function(allDataCriteria, timeSegmentVars) 
        {
            var denominatorPopulationRule = PopulationRule.getInstance();
            var eventVars = allDataCriteria.event_vars;
            
            // Build the DENOM from the event variables.
            if ( eventVars )
            {
                // Loop thru all of the event variables looking for ones with logical operators.
                for (var ev in eventVars)
                {
                    var eventVar = eventVars[ev];
                    if ( eventVar.operator === 'OR' || eventVar.operator === 'AND' || 
                         eventVar.operator === 'ANY' )
                    {
                        // Build the logical expression for this event variable and temporarily attach the expression to the variable
                        // as an attribute.
                        eventVar.logicExpression = this.buildLogicalExpression(eventVar, eventVars, allDataCriteria, timeSegmentVars);
                    }
                }
                // Extract all the "un-used" event variables' logicExpressions to an array for later incorporation into
                // the denominator population rule object.
                var logicExpressions = [];
                for (var ev in eventVars)
                {
                    var eventVar = eventVars[ev];
                    if ( !eventVar.used && eventVar.logicExpression )
                    {
                        logicExpressions.push(eventVar.logicExpression);
                        // Remove any temporary attributes used.
                        delete eventVar.used;
                        delete eventVar.logicExpression;
                    }
                }

                // Fill in the DENOM population rule object with generated data.
                denominatorPopulationRule.type = 'DENOM';
                denominatorPopulationRule.title = 'Denominator';
                // Add the logic expression(s) as precondition(s) of the DENOM.
                if ( logicExpressions.length === 1 )
                    denominatorPopulationRule.preconditions.push(logicExpressions[0]);
                else if ( logicExpressions.length > 1 )
                {
                    var conjunction = Conjunction.getInstance();
                    conjunction.id = Counter.getNextValue();
                    for (var i=0; i < logicExpressions.length; ++i)
                    {
                        conjunction.preconditions.push(logicExpressions[i]);
                    }
                    denominatorPopulationRule.preconditions.push(conjunction);
                }
            }
            return denominatorPopulationRule;
        },

        // Recursively builds the logical expressions from the given logic event variable.
        buildLogicalExpression: function(eventVar, eventVars, allDataCriteria, timeSegmentVars) 
        {
            var conjunction = Conjunction.getInstance();
            conjunction.id = Counter.getNextValue();
            if ( eventVar.operator === 'OR' || eventVar.operator === 'ANY' )
                conjunction.conjunction_code = 'atLeastOneTrue';
            
            var dataCriteria = allDataCriteria.data_criteria;
            // Loop thru the variables contained in this event variable. There can be nested logic event variables.
            for (var i=0; i < eventVar.variables.length; ++i)
            {
                var variable = eventVar.variables[i].attributes;
                if ( variable.type !== 'Event' )
                {
                    // Reached a "leaf" variable, determine how to handle it based on whether it is time segmented or not.
                    var timeSegmentVar = this.lookupTimeSegmentVar(variable.name, timeSegmentVars);
                    if ( timeSegmentVar )
                        conjunction.preconditions.push(this.processTimeSegmentLeafVariable(timeSegmentVar, allDataCriteria));
                    else
                        conjunction.preconditions.push(this.processLeafVariable(variable.name, allDataCriteria));
                }
                else if ( TemporalReference.lookup(variable.operator) )
                {
                    // Reached a "non-leaf" variable but it is a time event. Treat it as a "leaf" variable.
                    // Reference the left-hand-side of the time event as the precondition.
                    // Generate a new precondition reference only if it doesn't already exists 
                    // for this variable in the data_criteria. Otherwise re-use reference.
                    var preconditionReference = this.checkPreconditionReferenceExistance(dataCriteria, variable.variables[0].attributes.name);
                    if ( !preconditionReference )
                    {
                        preconditionReference = PreconditionReference.getInstance();
                        preconditionReference.initialize(Counter.getNextValue(), variable.variables[0].attributes.name);
                        this.replaceDataCriterionName(dataCriteria, variable.variables[0].attributesname, preconditionReference.reference);
                    }
                    conjunction.preconditions.push(preconditionReference);
                }
                else if ( variable.operator === 'OR' || variable.operator === 'AND' || variable.operator === 'ANY' )
                {
                    // Reached a "non-leaf" variable. If the same event variable exists in the top-level eventVar list mark it as "used".
                    // Only non-used top-level event variables will be collected as population criteria later.
                    if ( eventVars[variable.name] )
                        eventVars[variable.name].used = true;
                    // Recursively build the nested logical expression.
                    conjunction.preconditions.push(this.buildLogicalExpression(variable, eventVars, allDataCriteria, timeSegmentVars));
                }
            }
            return conjunction;
        },
        // Replaces the name of existing variable in the data_criteria with the new name.
        replaceDataCriterionName: function(dataCriteria, name, newName)
        {
            // Find the original variable name and replace with the new name.
            if ( dataCriteria[name] )
            {
                dataCriteria[newName] = dataCriteria[name];
                delete dataCriteria[name];
            }
        },
        // Finds existing precondition reference variable in the data_criteria with the given name.
        checkPreconditionReferenceExistance: function(dataCriteria, name)
        {
            var result = null;
            // Find the non time segment precondition with the same source name in the data_criteria.
            for (var x in dataCriteria)
            {
                if ( !dataCriteria[x].specific_occurrence || dataCriteria[x].specific_occurrence === "")
                {
                    var ndx = x.indexOf("_precondition_");
                    var id = (ndx > 0)? x.substring(ndx+"_precondition_".length, x.length) : "";
                    if ( id !== "" && dataCriteria[x].source_data_criteria === name )
                    {
                        // Take the name of the variable as the reference name and extract the id from it.
                        result = PreconditionReference.getInstance();
                        result.id = Number(id);
                        result.reference = x;
                        break;
                    }
                }
            }
            return result;
        },
        // Finds existing time segment precondition reference variable in the data_criteria with the given name.
        checkTimeSegmentPreconditionReferenceExistance: function(dataCriteria, name)
        {
            var result = null;
            // Find the precondition with the same prefix name in the data_criteria.
            for (var x in dataCriteria)
            {
                var ndx = x.indexOf("_precondition_");
                var id = (ndx > 0)? x.substring(ndx+"_precondition_".length, x.length) : "";
                if ( id !== "" && x.indexOf(name) >= 0 )
                {
                    // Take the name of the variable as the reference name and extract the id from it.
                    result = PreconditionReference.getInstance();
                    result.id = Number(id);
                    result.reference = x;
                    break;
                }
            }
            return result;
        },
        // Finds time segment variable given its original name.
        lookupTimeSegmentVar: function(name, timeSegmentVars)
        {
            var result = null;
            for (var i=0; timeSegmentVars && i<timeSegmentVars.length; ++i)
            {
                if ( timeSegmentVars[i].variable === name )
                {
                    result = timeSegmentVars[i];
                    break;
                }
            }
            return result;
        },
        // Creates a specifc occureence variable from the given timeSegment variable in data_criteria.
        createSpecificOccurrenceVariable: function(name, timeSegmentVar, allDataCriteria)
        {
            // Find the variable with the same name in source_data_criteria.
            var variable = allDataCriteria.src_data_criteria[timeSegmentVar.variable];
            if ( variable )
            {
                var clone = CriterionCodeList.getInstance();
                clone.copy(variable);
                // Make this a specific occurrence to the timesegment interval.
                clone.specific_occurrence = timeSegmentVar.interval;
                clone.specific_occurrence_const = clone.description;
                // Add the specific occurrence to data_criteria section.
                allDataCriteria.data_criteria[name] = clone;
            }
        },
        // Process normal leaf variables in a logical expression.
        processLeafVariable: function(varName, allDataCriteria)
        {
            var dataCriteria = allDataCriteria.data_criteria;
            // Generate a new precondition reference only if it doesn't already exists 
            // for this variable in the data_criteria. Otherwise re-use reference.
            var preconditionReference = this.checkPreconditionReferenceExistance(dataCriteria, varName);
            if ( !preconditionReference )
            {
                preconditionReference = PreconditionReference.getInstance();
                preconditionReference.initialize(Counter.getNextValue(), varName);
                this.replaceDataCriterionName(dataCriteria, varName, preconditionReference.reference);
            }
            return preconditionReference;
        },
        // Process time segment leaf variables in a logical expression.
        processTimeSegmentLeafVariable: function(timeSegmentVar, allDataCriteria)
        {
            var dataCriteria = allDataCriteria.data_criteria;
            // Generate a new precondition reference only if it doesn't already exists 
            // for this variable in the data_criteria. Otherwise re-use reference.
            // Use the name defined in the timeSegment as the reference name to check
            // for existance in data_criteria.
            var preconditionReference = this.checkTimeSegmentPreconditionReferenceExistance(dataCriteria, timeSegmentVar.name);
            if ( !preconditionReference )
            {
                preconditionReference = PreconditionReference.getInstance();
                preconditionReference.initialize(Counter.getNextValue(), timeSegmentVar.name);
                // Create a HQMF specific occurrence variable for this timeSegment variable in the data_criteria.
                this.createSpecificOccurrenceVariable(preconditionReference.reference, timeSegmentVar, allDataCriteria);
            }
            return preconditionReference;
        },
        // Determines if the given object is empty without any properties.
        isEmpty: function ( o ) 
        {
            for ( var p in o ) { 
                if ( o.hasOwnProperty( p ) ) { return false; }
            }
            return true;
        }
    };
    return result;
});


