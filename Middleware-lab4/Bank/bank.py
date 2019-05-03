import sys, Ice
import argparse
sys.path.append("../ICE/iceOUT")
import ClientBank
from currencyUpdater import CurrencyUpdater

class BankManagerI(ClientBank.UsersRegistration):
    def __init__(self, currencyUpdater):
        self.currencyUpdater = currencyUpdater
        self.usersDict = dict()

    def register(self, name, surname, pesel, income, current=None):
        print("Register catched")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('port', type=int)
    parser.add_argument('currencies', nargs='+')
    args= parser.parse_args()
    currencyUpdater = CurrencyUpdater(set(args.currencies))

    with Ice.initialize(sys.argv) as communicator:
        port=args.port
        adapter = communicator.createObjectAdapterWithEndpoints("BankAdapter", "default -p " + str(port))
        bankManager = BankManagerI(currencyUpdater)
        adapter.add(bankManager, communicator.stringToIdentity("BankManager"))
        adapter.activate()
        communicator.waitForShutdown()
    