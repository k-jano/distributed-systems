import sys
sys.path.append("../ICE/iceOUT")
import ClientBank
import time
import grpc
sys.path.append("../Proto/protoOut/grpc")
sys.path.append("../Proto/protoOut/python")
import currencyExchange_pb2
import currencyExchange_pb2_grpc

INITIAL_VALUE = 5.0
PORT = '50051'

class CurrencyUpdater():
    def __init__(self, currencies):
        self.currencies = currencies
        self.enumCurrencies = []
        self.protoEnumCurrencies = []
        self.protoIceDict = dict()
        for currency in self.currencies:
            if currency == 'EUR':
                self.enumCurrencies.append(ClientBank.Currency.EUR)
                self.protoEnumCurrencies.append(currencyExchange_pb2.Currency.EUR)
                self.protoIceDict[currencyExchange_pb2.Currency.EUR] = ClientBank.Currency.EUR
            elif currency == 'USD':
                self.enumCurrencies.append(ClientBank.Currency.USD)
                self.protoEnumCurrencies.append(currencyExchange_pb2.Currency.USD)
                self.protoIceDict[currencyExchange_pb2.Currency.USD] = ClientBank.Currency.USD
            elif currency == 'CHF':
                self.enumCurrencies.append(ClientBank.Currency.CHF)
                self.protoEnumCurrencies.append(currencyExchange_pb2.Currency.CHF)
                self.protoIceDict[currencyExchange_pb2.Currency.CHF] = ClientBank.Currency.CHF
            elif currency == 'GPB':
                self.enumCurrencies.append(ClientBank.Currency.GPB)
                self.protoEnumCurrencies.append(currencyExchange_pb2.Currency.GPB)
                self.protoIceDict[currencyExchange_pb2.Currency.GPB] = ClientBank.Currency.GPB
            elif currency == 'PLN':
                self.enumCurrencies.append(ClientBank.Currency.PLN)
            else:
                raise Exception("Wrong currency")

        #print(self.enumCurrencies)
        if(ClientBank.Currency.PLN not in self.enumCurrencies):
            self.enumCurrencies.append(ClientBank.Currency.PLN)
            
        self.currenciesDict = dict()
        for enumCurrency in self.enumCurrencies:
            if enumCurrency == ClientBank.Currency.PLN:
                val = 1
            else:
                val = INITIAL_VALUE
            self.currenciesDict[enumCurrency] = val
        print(self.currenciesDict)
        self._channel = grpc.insecure_channel('127.0.0.1:' + PORT)
        self._stub = currencyExchange_pb2_grpc.currencyServiceStub(self._channel)

    def getCurrencies(self):
        return self.currenciesDict
    
    def currencyUpdaterRoutine(self):
        request=currencyExchange_pb2.Subscribe(
            curs = self.protoEnumCurrencies
        )
        for response in self._stub.print(request):
            self.currenciesDict[self.protoIceDict[response.cur]] = response.val
            
