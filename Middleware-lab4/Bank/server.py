import argparse
from bank import Bank

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('bank_id', type=str)
    parser.add_argument('port', type=int)
    parser.add_argument('currencies', nargs='+')
    args= parser.parse_args()
    b=Bank(args.bank_id, args.currencies, args.port)