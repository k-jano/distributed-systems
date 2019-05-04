var PROTO_PATH = __dirname + "/../Proto/protoIn/currencyExchange.proto";
var grpc = require("grpc");
var protoLoader = require("@grpc/proto-loader");

const HOST = "127.0.0.1";
const PORT = "50051";
const TIME = 5000;

var packageDefinition = protoLoader.loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true
});

var mapCurr = {
  EUR: 5,
  USD: 6,
  CHF: 7,
  GBP: 8
};

var currSubscribers = {
  EUR: [],
  USD: [],
  CHF: [],
  GBP: []
};

function addSubsriber(id, curs) {
  for (var cur in curs) {
    var list = currSubscribers.get(cur);
    var isInList = false;
    for (var elem in list) {
      if (elem == id) isInList = true;
    }
    if (!isInList) {
      list.push(id);
    }
    currSubscribers.set(cur, list);
  }
}

function addBank(call) {
  console.log("Request")
  var id = call.request.id;
  var curs = call.request.curs;
  addSubsriber(id, curs);
  for (var cur in curs) {
    call.write({
      curs: cur,
      val: currSubscribers.get(cur)
    });
  }
}

function removeBank(call) {
  var id = call.request.id;
  for (var key in currSubscribers){
    var list = currSubscribers[key]
    for (var i=0; i< list.length; i++){
      if (list[i]==id)
        list.splice(i, 1)
    }
  }
  call.write({
   
  })
}

function print(call, callback){
  console.log('Catched print')
  callback(null, {msg: 'Hi from grpc'})
}

var protoDescriptor = grpc.loadPackageDefinition(packageDefinition);
var currencyEx = protoDescriptor.currencyEx;
var server = new grpc.Server();
server.addService(currencyEx.currencyService.service, {
  addBank: addBank,
  removeBank: removeBank,
  print: print
});

server.bind(HOST + ":" + PORT, grpc.ServerCredentials.createInsecure());
server.start();
