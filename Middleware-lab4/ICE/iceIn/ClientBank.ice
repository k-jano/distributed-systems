module ClientBank{

    exception UnauthorizedErr{
        string msg;
    }

    exception RegistrationErr{
        long pesel;
        string msg;
    }

    exception LoanRefusalErr{
        string msg;
    }

    exception WithdrawErr{
        string msg;
    }

    enum Type{
        STANDARD =1,
        PREMIUM =2
    };

    enum Currency{
        EUR =1,
        USD =2,
        CHF =3,
        GPB =4,
        PLN =5
    };

    struct LoanResponse{
        double valuePLN;
        double currencyValue;
        Currency currency;
        double loanPercent;
    };

    struct AccountUser{
        string name;
        string surname;
        long pesel;
        Type type;
        string password;
    };

    struct DateTime{
        int Year;
		byte Month;
		byte Day;
		byte Hour;
		byte Minute;
		byte Second;
    };

    struct Loan{
        double valuePLN;
        double value;
        Currency Currency;
        DateTime taken;
        DateTime expected;
        double loanPercent;
    };

    sequence<Loan> Loans;

    struct AccountBank{
        AccountUser account;
        double value;
        Loans loans;
    };

    interface StandardAccount{
        double getAccountValue(long pesel, string password) throws UnauthorizedErr;
        void depositMoney (double value) throws UnauthorizedErr;
        void withdrawMoney (double value) throws UnauthorizedErr, WithdrawErr;
    };

    interface PremiumAccount extends StandardAccount{
        LoanResponse getLoan(long pesel, string password, double value, Currency currency) throws UnauthorizedErr, LoanRefusalErr;
    }

    struct RegistrationResponse{
        string password;
        Type type;
    };

    interface UsersRegistration{
        void register(string name, string surname, long pesel, double income) throws RegistrationErr;
    };
};