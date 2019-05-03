module ClientBank{

    exception UnauthorizedErr{
        string msg;
    };

    exception RegistrationErr{
        string msg;
    };

    exception LoanRefusalErr{
        string msg;
    };

    exception WithdrawErr{
        string msg;
    };

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
        float valuePLN;
        float currencyValue;
        Currency currency;
        float loanPercent;
    };

    struct AccountKey{
        string pesel;
        Type type;
    };

    struct AccountUser{
        string name;
        string surname;
        string pesel;
        Type type;
        string password;
    };

    struct AccountBank{
        AccountUser accountUser;
        float value;
    };

    interface StandardAccount{
        float getAccountBalance() throws UnauthorizedErr;
    };

    interface PremiumAccount extends StandardAccount{
        LoanResponse getLoan(string pesel, string password, float value, Currency currency) throws UnauthorizedErr, LoanRefusalErr;
    };

    struct RegistrationResponse{
        string password;
        Type type;
    };
    
    struct LoginResponse{
        Type type;
        StandardAccount* accountAdministrator;
    };

    interface UsersRegistration{
        RegistrationResponse register(string name, string surname, string pesel, float income) throws RegistrationErr;
        LoginResponse login(string pesel, string password) throws UnauthorizedErr;
    };
};