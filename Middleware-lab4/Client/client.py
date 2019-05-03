import sys, Ice
import argparse
sys.path.append("../ICE/iceOUT")
import ClientBank

class ClientI:
    def __init__(self, port, communicator):
        self._port = port
        self._communicator = communicator

        bankBase = self._communicator.stringToProxy('BankManager:default -p ' +str(port))
        bank = ClientBank.UsersRegistrationPrx.checkedCast(bankBase)
        if not bank:
            raise RuntimeError("Invalid port")
        else:
            self._bank = bank

    def getAccountBalance(self):
        #ctx = {'login': self._pesel, 'clientType': self._clientType}
        #currant = {'ctx': ctx}
        #current = "Hi Jude"
        value = self._account.getAccountBalance()
        print('Actual account balance: ' + str(value) + ' PLN')


    def secondPanel(self):
        while True:
            print('-----Second Panel-----')
            action = input("Action: ")
            action = action.lower()
            if action == 'g':
                self.getAccountBalance()
            elif action == 'q':
                return
            else:
                print('Invalid action')

    def register(self):
        print('-----Registration-----')
        name = input('Name: ')
        surname = input('Surname: ')
        pesel = input('Pesel: ')
        income = input('Income: ')
        try:
            response = self._bank.register(name, surname, pesel, float(income))
            print("Given password: " + response.password + " type: " + str(response.type))
        except Exception as ex:
            print('Exception: ' + str(ex))
        

    def login(self):
        print('-----LOGIN-----')
        pesel = input('Pesel: ')
        password = input ('Password: ')
        try:
            response = self._bank.login(pesel, password)
            self._account = response.accountAdministrator
            self._pesel = pesel
            self._password = password
            self._clientType = response.type
            self.secondPanel()
        except Exception as ex:
            print('Excetpion: ' + str(ex))

    def firstPanel(self):
        while True:
            print('-----First Panel-----')
            action = input('Action: ')
            action = action.lower()
            if action == 'r':
                self.register()
            elif action == 'l':
                self.login()
            elif action == 'q':
                return
            else:
                print('Invalid action')
            

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('port', type=int)
    args= parser.parse_args()

    with Ice.initialize(sys.argv) as communicator:
        port=args.port
        clientI = ClientI(port, communicator)
        clientI.firstPanel()