import Ice
Ice.loadSlice("../ICE/ClientBank.ice")
import ClientBank
from bankAccountI import runBankAccount

bank_id=''
currencies=[]

class Bank():
    def __init__(self, bank_id, currencies, port):
        Bank.bank_id = bank_id
        Bank.currencies = currencies
        print(ClientBank)
        print(bank_id)
        print(currencies)
        print(port)
        runBankAccount(port)


#Bank("ul", "ce")
