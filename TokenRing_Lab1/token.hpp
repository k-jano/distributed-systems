#define BUFFER_SIZE 1024

typedef struct{
    int type; //1 - MSG, 2 - CONNECT, 3 - ADD, 4 - EMPTY
    char msg[BUFFER_SIZE];
    int portD;
    int portS;
    char ipAddr[BUFFER_SIZE];
} token;