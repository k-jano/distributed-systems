var PROTO_PATH = __dirname + "/../Proto/protoIn/currencyExchange.proto";
var grpc = require("grpc");
var protoLoader = require("@grpc/proto-loader");

const HOST = "127.0.0.1";
const PORT = "50051";
const TIME = 10000;
const eps = 0.005

var packageDefinition = protoLoader.loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true
});

var currValues = {
  EUR: 4.2,
  USD: 3.8,
  CHF: 3.7,
  GBP: 4.9
};

var currSubscribers = {}

function addBank(call) {
  var port = call.request.port
  var curs = call.request.curs
  console.log("Adding bank " + port)
  currSubscribers[port] = {call, curs}
  curs.forEach(cur =>{
    call.write({
      cur: cur,
      val: currValues[cur]
    })
  })
}

function update(){
  for (var key in currValues){
    sign = Math.random()*2
    sign>1 ? sign=1 : sign=0
    
    multiplicator = Math.random()*2
    multiplicator>1 ? multiplicator=2 :multiplicator=1

    currentValue = currValues[key]
    difference = eps* multiplicator *currentValue
    sign ? currentValue+= difference: currentValue -= difference
    currValues[key]=currentValue
  }
  curEur = currValues['EUR']
  toPrint = curEur.toFixed(2)
  console.log(currValues['EUR'].toFixed(2) + 'EUR ' + currValues['USD'].toFixed(2) + 'USD ' +currValues['CHF'].toFixed(2) + 'CHF ' + currValues['GBP'].toFixed(2) + 'GPB ')
  for (var sub in currSubscribers){
    //console.log(sub)
    call = currSubscribers[sub].call
    currSubscribers[sub].curs.forEach(cur =>
      call.write({
        cur: cur,
        val: currValues[cur]
      })
    )
  }
  setTimeout(update, TIME)
}

var protoDescriptor = grpc.loadPackageDefinition(packageDefinition);
var currencyEx = protoDescriptor.currencyEx;
var server = new grpc.Server();
server.addService(currencyEx.currencyService.service, {
  addBank: addBank,
});

server.bind(HOST + ":" + PORT, grpc.ServerCredentials.createInsecure());
server.start();
update();