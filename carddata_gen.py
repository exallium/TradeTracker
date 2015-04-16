import zipfile
import json
import sys
import itertools

INFILE = "AllSetsArray.json"
OUTFILE = "card_data.json"

def parse_cards(card_set):
    print "PARSE CARD SET %s WITH %s CARDS" % (card_set['name'], len(card_set['cards']))
    return [{'name': card['name'], 'multiverseId': card['multiverseid'] if 'multiverseid' in card else -1, 'set': card_set['code']} for card in card_set['cards']]

def parse_sets(card_sets):
    return [{'code': card_set['code'], 'name': card_set['name'], 'count': len(card_set['cards'])} for card_set in card_sets]
    
def carddata_gen():
    with open(INFILE, 'r') as fin:
        card_sets = json.load(fin)
        card_data = list(itertools.chain(*[parse_cards(card_set) for card_set in card_sets]))
        data = [parse_sets(card_sets), card_data]
        with open(OUTFILE, 'w') as fout:
            fout.write(json.dumps(data))


if __name__ == '__main__':
    carddata_gen()
