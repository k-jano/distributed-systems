#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <string.h>
#include <netinet/in.h>
#include <queue>
#include "token.cpp"
#include <unistd.h>
#include <pthread.h>
#include <string>
#include <arpa/inet.h>
#include <sys/socket.h>
#define MULT_GROUP "224.2.3.4"
#define MULT_PORT_1 9000
using namespace std;


string id;
int port;
char* ip;
int portN;
bool hasToken;
int protocol;

int multicastFd;
int neighbourFd;
int myFd;
queue<token> tokenQueue;
pthread_mutex_t mutex;


void establishMulticast(){
    if ((multicastFd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) { 
        perror("socket mult creation failed"); 
        exit(EXIT_FAILURE); 
    } 
    cout << "Establish multicast socket \n";
}

void establishNeighbour(){
    if ((neighbourFd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) { 
        perror("socket neighb creation failed"); 
        exit(EXIT_FAILURE); 
    } 
    cout << "Establish neighbour socket \n";
}

void establishMyself(){
    if ((myFd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) { 
        perror("my socket creation failed"); 
        exit(EXIT_FAILURE); 
    } 

    struct sockaddr_in addr;

    addr.sin_family= AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(port);
    if(bind(myFd, (const struct sockaddr *) &addr, sizeof(addr)) < 0){
        perror("my socket binding failed");
        exit(EXIT_FAILURE);
    }
    cout << "Establish my listening socket \n";
}

void init(){
    establishMulticast();
    establishNeighbour();
    establishMyself();
}

void send(token token){
    struct sockaddr_in     sendAddr;
    sendAddr.sin_family = AF_INET; 
    sendAddr.sin_port = htons(portN); 
    sendAddr.sin_addr.s_addr = inet_addr(ip); 
    sendto(neighbourFd, &token, sizeof(token), 0, (const struct sockaddr *) &sendAddr,  sizeof(sendAddr));
}

void sendMult(string ID, int port){
    struct sockaddr_in     sendAddr;
    char * toSend = new char[1000];
    strcpy(toSend, ID.c_str());
    strcat(toSend, " listening on port ");
    char* numberString = new char[1000];
    sprintf(numberString, "%d received a token", port);
    strcat(toSend, numberString);
    sendAddr.sin_family = AF_INET; 
    sendAddr.sin_port = htons(MULT_PORT_1); 
    sendAddr.sin_addr.s_addr = inet_addr(MULT_GROUP); 
    sendto(multicastFd, (const char *)toSend, strlen(toSend), 0, (const struct sockaddr *) &sendAddr,  sizeof(sendAddr));
    free(toSend);
    free(numberString);
}

void connect(){
    char myIp[BUFFER_SIZE]="127.0.0.1";
    token conToken = connectToken(port, portN, myIp);
    send(conToken);
}

void startWithToken(){
    if(hasToken){
        token empToken = emptyToken();
        send(empToken);
    }
}

void emptyAction(){
    token tmpToken;
    pthread_mutex_lock(&mutex);
    if(tokenQueue.empty()){
        tmpToken = emptyToken();
    } else {
        tmpToken = tokenQueue.front();
        tokenQueue.pop();
    }
    pthread_mutex_unlock(&mutex);
    send(tmpToken);
}

void connectRequestAction(token rcvToken){
    int portSource = rcvToken.portS;
    int portDestination = rcvToken.portD;
    char* ipAddrTmp = rcvToken.ipAddr;
    token newToken = addToken(portSource, portDestination, ipAddrTmp);
    pthread_mutex_lock(&mutex);
    tokenQueue.push(newToken);
    pthread_mutex_unlock(&mutex);
}

void addMemberAction(token rcvToken){
    int portSource = rcvToken.portS;
    int portDestination = rcvToken.portD;
    char* ipTmp = rcvToken.ipAddr;
    token tmpToken;
    if(portDestination == portN){
        portN = portSource;
        strcpy(ip, ipTmp);
        tmpToken = emptyToken();
    } else {
        tmpToken = rcvToken;
    }
    send(tmpToken);
}

void printMsg(token rcvToken){
    char* sourceNr = new char[1000];
    sprintf(sourceNr, "%d", rcvToken.portS);
    string sourceNrString = sourceNr;
    string tmp = "Messege from " + sourceNrString + " : " + rcvToken.msg + " \n";
    cout <<tmp;
}


void msgReceiveAction(token rcvToken){
    if(rcvToken.portD == port){
        printMsg(rcvToken);
        rcvToken = emptyToken();
    } else if(rcvToken.portS== port){
        cout << "Didn't reach destination \n";
        rcvToken = emptyToken();
    } 
    send(rcvToken);
    
}


void ringMemberSchedule(){
    token tmpToken = emptyToken();
    while(1){
        struct sockaddr_in addr;
        int len = sizeof(addr);
        if(recvfrom(myFd, &tmpToken, sizeof(tmpToken), 0, (struct sockaddr *) &addr, (socklen_t*) &len) != sizeof(tmpToken)){
            perror("reciving token failed");
            exit(EXIT_FAILURE);
        }
        int tmpType = tmpToken.type;
        if(tmpType!=2){
            sendMult(id, port);
            sleep(2);          
        }

        if(tmpType == 4){
            emptyAction();
        } else if( tmpType== 2){
            connectRequestAction(tmpToken);
        } else if(tmpType == 3){
            addMemberAction(tmpToken);
        } else if(tmpType ==1){
            msgReceiveAction(tmpToken);
        }
    }
}

void* userFunc(void* threadArgs){
    while(1){
        char msg[BUFFER_SIZE];
        int destP;
        cout<< "Enter message \n";
        cin >> msg;
        cout << "Enter destination port \n";
        cin >> destP;
        token tmpToken = msgToken(port, destP, msg);
        pthread_mutex_lock(&mutex);
        tokenQueue.push(tmpToken);
        pthread_mutex_unlock(&mutex);
    }
}

void udpStart(){
    init();
    connect();
    startWithToken();
    pthread_t userMsg;
    mutex = PTHREAD_MUTEX_INITIALIZER;
    if(pthread_create(&userMsg, NULL, userFunc, NULL) != 0){
        perror("During thread creation");
        exit(1);
    }
    ringMemberSchedule();
}
