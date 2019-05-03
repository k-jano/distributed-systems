import sys, Ice
import argparse
sys.path.append("../ICE/iceOUT")
import ClientBank
from currencyUpdater import CurrencyUpdater
from usersDict import UsersDict
from account import StandardAccountManagerI, PremiumAccountManagerI
import random
import string

THRESHOLD = 3000
PASSWD_LONG = 5
PESEL_LEN = 5

class BankManagerI(ClientBank.UsersRegistration):
    def __init__(self, currencyUpdater, UsersDict, adapter, communicator):
        self._currencyUpdater = currencyUpdater
        self._usersDict = UsersDict
        self._adapter = adapter
        self._communicator = communicator

    def passwdGenerate(self):
        letters = string.ascii_lowercase
        return ''.join(random.choice(letters) for i in range(PASSWD_LONG))


    def login(self, pesel, password, current=None):
        key1 = ClientBank.AccountKey(pesel, ClientBank.Type.STANDARD)
        key2 = ClientBank.AccountKey(pesel, ClientBank.Type.PREMIUM)
        usersDict = self._usersDict.getDict()
        counter = 0
        if key1 in list(usersDict.keys()):
            key = key1
            counter +=1
        
        if key2 in list(usersDict.keys()):
            key = key2
            counter +=1

        if counter == 0:
            raise ClientBank.UnauthorizedErr('Wrong pesel or passwd')

        clientType = usersDict.get(key).accountUser.type
        clientID = pesel + str(clientType.value)
        if (clientType == ClientBank.Type.STANDARD):
            accountI = StandardAccountManagerI(clientID)
        else:
            accountI = PremiumAccountManagerI(clientID)
        
        
        base = current.adapter.createProxy(Ice.stringToIdentity(clientID))

        if(clientType == ClientBank.Type.STANDARD):
            newI =  ClientBank.StandardAccountPrx.checkedCast(base)
        else:
            newI = ClientBank.PremiumAccountPrx.checkedCast(base)

        return ClientBank.LoginResponse(clientType, newI)
        


    def register(self, name, surname, pesel, income, current=None):
        if(income < 0):
            raise ClientBank.RegistrationErr('Income lower than 0')

        if(len(pesel)!=PESEL_LEN):
            raise ClientBank.RegistrationErr("Invalid pesel")

        if income > THRESHOLD:
            clientType = ClientBank.Type.PREMIUM
        else:
            clientType = ClientBank.Type.STANDARD

        key=ClientBank.AccountKey(pesel, clientType)
        usersDict = self._usersDict.getDict()
        if key in list(usersDict.keys()):
            raise ClientBank.RegistrationErr('Client already exist')

        password = self.passwdGenerate()
        objUser = ClientBank.AccountUser(name, surname, pesel, clientType, password)
        objBank = ClientBank.AccountBank(objUser, 0)
        self._usersDict.update(key, objBank)
        clientID = pesel + str(clientType.value)

        if (clientType == ClientBank.Type.STANDARD):
            accountI = StandardAccountManagerI(clientID)
        else:
            accountI = PremiumAccountManagerI(clientID)
        self._adapter.add(accountI, self._communicator.stringToIdentity(clientID))

        
        objReturn = ClientBank.RegistrationResponse(password, clientType)
        print("Registered " + str(pesel) + " " + str(clientType))
        return objReturn


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('port', type=int)
    parser.add_argument('currencies', nargs='+')
    args= parser.parse_args()
    currencyUpdater = CurrencyUpdater(set(args.currencies))
    usersDict = UsersDict()

    with Ice.initialize(sys.argv) as communicator:
        port=args.port
        adapter = communicator.createObjectAdapterWithEndpoints("BankAdapter", "default -p " + str(port))
        bankManager = BankManagerI(currencyUpdater, usersDict, adapter, communicator)
        adapter.add(bankManager, communicator.stringToIdentity("BankManager"))
        adapter.activate()
        communicator.waitForShutdown()
    