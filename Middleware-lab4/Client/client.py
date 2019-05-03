import sys, Ice
import argparse
sys.path.append("../ICE/iceOUT")
import ClientBank

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('port', type=int)
    args= parser.parse_args()

    with Ice.initialize(sys.argv) as communicator:
        port=args.port
        bankBase = communicator.stringToProxy('BankManager:default -p ' +str(port))
        bank = ClientBank.UsersRegistrationPrx.checkedCast(bankBase)
        if not bank:
            raise RuntimeError("Invalid proxy")
        
        bank.register("ja", "ja2", 9812352123, 3122)