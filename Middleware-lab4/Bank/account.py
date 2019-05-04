import sys, Ice
sys.path.append("../ICE/iceOUT")
import ClientBank
from usersDict import UsersDict
import datetime

class StandardAccountManagerI(ClientBank.StandardAccount):
    def __init__(self, userID, pesel, clientType, usersDict, currencyUpdater):
        self._userID = userID
        self._pesel = pesel
        self._clientType = clientType
        self._usersDict = usersDict
        self._currencyUpdater = currencyUpdater
        
    def getAccountBalance(self, current):
        usersDict = self._usersDict.getDict()
        key = ClientBank.AccountKey(self._pesel, self._clientType)
        accBank = usersDict.get(key)
        balance = accBank.value
        print('[' + self._userID + ']' + " Account balance request")
        return balance


class PremiumAccountManagerI(StandardAccountManagerI, ClientBank.PremiumAccount):
    def getLoan(self, value, currency, date, current):
        now = datetime.datetime.now()
        currYear = now.year
        currMonth = now.month
        currDay = now.day

        if (date.month<1 or date.month > 12 or date.day < 1 or date.day > 31):
            raise ClientBank.LoanRefusalErr("Wrong date format")

        if(date.year < currYear or (date.year == currYear and date.month < currMonth) or (date.year == currYear and date.month == currMonth and date.day <= currDay)):
            raise ClientBank.LoanRefusalErr("Date from the past")

        currencies = self._currencyUpdater.getCurrencies()
        if currency not in currencies.keys():
            raise ClientBank.LoanRefusalErr("Bank does not support this currency")

        exchange = currencies.get(currency)
        print('[' + self._userID + ']' + " Loan request")
        return ClientBank.LoanResponse(value, value*exchange, currency, exchange)


        
