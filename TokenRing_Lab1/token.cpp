#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <string.h>
#include <netinet/in.h>
#include <queue>
#include "token.hpp"
using namespace std;

token emptyToken(){
    token token;
    token.type = 4;
    return token;
}

token connectToken(int portSource, int portDestination, char* ip){
    token token;
    token.type=2;
    token.portD=portDestination;
    token.portS=portSource;
    strcpy(token.ipAddr,ip);
    return token;
}

token addToken(int portSource, int portDestination, char *ip){
    token token;
    token.type=3;
    token.portS = portSource;
    token.portD= portDestination;
    strcpy(token.ipAddr, ip);
    return token;
}

token msgToken(int portSource, int portDestination, char* msg){
    token token;
    token.type=1;
    token.portS=portSource;
    token.portD = portDestination;
    strcpy(token.msg, msg);
    return token;
}