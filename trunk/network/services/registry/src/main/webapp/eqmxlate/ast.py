
"""
Generic recursive-descent tree parsing framework.

Assuming input is in the form of a dictionary/list/constant tree as
obtained by deserializing from json.load()...

This framework allows quasi-syntactic validation to be performed by
creating a grammar consisting of a root parsing class which can
reference other parsing classes for sub-tree content.  By transforming
the raw tree into an AST with application-extended classes, other
semantic-level processing can be associated with the tree nodes in the
proper context.

The recursion over a hierarchical dictionary is done by deriving
parsing classes from the 'record' class defined in this module. The
'constant' class is used to represent leaf values.

  import ast

  class MyTreeNode (ast.record):
    prototype = [
      ('field1', /* child parsing pattern */...),
      ...
      ]
    defaults = {
      'field1': /* default value */...,
      ...
      }

Parsing is performed by trying to construct the tree node with
the deserialized JSON tree as argument:

  ast = MyTreeNode( json.load(json_file) )

In order to successfully parse, all named fields in the prototype MUST
be found in the input tree root or the defaults dictionary.  The
sub-tree found under that named field or the default value if the
named field is absent but a default is present, is then parsed
recursively using the child parsing pattern.

The child patterns supported are:

   A. The value 'None' matches an input value 'None' with no further
      parsing.

   B. A parser class is applied directly to the input sub-tree.

   C. A 1-member list containing a child pattern requires the input
      sub-tree to be a Python list and the child pattern is mapped
      over each element of the input list, yielding a list of parse
      trees.

   D. A 1-member dictionary containing a named child pattern requires
      the input sub-tree to be a Python dictionary and the child
      pattern is mapped over each item value of the input dictionary,
      yielding a dictionary of named parse trees preserving the keys
      of the input dictionary.

   E. A tuple of child patterns is an ordered list of alternatives
      which are applied to the sub-tree until the first successful
      parse is completed.

"""

import json

def parse_alternatives(alternatives, subtree, context):
    for proto in alternatives:
        try:
            return parse_subtree(proto, subtree, context)
        except TypeError, e:
            pass
            
    raise TypeError('%sparsing failed for all alternative syntaxes %s'
                    % (context and context + ': ' or '', 
                       [ proto and hasattr(proto, 'prototype') and dict(proto.prototype) or proto for proto in alternatives ]))

def parse_array(proto, subtree, context):
    if type(subtree) != list:
        raise TypeError('%sexpected array but got %s instead'
                         % (context and context + ': ' or '', type(subtree)))
    return [ parse_subtree(proto[0], subtree[i], context + '[%d]' % i) for i in range(0, len(subtree)) ]

def parse_map(proto, subtree, context):
    if type(subtree) != dict:
        raise TypeError('%sexpected dictionary but got %s instead'
                         % (context and context + ': ' or '', type(subtree)))
    proto = proto.items()[0][1]
    return dict([ (k, parse_subtree(proto, v, context + '[' + k + ']'))
                  for k, v in subtree.items() ])

def parse_subtree(proto, subtree, context):
    if type(proto) == tuple:
        return parse_alternatives(proto, subtree, context)
    elif type(proto) == list:
        return parse_array(proto, subtree, context)
    elif type(proto) == dict:
        return parse_map(proto, subtree, context)
    elif proto == None:
        if subtree == None:
            return None
        else:
            raise TypeError('%sexpected null but got %s instead'
                            % (context and context + ': ' or '', type(subtree)))
    else:
        return proto(subtree, context)

def flatten_subtree(subtree):
    if subtree == None:
        return None
    if type(subtree) == list:
        return [ flatten_subtree(v) for v in subtree ]
    elif type(subtree) == dict:
        return dict([ (k, flatten_subtree(v)) for k, v in subtree.items() ])
    else:
        return subtree.flatten()

class record (object):

    prototype = {}
    defaults = {}

    def __init__(self, tree, context=''):
        self.data = {}

        if type(tree) != dict:
            raise TypeError('%sexpected record but got %s instead' 
                             % (context and context + ': ' or '', type(tree)))

        for attr, proto in self.prototype:
            if (attr not in tree) and (attr not in self.defaults):
                raise TypeError('%smissing required attribute %s'
                                % (context and context + ': ' or '', attr))

        for attr, proto in self.prototype:
            # parse field based on grammatical prototype and defaults
            subtree = tree.get(attr, self.defaults.get(attr))
            try:
                self.data[attr] = parse_subtree(proto, subtree, context + '/' + attr)
            except TypeError, e:
                raise ValueError(str(e))

    def flatten(self):
        return dict([ (attr, flatten_subtree(self.data[attr]))
                      for attr, proto in self.prototype
                      if attr not in self.defaults or self.data[attr] != self.defaults[attr]])

    def __repr__(self):
        return repr(self.data)

class constant (object):

    prototype = None

    def __init__(self, value, context=''):
        if type(value) != self.prototype:
            raise TypeError('%sexpected %s but got %s instead'
                             % (context and context + ': ' or '', 
                                self.prototype,
                                type(value)))
        self.value = value

    def flatten(self):
        return self.value

    def __repr__(self):
        return repr(self.value)

