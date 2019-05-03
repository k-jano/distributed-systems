import sys, Ice
sys.path.append("../ICE/iceOUT")
import ClientBank

class StandardAccountManagerI(ClientBank.StandardAccount):
    def __init__(self, userID):
        self._userID = userID

    def getAccountBalance(self, current):
        print('[' + self._userID + ']' + "got account value request")
        return 0.0

class PremiumAccountManagerI(StandardAccountManagerI, ClientBank.PremiumAccount):
    def hello(self):
        print("Hello")
