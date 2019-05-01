var PROTO_PATH = __dirname + "/../Proto/currencyExchange.proto";
var grpc = require("grpc");
var protoLoader = require("@grpc/proto-loader");

const HOST = "127.0.0.1";
const PORT = "50051";
var packageDefinition = protoLoader.loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true
});

var mapCurr = {
  EUR: 4,
  USD: 3,
  CHF: 4,
  GBP: 5
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

function removeBank(call) {}

var protoDescriptor = grpc.loadPackageDefinition(packageDefinition);
var routeguide = protoDescriptor.currencyEx;
var server = new grpc.Server();
server.addService(routeguide.currencyService.service, {
  addBank: addBank,
  removeBank: removeBank
});

server.bind(HOST + ":" + PORT, grpc.ServerCredentials.createInsecure());
server.start();
