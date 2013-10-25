import sys
import string
from funcs_types import *
import json
import re

def dictmerge(dlist):
    d = dict()
    for ds in dlist:
        d.update(ds)
    return d

def flatten(v):
    if hasattr(v, 'flatten'):
        return v.flatten()
    elif type(v) == dict:
        return dict([ (k, flatten(v2)) for k, v2 in v.items() ])
    elif type(v) == list:
        return [ flatten(v2) for v2 in v ]
    else:
        return v

def simplify(astnode, measure):
    if hasattr(astnode, 'simplify'):
        return astnode.simplify(measure)
    else:
        return astnode

def specific_occurrences_update(so, so2):
    for key, val2 in so2.items():
        val2 = val2.primary_specific_occurrence()
        if key in so:
            val1 = so[key].primary_specific_occurrence()
            so[key] = val1

            if val1.criterion_summary() != val2.criterion_summary():
                raise NotImplementedError('specific occurrence %s with inconsistent criteria %s, %s'
                                          % (key, val1.criterion_summary(), val2.criterion_summary()))

            if val1 is not val2:
                val2.specific_occurrence_parent = val1

        else:
            so[key] = val2

def sql_tref_combiner(crit):
    if hasattr(crit, 'sql_tref_combiner'):
        return crit.sql_tref_combiner()
    else:
        return ''

def prettyindent(parts, pre1='', preI='', sufI='', sufN=''):
    """pretty printing

       prettyindent(['expr1', 'expr2', 'expr3'], '(', 'AND ', '', ')')

       prettyindent([prettyindent(['expr1', 'expr2'], '(', 'AND ', '', ')'), prettyindent(['expr1', 'expr2'], '(', 'OR ', '', ')')], '(SELECT ', '', ',', '')

    """
    block_indent = len(pre1) 
    item_indent = len(preI)
    total_indent = block_indent + item_indent
    sep_indent = total_indent - len(preI)
    
    def adjustlines(part):
        lines = part.split('\n')
        line0 = lines[0]
        lines = [ ''.ljust(total_indent) + line for line in lines[1:] ]
        if lines:
            return line0 + '\n' + '\n'.join(lines)
        else:
            return line0
    
    return pre1 + ''.ljust(item_indent) + (sufI + '\n' + ''.ljust(sep_indent) + preI).join(map(adjustlines, parts)) + sufN

def criterion_is_constant(crit):
    return type(crit) in [ ConstantStr, ConstantUnicode ]

def criterion_mapping(crit):
    """Returns mapping tuple (table, patient, start, end, code, status, value, fields)

       table:    is a table reference for the episode

       patient:  expression for episode patient ID

       start:    expression for episode start date/time

       end:      expression for episode end date/time or NULL

       code:     expression for episode coding or NULL, e.g. 
                 diagnosis code

       status:   expression for episode status or NULL, i.e. 
                 HQMF status string

       negation: expression for episode negation, i.e. negation 
                 rationale or NULL for non-negated/non-negatable

       value:    expression for episode value or NULL, e.g. lab 
                 numeric value

    """
    qds_data_type = crit.data['qds_data_type'].flatten()

    return (qds_data_type, 
            'patient_id', 
            'start_dt', 
            'end_dt', 
            'code', 
            'status', 
            'negation', 
            'value')


inline_code_lists_max_limit = None
try:
    import psycopg2
    database_name = '' # this will use default database for calling user
    inline_code_lists_database_conn = psycopg2.connect(database=database_name)
except Exception, e:
    inline_code_lists_database_conn = None
    sys.stderr.write('%s\n' %str(e))

def coded_values_by_id(code_list_id):
    """Returns a SQL code_list_expr suitable for use in a  ... value OP ANY code_list_expr ... test.

       code_list_expr will either be a parenthesized subquery or a
       parenthesized array of codes if inlining is feasible.

       inlining feasibility is decided by whether we have an inlining
       database connection and 
    """
    code_list_sql = 'SELECT code FROM eqmxlate_value_sets.code_lists WHERE code_list_id = %s' % code_list_id.sql()
    result = '(%s)' % code_list_sql  # subquery to defer expansion to runtime
    
    if inline_code_lists_database_conn:
        try:
            cur = inline_code_lists_database_conn.cursor()
            cur.execute(code_list_sql)
            codes = [ row[0] for row in cur ]
            cur.close()
            inline_code_lists_database_conn.commit()
            if inline_code_lists_max_limit is None or len(codes) <= inline_code_lists_max_limit:
                # override default subquery result
                result = '/* code_list_id: %s */ (ARRAY[%s]::integer[])' % (
                    code_list_id.sql(), 
                    ', '.join([ str(code) for code in codes ])
                    )
        except Exception, e:
            inline_code_lists_database_conn.rollback()
            sys.stderr.write('Notice: attempt to inline code_list_id = %s failed.' % code_list_id.sql())
            sys.stderr.write('%s\n' %str(e))
            
    return result


class QdmElement (ast.record):
    def specific_occurrences(self):
        return dict()

    def simplify(self, measure):
        self.measure = measure
        return self

    def simplify_subset_context(self):
        return self

    def criterion_summary(self):
        raise NotImplemented()

    def is_specific_occurrence(self):
        return False

    def is_psuedo_specific_occurrence(self):
        return False

    def sql_episodes_so_bind(self):
        raise NotImplementedError()        

    def sql_episodes_step1(self, so_bind=False):
        """
        1. Event code matches value set code (Select procedures from
           patient based on matching code as defined by the value set)

        """
        raise NotImplementedError()

    def sql_episodes_step2(self):
        """
        2. Events filtered by status (active, ordered, resolved)

        """
        raise NotImplementedError()

    def sql_episodes_step3(self):
        """
        3. Events filtered by Negation (Negation Rationale, I.e. Not done)        

        """
        raise NotImplementedError()

    def sql_episodes_step4(self):
        """
        4. Events filtered by fields (source, severity, facility location ...)

        """
        raise NotImplementedError()

    def sql_episodes_step5(self):
        """
        5. Event filtered by temporal references (i.e. Starts After Start, During ...)

        """
        raise NotImplementedError()

    def sql_episodes_step6(self):
        """
        6. Events filtered by value restriction (I.e. Ejection fraction > 40%)

        """
        raise NotImplementedError()

    def sql_episodes_step7(self):
        """
        7. Event filtered by subset operator (FIRST, SECOND, MOST RECENT)

        """
        raise NotImplementedError()

    def sql_episodes(self):
        return self.sql_episodes_step7()

    def sql_episodes_generic(self):
        return prettyindent([ 'SELECT patient_id, start_dt, end_dt, audit',
                              prettyindent([ self.sql_episodes() ],
                                           'FROM ', '', '', ' s') ],
                            '(', '', '', ')')

    def sql_boolean(self):
        return 'EXISTS\n' + self.sql_episodes(boolean=True)


class Value (QdmElement):
    prototype = [
        ('type', ConstantText),
        ('unit', (ConstantText, None)),
        ('value', ConstantText),
        ('inclusive?', ConstantBool),
        ('derived?', ConstantBool)
        ]

    defaults = {
        'unit': None
        }

    def sql_and_op(self):
        time_units = dict(a='Y', m='M', mo='M', wk='W', d='D')

        if flatten(self.data['unit']) in time_units:
            return ("'P%d%s'::interval" % (int(flatten(self.data['value'])), time_units[flatten(self.data['unit'])]), '')
        else:
            return (flatten(self.data['value']), flatten(self.data['inclusive?']) and '=' or '')

class ValueConstraint (QdmElement):
    prototype = [
        ('type', ConstantText),
        ('high', (Value, None)),
        ('low', (Value, None)),
        ('width', (Value, None)),
        ('system', (ConstantText, None)),
        ('code', (ConstantText, None)),
        ('code_list_id', (ConstantText, None))
        ]
    
    defaults = {
        'high': None,
        'low': None,
        'width': None,
        'system': None,
        'code': None,
        'code_list_id': None
        }

    def sql_where(self):
        return self.sql_compare('value')

    def sql_range_quantity(self, val):
        if self.data['low']:
            sql, op = self.data['low'].sql_and_op(); # TODO: make use of op
            return 'ROW(%s, %s, NULL::interval)::date_window /* low limit */' % (val, sql)
        elif self.data['high']:
            sql, op = self.data['high'].sql_and_op(); # TODO: make use of op
            return 'ROW(%s, NULL::interval, %s)::date_window /* high limit */' % (val, sql)
        else:
            raise NotImplementedError('range with neither high nor low')

    def sql_compare(self, val):
        vtype = flatten(self.data['type'])
        if vtype == 'ANYNonNull':
            return '%s IS NOT NULL' % val
        elif vtype == 'CD':
            if self.data['code']:
                assert not self.data['code_list_id']
                return '%s = %s' % (val, sql_literal(flatten(self.data['code'])))
            else:
                assert self.data['code_list_id']
                return '%s = ANY %s' % (val, coded_values_by_id(self.data['code_list_id']))
        elif self.data['low']:
            value, comp_suffix = self.data['low'].sql_and_op()
            return '%s >%s %s /* low limit */' % (val, comp_suffix, value)
        elif self.data['high']:
            value, comp_suffix = self.data['high'].sql_and_op()
            return '%s <%s %s /* high limit */' % (val, comp_suffix, value)
        else:
            raise NotImplementedError(json.dumps(self.flatten(), indent=2))

class SubsetOperator (QdmElement):
    prototype = [
        ('type', ConstantText),
        ('value', (ValueConstraint, None))
        ]
    
    defaults = {
        'value': None
        }

    def sql_boolean(self, source_sql):
        oper = flatten(self.data['type'])
        if oper == 'COUNT':
            if self.data['value']:
                select = self.data['value'].sql_compare('count(*)')
            else:
                select = 'count(*)'
            sql = prettyindent([ 'SELECT %s' % select,
                                 prettyindent([ source_sql ], 'FROM (', '', '', ') s') ],
                               '(', '', '', '\n)')
            return sql
        elif self.is_specific_occurrence():
            return 'specific occurrence reference\n' + 'IN\n' + self.sql_episodes(source_sql)
        else:
            return 'EXISTS\n' + self.sql_episodes(source_sql)

    def sql_episodes(self, source_sql, so_bind=False):
        oper = flatten(self.data['type'])
        if oper == 'FIRST':
            return prettyindent([ '/* FIRST episode */',
                                  'SELECT *',
                                  prettyindent([ source_sql ], 'FROM (', '', '', ') s'),
                                  'ORDER BY start_dt',
                                  'LIMIT 1' ],
                                '(', '', '', '\n)')
        elif oper == 'RECENT':
            return prettyindent([ '/* RECENT episode */',
                                  'SELECT *',
                                  prettyindent([ source_sql ], 'FROM (', '', '', ') s'),
                                  'ORDER BY end_dt DESC, start_dt DESC',
                                  'LIMIT 1' ],
                                '(', '', '', '\n)')
        elif so_bind:
            return source_sql
        else:
            return prettyindent([ '/* FIXME subset in episodic context */',
                                  source_sql ],
                                '', '', '', '')
        #raise NotImplementedError('subset operator %s in episodic context' % oper)

class TemporalReference (QdmElement):
    def specific_occurrences(self):
        if hasattr(self.data['reference'], 'specific_occurrences'):
            return self.data['reference'].specific_occurrences()
        else:
            return dict()

    def simplify(self, measure):
        QdmElement.simplify(self, measure)
        self.data['reference'] = simplify(measure.get_data_criterion(self.data['reference']), measure)
        return self

    def sql_tref_wheres(self, context_criteria):
        preposition = flatten(self.data['type'])
        combiner = sql_tref_combiner(self.data['reference'])

        table, ctx_patient, ctx_start_dt, ctx_end_dt, ctx_code, ctx_status, ctx_neg, ctx_value \
            = criterion_mapping(context_criteria)

        def temporal_reference(crit):
            if criterion_is_constant(crit):
                ref_patient, ref_start_dt, ref_end_dt, ref_code, ref_status, ctx_neg, ctx_value = (
                    None, flatten(crit) + '.start_dt', flatten(crit) + '.end_dt', None, None, None, None
                    )
                ref_sql = None
            else:
                table, ref_patient, ref_start_dt, ref_end_dt, ref_code, ref_status, ref_neg, ref_value \
                    = criterion_mapping(crit)
                ref_sql = crit.sql_episodes()
            
            if preposition in ['CONCURRENT', 'DURING', 'SDU', 'EDU' ]:
                lhs = {
                    'DURING': 'daterange(start_dt, end_dt)',
                    'CONCURRENT': 'daterange(start_dt, end_dt)',
                    'SDU': 'start_dt',
                    'EDU': 'end_dt'
                    }[preposition]

                op = {
                    'CONCURRENT': '&&'
                    }.get(preposition, '<@')

                if ref_sql:
                    rhs = prettyindent( [ 'SELECT daterange(start_dt, end_dt)',
                                          prettyindent([ ref_sql ],
                                                       'FROM ', '', '', ' s') ],
                                        '(', '', '', '\n)')
                    return ('(select %s)\n%s /* %s */ ANY\n%s' % (lhs, op, preposition, rhs))
                else:
                    return ('%s\n<@ /* %s */\ndaterange(%s,%s)' % (lhs, preposition, ref_start_dt, ref_end_dt))
            elif preposition in [ 'SBS', 'SAS', 'SBE', 'SAE', 'EBS', 'EAS', 'EBE', 'EAE' ]:
                lhs = {'S': 'start_dt', 'E': 'end_dt'}[preposition[0]]
                if self.data['range']:
                    lhs = self.data['range'].sql_range_quantity(lhs)
                else:
                    lhs = 'row(%s, null::interval, null::interval)::date_window' % lhs
                oper = {'B': '|~<', 'A': '|~>'}[preposition[1]]

                if ref_sql:
                    rhs = prettyindent([ 'SELECT %s' % {'S': 'start_dt', 'E': 'end_dt'}[preposition[2]],
                                         prettyindent([ ref_sql ],
                                                      'FROM ', '', '', ' s')],
                                       '(', '', '', '\n)')
                    return ('(select %s)\n%s /* %s */ ANY\n%s' % (lhs, oper, preposition, rhs))
                else:
                    return ('%s\n%s /* %s */\n%s' % (lhs, oper, preposition, {'S': ref_start_dt, 'E': ref_end_dt}[preposition[2]]))
            else:
                raise NotImplementedError('temporal reference relationship %s' % preposition)

        reference = self.data['reference']
        if hasattr(reference, 'data'):
            if 'children_criteria' in reference.data:
                if reference.data['subset_operators']:
                    wheres = [ temporal_reference(reference) ]
                else:
                    wheres = [ temporal_reference(crit) for crit in reference.data['children_criteria'] ]
            else:
                wheres = [ temporal_reference(reference) ]
        else:
            wheres = [ temporal_reference(reference) ]

        return combiner, wheres

class CriterionBasic (QdmElement):
    def __init__(self, tree, context=''):
        QdmElement.__init__(self, tree, context)
        assert len(self.data['subset_operators']) <= 1
        self.psuedo_specific_occurrence = False
        self.specific_occurrence_num = None
        self.specific_occurrence_parent = None

    def qds_data_type(self):
        if self.data['qds_data_type']:
            return self.data['qds_data_type'].flatten()
        else:
            return None

    def primary_specific_occurrence(self):
        if self.specific_occurrence_parent is None:
            return self
        else:
            return self.specific_occurrence_parent.primary_specific_occurrence()

    def is_specific_occurrence(self):
        if self.data['specific_occurrence'] and self.data['specific_occurrence_const']:
            return True
        else:
            return False
    
    def is_psuedo_specific_occurrence(self):
        return self.is_specific_occurrence() and self.psuedo_specific_occurrence

    def shortened_identifier(self):
        ident = self.data['specific_occurrence_const'].flatten()
        m = re.match('.*_precondition_(?P<pc>[0-9]+)', ident)
        if m:
            return ('pc%s' % m.group('pc')), ident
        elif self.specific_occurrence_parent is not None:
            return self.primary_specific_occurrence().shortened_identifier()
        elif self.specific_occurrence_num is not None:
            return ('so%d' % self.specific_occurrence_num), ident
        else:
            return ident, ident

    def specific_occurrences(self):
        so = dict()
        
        if self.is_specific_occurrence():
            i_short, i_long = self.shortened_identifier()

            key = '%s/%s' % (i_short, 
                             self.data['specific_occurrence'].flatten())
            
            so[key] = self

        for item in self.data['temporal_references']:
            specific_occurrences_update(so, item.specific_occurrences())

        return so

    def simplify(self, measure):
        QdmElement.simplify(self, measure)
        self.data['temporal_references'] = [ 
            simplify(ref, measure)
            for ref in self.data['temporal_references']
            ]
        if self.data['subset_operators']:
            return self.simplify_subset_context()
        else:
            return self

    def sql_episodes_so_bind(self):
        if self.is_psuedo_specific_occurrence():
            # wrap all patient's rows into an array to make avaialble for audit 
            # while not increasing the combinatoric space of possible patient solutions

            sql = self.sql_episodes_step7(so_bind=True)

            sql = prettyindent([prettyindent(['array_agg( ROW( (s).* )::%s ) AS rowset' % sql_identifier(self.qds_data_type())
                                              ],
                                             'SELECT ', '', ',', ''),
                                prettyindent([sql], 'FROM ', '', '', ' s')],
                               '(', '', '', '\n)')

            return sql
        elif self.is_specific_occurrence():
            return self.sql_episodes_step1(so_bind=True)
        else:
            raise NotImplementedError()

    def sql_episodes_step5(self):
        """
        5. Event filtered by temporal references (i.e. Starts After Start, During ...)

        """
        step4 = self.sql_episodes_step4()
        if not self.data['temporal_references']:
            return step4
        elif len(self.data['temporal_references']) == 1:

            selects = 'SELECT *'

            froms = prettyindent([self.sql_episodes_step4()],
                                 'FROM ', '', '', ' s')

            combiner, temporal_wheres = self.data['temporal_references'][0].sql_tref_wheres(self)

            if len(temporal_wheres) > 1:
                comment = ('/* filter by temporal_references w/ derivation_operator %s */' 
                           % flatten(self.data['temporal_references'][0].data['reference'].data['derivation_operator']))
            else:
                comment = '/* filter by temporal_references */'

            where = prettyindent(temporal_wheres,
                                 'WHERE ', '%s ' % combiner)

            return prettyindent([comment, selects, froms, where], '(', '', '', '\n)')
    
        else:
            raise NotImplementedError('more than one temporal reference')

    def sql_episodes_step7(self, boolean=False, sql=None, so_bind=False):
        """
        7. Event filtered by subset operator (FIRST, SECOND, MOST RECENT)

        """
        if sql == None:
            sql = self.sql_episodes_step6()

        if self.data['subset_operators']:
            assert len(self.data['subset_operators']) == 1
            oper = self.data['subset_operators'][0]
            if boolean:
                sql = oper.sql_boolean(sql)
            else:
                sql = oper.sql_episodes(sql)

        return sql

def frozendict(d):
    items = d.items()
    items.sort()
    return tuple(items)

def thaweddict(fd):
    d = dict()
    for key, vals in fd:
        d[key] = vals
    return d

class CriterionCodeList (CriterionBasic):

    def criterion_summary(self):
        summ = dict( [ (k, flatten(self.data[k]))
                       for k in [ 'standard_category', 'qds_data_type', 'type',
                                  'definition', 'status', 'negation', 
                                  'source_data_criteria',
                                  'code_list_id',
                                  'specific_occurrence', 'specific_occurrence_const' ] ] )
        codes = dict([ (flatten(codespace), [ flatten(code) for code in codes ])
                       for codespace, codes in self.data.get('inline_code_list').items() ])
        summ['inline_code_list'] = frozendict(codes)
        return frozendict(summ)

    def sql_episodes_step1(self, so_bind=False):
        """
        1. Event code matches value set code (Select procedures from
           patient based on matching code as defined by the value set)

        """
        table, patient_id, start_dt, end_dt, code, status, negation, value \
            = criterion_mapping(self)
        valsexpr = coded_values_by_id(self.data['code_list_id'])

        selects = prettyindent(['*'],
                               'SELECT ', '', ',')

        froms = 'FROM %s' % table

        wheres = []
        if not so_bind:
            wheres.append( '%s = p.patient_id' % patient_id )

        if valsexpr:
            wheres.append('%s = ANY %s' % (code, valsexpr))
                   
        where = prettyindent(wheres, 'WHERE ', 'AND ')

        sql = prettyindent(['/* %s */' % flatten(self.data['source_data_criteria']), selects, froms, where], '(', '', '', '\n)')

        return sql
            
    def sql_episodes_step2(self):
        """
        2. Events filtered by status (active, ordered, resolved)

        """
        status = flatten(self.data['status'])
        if status:
            return prettyindent(['/* filter by status */',
                                 'SELECT *',
                                 prettyindent([self.sql_episodes_step1()], 'FROM ', '', '', ' s'),
                                 'WHERE status = %s' % sql_literal(status)],
                                '(', '', '', '\n)')
        else:
            return self.sql_episodes_step1()

    def sql_episodes_step3(self):
        """
        3. Events filtered by Negation (Negation Rationale, I.e. Not done)        

        """
        if flatten(self.data['negation']):
            comment = '/* filter by negation */'
            if self.data['negation_code_list_id']:
                valexpr = coded_values_by_id(self.data['negation_code_list_id'])
                test = 'negation = ANY %s' % valexpr
            else:
                test = 'negation IS NOT NULL'
        else:
            comment = '/* filter by non-negation */'
            test = 'negation IS NULL'
            
        return prettyindent([comment,
                             'SELECT *',
                             prettyindent([self.sql_episodes_step1()], 'FROM ', '', '', ' s'),
                             'WHERE %s' % test],
                            '(', '', '', '\n)')

    def sql_episodes_step4(self):
        """
        4. Events filtered by fields (source, severity, facility location ...)

        """
        if self.data['field_values']:

            wheres = []

            for field, constraint in self.data['field_values'].items():
                wheres.append( ( constraint.sql_compare(field) ) )

            if len(wheres) > 1:
                wheres = [ prettyindent(wheres, '', 'AND ') ]

            return prettyindent(['/* filter by fields */',
                                 'SELECT *',
                                 prettyindent([self.sql_episodes_step3()], 'FROM ', '', '', ' s'),
                                 prettyindent(wheres, 'WHERE ')],
                                '(', '', '', '\n)')

        else:
            return self.sql_episodes_step3()

    def sql_episodes_step6(self):
        """
        6. Events filtered by value restriction (I.e. Ejection fraction > 40%)

        """
        if self.data['value']:
            try:
                return prettyindent(['/* filter by value */',
                                     'SELECT *',
                                     prettyindent([self.sql_episodes_step1()], 'FROM ', '', '', ' s'),
                                     'WHERE %s' % self.data['value'].sql_where()],
                                    '(', '', '', '\n)')
            except NotImplementedError, exc:
                raise NotImplementedError(json.dumps(self.flatten(), indent=2))
        else:
            return self.sql_episodes_step5()

    def sql_episodes(self, boolean=False):
        if self.is_specific_occurrence() and not self.is_psuedo_specific_occurrence():
            i_short, i_long = self.shortened_identifier()
            so_ref = sql_identifier('%s/%s' % (
                    i_short,
                    flatten(self.data['specific_occurrence'])
                    ))
            if self.is_psuedo_specific_occurrence():
                so_value = 'SELECT (unnest((%s).rowset)).*' % so_ref
                psuedo = 'psuedo-'
            else:
                so_value = 'SELECT (%s).*' % so_ref
                psuedo = ''

            return prettyindent([ '/* constrained %sspecific-occurrence binding %s */' % (psuedo, i_long),
                                  so_value,
                                  'INTERSECT',
                                  'SELECT *',
                                  prettyindent([self.sql_episodes_step7()],
                                               'FROM ', '', '', ' s')],
                                '(', '', '', '\n)')
        else:
            return self.sql_episodes_step7(boolean=boolean)


class CriterionDerivation (CriterionBasic):
    def __init__(self, tree, context=''):
        CriterionBasic.__init__(self, tree, context)

        # enforce additional invariants assumed by our code on this subtype
        for key, value in {
            'type': 'derived',
            'definition': 'derived'
            }.items():
            if flatten(self.data[key]) != value:
                raise NotImplementedError('"%s"="%s" in derived criterion' 
                                          % (key, self.data[key]))
        for key in ['description',
                    'standard_category',
                    'qds_data_type',
                    'property',
                    'status',
                    'hard_status',
                    'specific_occurrence',
                    'specific_occurrence_const',
                    'value',
                    'temporal_references',
                    'field_values']:
            if flatten(self.data[key]):
                raise NotImplementedError('non-empty field "%s"="%s" in derived criterion' 
                                          % (key, flatten(self.data[key])))

    def qds_data_type(self):
        return 'qds_generic_event'

    def criterion_summary(self):
        summ = dict( [ (k, flatten(self.data[k]))
                       for k in [ 'standard_category', 'qds_data_type', 'type',
                                  'definition', 'status', 'negation', 
                                  'source_data_criteria',
                                  'specific_occurrence', 'specific_occurrence_const' ] ] )
        return frozendict(summ)

    def simplify_subset_context(self):
        if flatten(self.data['derivation_operator']) == 'XPRODUCT':
            # XPRODUCT is same as UNION as a subset operand
            self.data['derivation_operator'] = ConstantStr('UNION')
        return self

    def specific_occurrences(self):
        so = CriterionBasic.specific_occurrences(self)
        
        for item in self.data['children_criteria']:
            specific_occurrences_update(so, item.specific_occurrences())

        return so

    def derivation_same(self, deriv):
        if self.data['negation'] or deriv.data['negation']:
            return False
        else:
            return self.data['derivation_operator'].flatten() == deriv.data['derivation_operator'].flatten() 

    def simplify(self, measure):
        self = CriterionBasic.simplify(self, measure)

        if len(self.data['children_criteria']) == 0:
            raise NotImplementedError('zero-length children-criteria %s' % flatten(self))
        elif len(self.data['children_criteria']) == 1 and not self.data['negation']:
            return simplify(measure.get_data_criterion(self.data['children_criteria'][0]), measure)
        else:
            children = self.data['children_criteria']
            children_prime = []
            for child in children:
                child = simplify(measure.get_data_criterion(child), measure)
                if hasattr(child, 'derivation_same') and child.derivation_same(self):
                    children_prime.extend(child.data['children_criteria'])
                else:
                    children_prime.append(child)
            self.data['children_criteria'] = children_prime
            return self
        
    def sql_tref_combiner(self):
        return {
            'UNION': 'OR',
            'XPRODUCT': 'AND'
            }[ flatten(self.data['derivation_operator']) ]
            
    def sql_episodes_step7(self, boolean=False, so_bind=False):
        """
        7. Event filtered by subset operator (FIRST, SECOND, MOST RECENT)

        """
        # need to apply derivation operator first before doing usual subset
        if flatten(self.data['derivation_operator']) == 'UNION':
            sql = prettyindent([ child.sql_episodes_generic() for child in self.data['children_criteria'] ],
                               '(', 'UNION ', '', '\n)')
        else:
            raise NotImplementedError('derivation operator "%s"' % flatten(self.data['derivation_operator']))

        return CriterionBasic.sql_episodes_step7(self, boolean, sql, so_bind=so_bind)

    def sql_boolean(self):
        if self.data['subset_operators']:
            return self.sql_episodes_step7(boolean=True)
        else:
            return QdmElement.sql_boolean()

class PreconditionReference (QdmElement):
    prototype = [
        ('id', ConstantInt),
        ('reference', ConstantText)
        ]

    def simplify(self, measure):
        return simplify(measure.get_data_criterion(self.data['reference']), measure)
        

class Conjunction (QdmElement):

    def specific_occurrences(self):
        so = dict()
        for item in self.data['preconditions']:
            specific_occurrences_update(so, item.specific_occurrences())
        return so

    def conjunction_same(self, conj):
        if self.data['negation'] or conj.data['negation']:
            return False
        else:
            return self.data['conjunction_code'].flatten() == conj.data['conjunction_code'].flatten() 

    def simplify(self, measure):
        assert flatten(self.data['conjunction?'])
        assert flatten(self.data['conjunction_code']) in ['allTrue', 'atLeastOneTrue']

        if len(self.data['preconditions']) == 0:
            return None
        elif len(self.data['preconditions']) == 1 and not self.data['negation']:
            return simplify(self.data['preconditions'][0], measure)
        else:
            items = self.data['preconditions']
            items_prime = []
            for item in items:
                item = simplify(item, measure)
                if item == None:
                    continue
                if hasattr(item, 'conjunction_same') and item.conjunction_same(self):
                    items_prime.extend(item.data['preconditions'])
                else:
                    items_prime.append(item)
            self.data['preconditions'] = items_prime
            return self

    def sql_boolean(self):
        combiner = dict(alltrue='AND', atleastonetrue='OR')[flatten(self.data['conjunction_code']).lower()]
        sql = prettyindent([ item.sql_boolean() for item in self.data['preconditions'] ],
                           '(', '%s ' % combiner, '', '\n)')
        if flatten(self.data['negation']):
            sql = prettyindent([ sql ], 'NOT (', '', '', '\n)')
        return sql

Criterion = (CriterionDerivation, CriterionCodeList, ConstantText)

# these are flattened into one alternatives tuple to make friendlier error messages
QueryOrNone = tuple(list(Criterion) + [Conjunction] + [None])
Query = tuple(list(Criterion) + [Conjunction])

TemporalReference.prototype = [
    ('type', ConstantText),
    ('reference', (Criterion, TemporalReference, ConstantText)),
    ('range', (ValueConstraint, None)),
    ('title', (ConstantText, None))
    ]

TemporalReference.defaults = {
    'range': None,
    'title': None
    }

CriterionBasic.prototype = [
    ('title', ConstantText),
    ('description', ConstantText),
    ('standard_category', ConstantText),
    ('qds_data_type', ConstantText),
    ('property', (ConstantText, None)),
    ('type', ConstantText),
    ('definition', ConstantText),
    ('status', (ConstantText, None)),
    ('hard_status', ConstantBool),
    ('negation', ConstantBool),
    ('negation_code_list_id', (ConstantText, None)),
    ('specific_occurrence', (ConstantText, None)),
    ('specific_occurrence_const', (ConstantText, None)),
    ('source_data_criteria', ConstantText),
    ('value', (ValueConstraint, None)),
    ('temporal_references', [ TemporalReference ]),
    ('subset_operators', [ SubsetOperator ]),
    ('field_values', { 'field': ValueConstraint })
    ]

CriterionBasic.defaults = {
    'property': None,
    'status': None,
    'specific_occurrence': None,
    'specific_occurrence_const': None,
    'temporal_references': [],
    'value': None,
    'subset_operators': [],
    'field_values': {},
    'negation_code_list_id': None
    }

CriterionCodeList.prototype = CriterionBasic.prototype + [
    ('code_list_id', ConstantText),
    ('inline_code_list', { 'codespace': [ ConstantText ] })
    ]

CriterionCodeList.defaults = dictmerge([CriterionBasic.defaults,
                                        { 
            'inline_code_list': {} 
            } ])

CriterionDerivation.prototype = CriterionBasic.prototype + [
    ('derivation_operator', (ConstantText, None)),
    ('children_criteria', [ Criterion ])
    ]

Conjunction.prototype = [
    ('conjunction?', ConstantBool),
    ('preconditions', [ (PreconditionReference, Conjunction, Criterion) ]),
    ('conjunction_code', ConstantText),
    ('negation', (ConstantBool, None)),
    ('id', (ConstantInt, None))
    ]

Conjunction.defaults = {
    'id': None,
    'negation': False,
    'conjunction?': True,
    'preconditions': []
    }

class PopulationRule (Conjunction):
    prototype = Conjunction.prototype + [
        ('type', ConstantText),
        ('title', ConstantText),
        ('hqmf_id', ConstantText)
        ]

    defaults = dictmerge([Conjunction.defaults,
                          {
                'conjunction_code': 'allTrue',
                'preconditions': []
                } ])

class Population (QdmElement):
    prototype = [
        ('IPP', ConstantText),
        ('DENOM', ConstantText),
        ('NUMER', ConstantText),
        ('DENEX', (ConstantText, None)),
        ('title', (ConstantText, None)),
        ('id', (ConstantText, None))
        ]

    defaults = {
        'title': None,
        'id': None,
        'DENEX': None
        }

    def simplify(self, measure):
        QdmElement.simplify(self, measure)

        def simplify_population(key, inclusions, exclusions):
            inclusions = [ flatten(measure.get_pop_criterion(ckey))
                           for ckey in inclusions ]

            exclusions = [ flatten(measure.get_pop_criterion(ckey))
                           for ckey in exclusions ]

            orig = measure.get_pop_criterion(key)

            rewritten = {
                'type': flatten(orig.data['type']),
                'title': flatten(orig.data['title']),
                'hqmf_id': flatten(orig.data['hqmf_id']),
                'conjunction?': True,
                'conjunction_code': 'allTrue',
                'preconditions': inclusions + (
                    exclusions and [ {
                            'conjunction_code': 'allTrue',
                            'negation': True,
                            'preconditions': exclusions
                            } ] or []
                    )
                }

            self.data[key] = simplify(PopulationRule(rewritten), measure)

        # save the population criteria names before simplifying
        IPP = flatten(self.data['IPP'])
        DENOM = flatten(self.data['DENOM'])
        NUMER = flatten(self.data['NUMER'])
        DENEX = flatten(self.data['DENEX'])

        # re-write and re-parse the population criteria 
        # instead of names, we now store actual PopulationRule instances
        self.data['NUMER'] = simplify(measure.get_pop_criterion(NUMER), measure)
        self.data['DENEX'] = simplify(measure.get_pop_criterion(DENEX), measure)
        self.data['DENOM'] = simplify(measure.get_pop_criterion(DENOM), measure)
        self.data['IPP'] = simplify(measure.get_pop_criterion(IPP), measure)

        return self

    def specific_occurrences(self):
        so = dict()
        for key in ['IPP', 'DENOM', 'NUMER', 'DENEX']:
            if self.data[key]:
                specific_occurrences_update(so, self.data[key].specific_occurrences())

        return so

    def sql_patients(self):        
        sos = self.specific_occurrences()

        pat_table, pat_pat_id = ('patients', 'patient_id')

        def sql_boolean(crit):
            if crit != None:
                return crit.sql_boolean()
            else:
                return 'True'

        return ('CREATE VIEW %s AS\n' % sql_identifier('measure_solutions_%s%s' % (flatten(self.measure.data['id']), flatten(self.data['id']) or ''))
                + prettyindent([ prettyindent([ 'p.patient_id AS patient_id',
                                                '%s AS IPP' % sql_boolean(self.data['IPP']),
                                                '%s AS DENOM' % sql_boolean(self.data['DENOM']),
                                                '%s AS DENEX' % sql_boolean(self.data['DENEX']),
                                                #('/* %s */' % json.dumps(flatten(self.data['NUMER']))) + '\n' + ''
                                                '%s AS NUMER' % sql_boolean(self.data['NUMER']) ] 
                                              + [ 'ROW(%s.*)::%s AS %s /* %s/%s */' % (sql_identifier(sokey),
                                                                                       sql_identifier(socrit.qds_data_type()),
                                                                                       sql_identifier(sokey),
                                                                                       socrit.data['specific_occurrence_const'].flatten(),
                                                                                       socrit.data['specific_occurrence'].flatten())
                                                  for sokey, socrit in sos.items()
                                                  if not socrit.is_psuedo_specific_occurrence()
                                                  ]
                                              + [ '%s AS %s /* %s/%s */' % (socrit.sql_episodes_so_bind(), 
                                                                            sql_identifier(sokey),
                                                                            socrit.data['specific_occurrence_const'].flatten(),
                                                                            socrit.data['specific_occurrence'].flatten())
                                                  for sokey, socrit in sos.items()
                                                  if socrit.is_psuedo_specific_occurrence()
                                                  ],
                                              'SELECT ', sufI=','),
                                 prettyindent([ '(SELECT %s AS patient_id FROM %s\n) AS p' % (pat_pat_id, pat_table) ] 
                                              + [ '%s AS %s /* %s/%s */\n' % (socrit.sql_episodes_so_bind(), 
                                                                              sql_identifier(sokey),
                                                                              socrit.data['specific_occurrence_const'].flatten(),
                                                                              socrit.data['specific_occurrence'].flatten())
                                                  + '  ON (p.patient_id = %s.patient_id)' % sql_identifier(sokey)
                                                  for sokey, socrit in sos.items()
                                                  if not socrit.is_psuedo_specific_occurrence()
                                                  ], 
                                              'FROM ', 'LEFT OUTER JOIN '),
                                 prettyindent([ prettyindent([ sql_boolean(self.data['IPP']),
                                                               sql_boolean(self.data['DENOM']) ],
                                                             '', 'AND ') ],
                                              'WHERE ')
                                 ],
                               '(', '', '', '\n)') + ';\n\n'

                + 'CREATE VIEW %s AS\n' % sql_identifier('measure_summary_%s%s' % (flatten(self.measure.data['id']), flatten(self.data['id']) or ''))
                + prettyindent([ 'SELECT DISTINCT ON (patient_id) *',
                                 'FROM %s' % sql_identifier('measure_solutions_%s%s' % (flatten(self.measure.data['id']), flatten(self.data['id']) or '')),
                                 'ORDER BY patient_id, (DENEX AND DENOM AND IPP) DESC, (NUMER AND DENOM AND IPP AND NOT DENEX) DESC, (DENOM AND IPP AND NOT DENEX) DESC, IPP DESC'
                                 ],
                               '(', '', '', '\n)') + ';\n\n'
                )

class Measure (QdmElement):
    def __init__(self, tree, context='', use_psuedo_specific_occurrences=True):
        QdmElement.__init__(self, tree, context)
        self.use_psuedo_specific_occurrences = use_psuedo_specific_occurrences
    prototype = [
        ('id', ConstantText),
        ('title', ConstantText),
        ('description', ConstantText),
        ('population_criteria', {
                'pop_name': PopulationRule
                }),
        ('data_criteria', { 
                'criterion_name': Criterion 
                }),
        ('source_data_criteria', { 
                'criterion_name': Criterion 
                }),
        #('attributes', [ ]),
        ('populations', [ 
                Population
                ]),
        ('measure_period', ValueConstraint)
        ]

    def simplify(self):
        self.data['populations'] = [
            simplify(pop, self)
            for pop in self.data['populations']
            ]

        so = dict()
        for pop in self.data['populations']:
            specific_occurrences_update(so, pop.specific_occurrences())

        so_items = so.items()
        so_items.sort(key=lambda i: i[0])

        for i in range(0, len(so_items)):
            key, crit = so_items[i]
            crit.specific_occurrence_num = i + 1

        self.sos = so

    def get_pop_criterion(self, key):
        if key != None:
            return self.data['population_criteria'][key]
        else:
            return None

    def get_data_criterion(self, key):
        key_flat = flatten(key)

        if type(key_flat) in [str, unicode]:

            if key_flat not in self.data['data_criteria']:
                return key

            crit = self.data['data_criteria'].get(key_flat)

            if self.use_psuedo_specific_occurrences and type(crit) in [ CriterionCodeList, CriterionDerivation ] and not crit.is_specific_occurrence():
                crit = ast.parse_alternatives(Criterion, crit.flatten(), key_flat)

                pso = 'audit'
                crit.data['specific_occurrence'] = ConstantStr(pso)
                crit.data['specific_occurrence_const'] = ConstantUnicode(key_flat)
                crit.psuedo_specific_occurrence = True

                return crit
            else:
                return crit
        else:
            return key

    def sql_patients(self):
        sql = []
        for pop in self.data['populations']:
            self.psuedo_specific_occurrence_next = {}
            sql.append( pop.sql_patients() )
        return ''.join([ pop.sql_patients() for pop in self.data['populations'] ])


