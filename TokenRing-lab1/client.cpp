#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <string.h>
#include <netinet/in.h>
#include <queue>
#include "clientUDP.cpp"
#include <unistd.h>
#include <pthread.h>
#include <string>

using namespace std;


void TCPestablishNeighbour(){
    if ((neighbourFd = socket(AF_INET, SOCK_STREAM, 0)) < 0 ) { 
        perror("socket neighb creation failed"); 
        exit(EXIT_FAILURE); 
    } 
    cout << "Establish neighbour socket \n";
}

void TCPestablishMyself(){
    if ((myFd = socket(AF_INET, SOCK_STREAM, 0)) < 0 ) { 
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

    if(listen(myFd,10) <0){ 
        perror("my socket listening failed");
        exit(EXIT_FAILURE);
    }

    cout << "Establish my listening socket \n";
}

void TCPinit(){
    establishMulticast();
    TCPestablishMyself();
}

void TCPsend(token sendToken){
    if ((neighbourFd = socket(AF_INET, SOCK_STREAM, 0)) < 0 ) { 
        perror("socket neighb creation failed"); 
        exit(EXIT_FAILURE); 
    } 

    struct sockaddr_in sendAddr;

    sendAddr.sin_family= AF_INET;
    sendAddr.sin_addr.s_addr = inet_addr(ip);
    sendAddr.sin_port = htons(portN);
    if(connect(neighbourFd, (const struct sockaddr *) &sendAddr, sizeof(sendAddr))< 0){
        perror("socket neighb connection failed");
        exit(EXIT_FAILURE);
    }

    sendto(neighbourFd, &sendToken, sizeof(sendToken), 0, (const struct sockaddr *) &sendAddr,  sizeof(sendAddr));

    if(close(neighbourFd) < 0){
        perror("socket neighb closing failed");
        exit(EXIT_FAILURE);
    }
}

void TCPconnect(){
    char myIp[BUFFER_SIZE]="127.0.0.1";
    token conToken = connectToken(port, portN, myIp);
    TCPsend(conToken);
}

void TCPstartWithToken(){
    if(hasToken){
        token empToken = emptyToken();
        TCPsend(empToken);
    }
}

void TCPconnectRequestAction(token rcvToken){
    int portSource = rcvToken.portS;
    int portDestination = rcvToken.portD;
    char* ipAddrTmp = rcvToken.ipAddr;
    if(hasToken){
        portN = portSource;
        strcpy(ip, ipAddrTmp);
        rcvToken= emptyToken();
        TCPsend(rcvToken);
        hasToken=false;
    } else {
        token newToken = addToken(portSource, portDestination, ipAddrTmp);
        pthread_mutex_lock(&mutex);
        tokenQueue.push(newToken);
        pthread_mutex_unlock(&mutex);
    }
}

void TCPemptyAction(){
    token tmpToken;
    pthread_mutex_lock(&mutex);
    if(tokenQueue.empty()){
        tmpToken = emptyToken();
    } else {
        tmpToken = tokenQueue.front();
        tokenQueue.pop();
    }
    pthread_mutex_unlock(&mutex);
    TCPsend(tmpToken);
}

void TCPaddMemberAction(token rcvToken){
    int portSource = rcvToken.portS;
    int portDestination = rcvToken.portD;
    char* ipAddrTmp = rcvToken.ipAddr;
    token tmpToken;
    if(portDestination == portN){
        portN = portSource;
        strcpy(ip, ipAddrTmp);
        tmpToken = emptyToken();
    } else {
        tmpToken = rcvToken;
    }
    TCPsend(tmpToken);
}

void TCPmsgReceiveAction(token rcvToken){
    if(rcvToken.portD == port){
        printMsg(rcvToken);
        rcvToken = emptyToken();
    } else if(rcvToken.portS== port){
        cout << "Didn't reach destination \n";
        rcvToken = emptyToken();
    } 
    TCPsend(rcvToken);
}

void TCPringMemberSchedule(){
    token tmpToken = emptyToken();
    while(1){
        int receivedSth;
        if((receivedSth = accept(myFd, NULL, NULL)) <0){
            perror("my socket accept failed");
            exit(EXIT_FAILURE);
        }

        if(read(receivedSth, &tmpToken, sizeof(token)) != sizeof(token)){
            perror("my socket read failed");
            exit(EXIT_FAILURE);
        }

        if(close(receivedSth) < 0 ){
            perror("my socket closed");
            exit(EXIT_FAILURE);
        }

        int tmpType = tmpToken.type;
        if(tmpType!=2){
            sendMult(id, port);
            sleep(2);          
        }

        if(tmpType==2){
            TCPconnectRequestAction(tmpToken);
        } else if(tmpType==4){
            TCPemptyAction();
        } else if(tmpType==3){
            TCPaddMemberAction(tmpToken);
        } else if(tmpType==1){
            TCPmsgReceiveAction(tmpToken);
        }
    }
}

void clientTCP(){
    TCPinit();
    if(port != portN){
        TCPconnect();
    }
    pthread_t userMsg;
    mutex = PTHREAD_MUTEX_INITIALIZER;
    if(pthread_create(&userMsg, NULL, userFunc, NULL) != 0){
        perror("During thread creation");
        exit(1);
    }
    TCPringMemberSchedule();
}

int main(int argc, char** argv) 
{
    //Parsing
    if(argc!=7){
        perror("Wrong nr of args \n");
        exit(1);
    }

    id= argv[1];
    cout << "My id: " << id << "\n";
    port = atoi(argv[2]);
    cout << "My port: " << port << "\n";
    ip = argv[3];
    portN = atoi(argv[4]);


    //Has Token
    int tmpToken=atoi(argv[5]);
    if(tmpToken==0){
        hasToken= false;
    } else if(tmpToken==1){
        hasToken = true;
    } else {
        perror("Wrong Token arg");
        exit(1);
    }


    //Protocol
    string protTmp = argv[6];
    if(protTmp=="tcp"){
        clientTCP();
    } else if(protTmp=="udp"){
        udpStart();
    } else {
        perror("Wrong Protocol arg");
        exit(1);
    }
}