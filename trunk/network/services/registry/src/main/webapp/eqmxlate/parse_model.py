#!/usr/bin/python

import json
import model
import sys
import getopt

usage='Usage: ' + sys.argv[0] + ' [-x] filename\n' 
use_pso = True

class parse_model:
    def parse_file(self, infile, use_pso):
        md = json.load(infile)
        infile.close()
        m = model.Measure(md, use_psuedo_specific_occurrences=use_pso)
        m.simplify()
        return m



if __name__ == '__main__':
    try:
        opts, args = getopt.getopt(sys.argv[1:], "x", ["dont_use_pseudo_specific_occurrences"])
    except getopt.GetoptError as err:
        print str(err)
        print usage
        sys.exit(2)
    for o, a in opts:
        if o in ("-x", "dont_use_pseudo_specific_occurrences"):
           use_pso = False
    infile = sys.stdin
    if len(args) > 1:
        sys.stderr.write(usage)
        sys.exit(1)
    if len(args) > 0:
        infile = open(args[0])
    pm = parse_model()
    m = pm.parse_file(infile, use_pso)
    print m.sql_patients()
