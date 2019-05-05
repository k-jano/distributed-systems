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

    enum Type{
        STANDARD =1,
        PREMIUM =2
    };

    enum Currency{
        EUR =1,
        USD =2,
        CHF =3,
        GBP =4,
        PLN =5
    };

    struct LoanResponse{
        float valuePLN;
        float currencyValue;
        Currency currency;
        float exchange;
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

    struct Date{
        int year;
        int month;
        int day;
    };

    struct RegistrationResponse{
        string password;
        Type type;
    };
    
    interface StandardAccount{
        float getAccountBalance();
    };

    interface PremiumAccount extends StandardAccount{
        LoanResponse getLoan(float value, Currency currency, Date date) throws LoanRefusalErr;
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