import sys
sys.path.append("../ICE/iceOUT")
import ClientBank

class CurrencyUpdater():
    def __init__(self, currencies):
        self.currencies = currencies
        self.enumCurrencies = []
        for currency in self.currencies:
            if currency == 'EUR':
                self.enumCurrencies.append(ClientBank.Currency.EUR)
            elif currency == 'USD':
                self.enumCurrencies.append(ClientBank.Currency.USD)
            elif currency == 'CHF':
                self.enumCurrencies.append(ClientBank.Currency.CHF)
            elif currency == 'GPB':
                self.enumCurrencies.append(ClientBank.Currency.GPB)
            elif currency == 'PLN':
                self.enumCurrencies.append(ClientBank.Currency.PLN)
            else:
                raise Exception("Wrong currency")

        print(self.enumCurrencies)
        if(ClientBank.Currency.PLN not in self.enumCurrencies):
            self.enumCurrencies.append(ClientBank.Currency.PLN)
        self.courrenciesDict = dict()
        for enumCurrency in self.enumCurrencies:
            if enumCurrency == ClientBank.Currency.PLN:
                val = 1
            else:
                val = 5
            self.courrenciesDict[enumCurrency] = val
        print(self.courrenciesDict)

    def getCurrencies(self):
        return self.currencies