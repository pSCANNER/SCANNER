/*
 * Generates the HQMF JSON from the scanner internal JSON.
 * This is the entry point into the SCANNER HQMF generation.
 */
define([
    'hqmf/measure',
    'hqmf/dataCriteriaBuilder',
    'hqmf/populationsBuilder',
    'hqmf/populationCriteriaBuilder',
    'hqmf/measurePeriodBuilder'
], function (Measure, DataCriteriaBuilder, PopulationsBuilder, PopulationCriteriaBuilder, MeasurePeriodBuilder) 
{
    'use strict';
    var generator = {
        generate: function(app) 
        {
            // Preprocess the input data.
            this.normalizeAllNames(app);
            // Build and assemble the components of the HQMF measure object.
            var measure = Measure.getInstance();
            // The data_criteria must be done before the population criteria because the populaiton criteria reference
            // the variables in the data_criteria.
            var allDataCriteria = DataCriteriaBuilder.buildDataCriteria(app);
            measure.measure_period = MeasurePeriodBuilder.buildMeasurePeriod(app);
            measure.data_criteria = allDataCriteria.data_criteria;
            measure.source_data_criteria = allDataCriteria.src_data_criteria;
            measure.population_criteria = PopulationCriteriaBuilder.buildPopulationCriteria(app, allDataCriteria);
            PopulationsBuilder.buildPopulations(app, measure);
            // The following must be done after the population criteria are done because the population criteria will generate
            // time segment variables in data_criteria as it builds the logical expressions.
            DataCriteriaBuilder.insertTemporalReferences(allDataCriteria);
            
            return measure;
        },
        normalizeAllNames: function(app) 
        {
            this.normalizeIPPNames(app.initialPopulation.models);
            this.normalizeVariableNames(app.variables.models);
        },
        normalizeIPPNames: function(ipVars) 
        {
            for (var k=0; k < ipVars.length; ++k)
            {
                var ipVar = ipVars[k].attributes;
                if ( ipVar.type !== 'Event' && ipVar.type !== 'endDate' && ipVar.type !== 'startDate' )
                    ipVar.name = ipVar.name.replace(/[\s,\[\]]/g, "");
            }
        },                
        normalizeVariableNames: function(coreVars) 
        {
            for (var k=0; k < coreVars.length; ++k)
            {
                var coreVar = coreVars[k].attributes;
                if ( coreVar.type !== 'Event' )
                    coreVar.name = coreVar.name.replace(/[\s,\[\]]/g, "");
                // Recursively normalize all nested variables, if any.
                if ( coreVar.variables )
                    this.normalizeVariableNames(coreVar.variables);
            }
        }
    };
    return generator;
});
