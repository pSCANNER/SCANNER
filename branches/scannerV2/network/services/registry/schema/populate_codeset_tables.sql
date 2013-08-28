insert into drugs_code_set (
    concept_id,
    concept_name,
    valid_start_date,
    valid_end_date,
    concept_level,
    concept_class,
    vocabulary_id,
    concept_code
) select distinct
    concept_id,
    concept_name,
    valid_start_date,
    valid_end_date,
    concept_level,
    concept_class,
    vocabulary_id,
    concept_code
   from concept c join concept_ancestor a on c.concept_id = a.ancestor_concept_id
   where c.concept_level = 2 and
              now() <= c.valid_end_date and
              c.vocabulary_id = 8 and
              c.concept_class = 'Ingredient';

insert into conditions_code_set (
    concept_id,
    concept_name,
    valid_start_date,
    valid_end_date,
    concept_level,
    concept_class,
    vocabulary_id,
    concept_code
) select distinct
    concept_id,
    concept_name,
    valid_start_date,
    valid_end_date,
    concept_level,
    concept_class,
    vocabulary_id,
    concept_code
   from concept c join concept_ancestor a on c.concept_id = a.ancestor_concept_id
   where c.concept_level = 2 and
              now() <= c.valid_end_date and
              c.vocabulary_id = 1 and
              c.concept_class = 'Clinical finding';

insert into favorites_code_set (
    concept_id,
    concept_name,
    valid_start_date,
    valid_end_date,
    concept_level,
    concept_class,
    vocabulary_id,
    concept_code
) select distinct
    concept_id,
    concept_name,
    valid_start_date,
    valid_end_date,
    concept_level,
    concept_class,
    vocabulary_id,
    concept_code
   from concept c
   where c.concept_level = 2 and
              now() <= c.valid_end_date and
              c.vocabulary_id = 40000;


