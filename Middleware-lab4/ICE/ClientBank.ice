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

    enum Type{
        STANDARD,
        PREMIUM
    };

    enum Currency{
        EUR,
        USD,
        CHF,
        GPB,
        PLN
    };

    struct LoanResponse{
        double valuePLN;
        double currencyValue;
        Currency currency;
        double loanPercent;
    };

    struct RegistrationResponse{
        string password;
        Type type;
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

    interface Users{
        RegistrationResponse register(string name, string surname, long pesel, double income) throws RegistrationErr;
    };

    interface BankAccounts{
        double getAccountValue(long pesel, string password) throws UnauthorizedErr;
        Loans getLoans(long pesel, string password, DateTime expected) throws UnauthorizedErr;
        LoanResponse getLoan(long pesel, string password, double value, Currency currency) throws UnauthorizedErr, LoanRefusalErr;
    };
};