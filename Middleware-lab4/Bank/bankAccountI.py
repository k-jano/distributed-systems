import sys, Ice
import ClientBank

class BankAccountI(ClientBank.Users):
    def register(self, name, surname, pesel, income):
        print('Hi')

def runBankAccount(port):
    with Ice.initialize(sys.argv) as communicator:
        adapter = communicator.createObjectAdapterWithEndpoints("Bank", "default -p "+ str(port))
        object = BankAccountI()
        adapter.add(object, communicator.stringToIdentity("Bank"))
        adapter.activate()
        communicator.waitForShutdown()        