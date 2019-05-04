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

var currValues = {
  EUR: 4.2,
  USD: 3.8,
  CHF: 3.7,
  GBP: 4.9
};

var currSubscribers = {}

var subscriber= []


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
  console.log("Adding subscriber")
  var curs = call.request.curs;
  //addSubsriber(id, curs);
  for (var cur in curs) {
    call.write({
      curs: cur,
      val: currValues.get(cur)
    });
  }
}

function update(){
  for (var key in currValues){
    sign = Math.random()*2
    sign>1 ? sign=1 : sign=0
    
    currentValue = currValues[key]
    difference = 0.02*currentValue
    sign ? currentValue+= difference: currentValue -= difference
    //console.log(currentValue.toFixed(2) + ' ' + key)
    currValues[key]=currentValue
  }
  //console.log()
  subscriber.forEach(sub =>
    sub.write({
      msg: 'From update' + val
    })
  )
  setTimeout(update, 5000)
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
update();