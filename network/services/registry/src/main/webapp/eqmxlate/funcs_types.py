
"""
A manifest of available functions and types to be used in kubla models and queries

"""

import ast

def string_wrap(s, escape='\\', protect=[]):
    s = s.replace(escape, escape + escape)
    for c in set(protect):
        s = s.replace(c, escape + c)
    return s

def sql_identifier(s):
    # double " to protect from SQL
    # double % to protect from web.db
    return '"%s"' % string_wrap(string_wrap(s, '%'), '"') 

def sql_literal(v):
    if v != None:
        # double ' to protect from SQL
        # double % to protect from web.db
        s = '%s' % v
        return "'%s'" % string_wrap(string_wrap(s, '%'), "'")
    else:
        return 'NULL'

class ScalarType (ast.record):
    def __str__(self):
        return str(self.data['type'])

    def pass2(self, model, nsname, relname):
        pass

    def sql(self):
        return self.data['type'].flatten()

    def outtype(self):
        return self

class RowType (ast.record):
    def __str__(self):
        return '[' + ', '.join([ str(self.data['fieldtypes'][tname.flatten()])
                                 for tname in self.data['fieldnames'] ]) + ']'

    def pass2(self, model, nsname, relname):
        pass

class constant (ast.constant):
    sqltype = None

    def colrefs(self):
        if False:
            yield

    def outtype(self):
        return ScalarType({ 'type': self.sqltype })

    def pass2(self, model, nsname, relname):
        pass

class ConstantInt (constant):
    prototype = int
    sqltype = 'int'

    def sql(self, rename=False, tablepaths=[], denorm=False):
        return '%d' % self.flatten()

class ConstantBool (constant):
    prototype = bool
    sqltype = 'bool'

    def sql(self, rename=False, tablepaths=[], denorm=False):
        return '%s' % self.flatten()

class ConstantStr (constant):
    prototype = str
    sqltype = 'text'

    def sql(self, rename=False, tablepaths=[], denorm=False):
        return "'%s'" % self.flatten()

class ConstantUnicode (constant):
    prototype = unicode
    sqltype = 'text'

    def sql(self, rename=False, tablepaths=[], denorm=False):
        return "'%s'" % self.flatten()

ConstantText = (ConstantStr, ConstantUnicode)

Constant = (ConstantInt, ConstantBool, ConstantStr, ConstantUnicode)

RowType.prototype = [
    ('fieldnames', [ ConstantText ]),
    ('fieldtypes', { 'field name': (ScalarType, RowType) })
    ]

ScalarType.prototype = [
    ('type', ConstantText)
    ]
    

def is_type_compatible(context_type, expression_type):
    if type(context_type) == ScalarType and context_type.data['type'].flatten() == 'ANY':
        return True
    elif type(context_type) != type(expression_type):
        return False
    elif type(context_type) == ScalarType:
        return context_type.data['type'].flatten() == expression_type.data['type'].flatten()

    if len(context_type.data['fieldnames']) != len(expression_type.data['fieldnames']):
        return False
    
    for i in range(0, len(context_type.data['fieldnames'])):
        if not is_type_compatible(context_type.data['fieldtypes'][context_type.data['fieldnames'][i].flatten()],
                                  expression_type.data['fieldtypes'][expression_type.data['fieldnames'][i].flatten()]):
            return False

    return True

class FunctionSignature (ast.record):
    prototype = [
        ('intypes', [ (RowType, ScalarType) ]),
        ('invartype', (RowType, ScalarType, None)),
        ('outtype', (RowType, ScalarType, None)),
        ('passthrough', ConstantBool)
        ]
    defaults = {
        'invartype': None,
        'outtype': None,
        'passthrough': False
        }

    def outtype(self, intypes):
        if len(intypes) < len(self.data['intypes']):
            #print 'too few args'
            raise TypeError('got %d inputs (%s) when %d inputs (%s) are required'
                            % (len(intypes), intypes, len(self.data['intypes']), self.data['intypes']))
        for i in range(0, len(self.data['intypes'])):
            if not is_type_compatible(self.data['intypes'][i],
                                      intypes[i]):
                #print 'arg %d incompatible' % i
                raise TypeError('argument %d type (%s) not compatible with expected type (%s)'
                                % (i+1, intypes[i], self.data['intypes'][i]))
        if len(intypes) > len(self.data['intypes']):
            if self.data['invartype'] == None:
                #print 'too many args'
                raise TypeError('got %d inputs (%s) when at most %d inputs (%s) are supported'
                                % (len(intypes), intypes, len(self.data['intypes']), self.data['intypes']))
            for i in range(len(self.data['intypes']), len(intypes)):
                #print 'arg %d incompatible' % i
                if not is_type_compatible(self.data['invartype'],
                                          intypes[i]):
                    raise TypeError('argument %d type (%s) not compatible with expected type (%s)'
                                    % (i+1, intypes[i], self.data['invartype']))

        if self.data['passthrough'].flatten():
            return intypes[0]
        else:
            return self.data['outtype']

class Function (ast.record):
    prototype = [
        ('signatures', [FunctionSignature]),
        ('aggregate', ConstantBool),
        ('window', ConstantBool),
        ('infixop', (ConstantText, None)),
        ('infixopset', ConstantBool),
        ('prefixop', (ConstantText, None)),
        ('postfixop', (ConstantText, None)),
        ('sqlfunc', (ConstantText, None))
        ]
    defaults = {
        'aggregate': False,
        'window': False,
        'infixop': None,
        'infixopset': False,
        'prefixop': None,
        'postfixop': None,
        'sqlfunc': None
        }

    def pass2(self, nsname, fname):
        self.nsname = nsname
        self.fname = fname

    def outtype(self, intypes):
        for signature in self.data['signatures']:
            try:
                return signature.outtype(intypes)
            except:
                pass
        raise TypeError('function %s/%s does not support input types %s' % (self.nsname, self.fname, intypes))

    sqlfunc_template = '%s(%s)'

    def sql(self, args):
        if self.data['prefixop']:
            assert len(args) == 1
            return '%s (%s)' % (self.data['prefixop'].flatten(), args[0])
        elif self.data['postfixop']:
            assert len(args) == 1
            return '(%s) %s' % (args[0], self.data['postfixop'].flatten())
        elif self.data['infixop']:
            if self.data['infixopset'].flatten():
                assert len(args) >= 2
                return '(%s) %s (%s)' % (args[0], self.data['infixop'].flatten(), ', '.join(args[1:]))
            else:
                return (' %s ' % self.data['infixop'].flatten()).join([ '(%s)' % arg for arg in args ])
        elif self.data['sqlfunc']:
            return self.sqlfunc_template % (self.data['sqlfunc'].flatten(), ', '.join(args))
        else:
            return '"%s"."%s"(%s)' % (self.nsname, self.fname, ', '.join(args))

class FunctionMap (ast.record):
    prototype = [
        ('functions', { 'function name': Function })
        ]

class FunctionNamespaces (ast.record):
    prototype = [
        ('namespaces', { 'namespace name': FunctionMap })
        ]

    def __init__(self, tree, context=''):
        ast.record.__init__(self, tree, context)
        self.pass2()

    def pass2(self):
        for nsname, ns in self.data['namespaces'].iteritems():
            for fname, func in ns.data['functions'].iteritems():
                func.pass2(nsname, fname)

    def function(self, nsname, fname):
        if nsname not in self.data['namespaces']:
            raise ValueError('unknown function namespace "%s"' % nsname)
        ns = self.data['namespaces'][nsname]
        if fname not in ns.data['functions']:
            raise ValueError('unknown function "%s/%s"' % (nsname, fname))
        return ns.data['functions'][fname]

def bool_func_prototype(op):
    return {
        'signatures': [
            {
                'intypes': [
                    { 'type': 'bool' }, 
                    { 'type': 'bool' }
                    ],
                'invartype': { 'type': 'bool' },
                'outtype': { 'type': 'bool' }
                }
            ],
        'infixop': op
        }

def nullpred_func_prototype(op):
    return {
        'signatures': [
            {
                'intypes': [ { 'type': 'ANY' } ],
                'outtype': { 'type': 'bool' }
                }
            ],
        'postfixop': op
        }

def compare_func_prototype(op):
    return {
        'signatures': [
            {
                'intypes': [
                    { 'type': intype }, 
                    { 'type': intype }
                    ],
                'outtype': { 'type': 'bool' }
                }
            for intype in [ 'int', 'date', 'text' ]
            ] + [
            {
                'intypes': [
                    { 'type': 'date' }, 
                    { 'type': 'text' }
                    ],
                'outtype': { 'type': 'bool' }
                }
            ],
        'infixop': op
        }

def bound_func_prototype(func):
    return {
        'signatures': [
            {
                'intypes': [
                    { 'type': intype }
                    ],
                'passthrough': True
                }
            for intype in [ 'int', 'date', 'text' ]
            ],
        'aggregate': True,
        'sqlfunc': func
        }

def count_func_prototype(func):
    return {
        'signatures': [
            {
                'intypes': [
                    { 'type': intype }
                    ],
                'outtype': { 'type': 'int' }
                }
            for intype in [ 'int', 'date', 'text', 'bool' ]
            ],
        'aggregate': True,
        'sqlfunc': func
        }

functions_manifest = FunctionNamespaces({
        'namespaces': {
            'builtin': {
                'functions': {
                    'row': {
                        'signatures': [
                            {
                                'intypes': [],
                                'invartype': { 'type': 'ANY' }
                                }
                            ],
                        'sqlfunc': 'ROW'
                        },
                    'and': bool_func_prototype('AND'),
                    'or': bool_func_prototype('OR'),
                    'not': {
                        'signatures': [
                            {
                                'intypes': [ { 'type': 'bool' } ],
                                'outtype': { 'type': 'bool' }
                                }
                            ],
                        'prefixop': 'NOT'
                        },
                    'null': nullpred_func_prototype('IS NULL'),
                    'nonnull': nullpred_func_prototype('IS NOT NULL'),
                    'lesseq': compare_func_prototype('<='),
                    'greatereq': compare_func_prototype('>='),
                    'less': compare_func_prototype('<'),
                    'greater': compare_func_prototype('>'),
                    'min': bound_func_prototype('min'),
                    'max': bound_func_prototype('max'),
                    'in': {
                        'signatures': [
                            {
                                'intypes': [
                                    { 'type': intype }
                                    ],
                                'invartype': { 'type': intype },
                                'outtype': { 'type': 'bool' }
                                }
                            for intype in [ 'int', 'date', 'text' ]
                            ],
                        'infixop': 'IN',
                        'infixopset': True
                        },
                    'add': {
                        'signatures': [
                            {
                                'intypes': [
                                    { 'type': intype1 }, 
                                    { 'type': intype2 }
                                    ],
                                'outtype': { 'type': outtype }
                                }
                            for intype1, intype2, outtype in [
                                ('int', 'int', 'int'),
                                ('date', 'int', 'date'),
                                ]
                            ],
                        'infixop': '+'
                        },
                    'subtract': {
                        'signatures': [
                            {
                                'intypes': [
                                    { 'type': intype1 }, 
                                    { 'type': intype2 }
                                    ],
                                'outtype': { 'type': outtype }
                                }
                            for intype1, intype2, outtype in [
                                ('int', 'int', 'int'),
                                ('date', 'int', 'date'),
                                ('date', 'date', 'int')
                                ]
                            ],
                        'infixop': '-'
                        },
                    'sum': {
                        'signatures': [
                            {
                                'intypes': [
                                    { 'type': 'int' }
                                    ],
                                'outtype': { 'type': 'int' }
                                }
                            ],
                        'sqlfunc': 'sum'
                        },
                    'count': count_func_prototype('count'),
                    'count_distinct': count_func_prototype('count')
                    }
                },
            'custom': {
                'functions': {
                    'drug_stockpile': {
                        'signatures': [
                            {
                                'intypes': [
                                    {
                                        'fieldnames': [
                                            'gapdays',
                                            'from_dt',
                                            'to_dt',
                                            'dayssup'
                                            ],
                                        'fieldtypes': {
                                            'gapdays': { 'type': 'int' },
                                            'from_dt': { 'type': 'date' },
                                            'to_dt': { 'type': 'date' },
                                            'dayssup': { 'type': 'int' }
                                            }
                                        }
                                    ],
                                'outtype': {
                                    'fieldnames': [
                                        'from_dt',
                                        'to_dt',
                                        'era'
                                        ],
                                    'fieldtypes': {
                                        'from_dt': { 'type': 'date' },
                                        'to_dt': { 'type': 'date' },
                                        'era': { 'type': 'int' }
                                        }
                                    }
                                }
                            ],
                        'window': True
                        }
                    }
                }
            }
        })

# customize the implementation of certain functions in the manifest

class RowFunction (Function):

    def outtype(self, intypes):
        """builtin/row maps the input type vector to an output type vector (record)"""
        rowtype = {
            'fieldnames': [ 'f%d' % (i+1) for i in range(0, len(intypes)) ],
            'fieldtypes': dict([ ('f%d' % (i+1), intypes[i].flatten() )
                                 for i in range(0, len(intypes)) ])
            }
        return RowType(rowtype, 'dynamically created row type')

row_func = RowFunction({
        'signatures': [
            {
                'intypes': [],
                'invartype': { 'type': 'ANY' }
                }
            ],
        'sqlfunc': 'ROW'
        })

row_func.pass2('builtin', 'row')

functions_manifest.data['namespaces']['builtin'].data['functions']['row'] = row_func

functions_manifest.data['namespaces']['builtin'].data['functions']['count_distinct'].sqlfunc_template = '%s(DISTINCT %s)'

