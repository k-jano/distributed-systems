class UsersDict():
    def __init__(self):
        self._userDict = dict()
    
    def getDict(self):
        return self._userDict

    def update(self, key, value):
        self._userDict[key] = value