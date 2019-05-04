slice2py ..\ICE\iceIn\ClientBank.ice --output-dir ..\ICE\iceOut

python -m grpc_tools.protoc -I../Proto/protoIn --python_out=../Proto/protoOut/python --grpc_python_out=../Proto/protoOut/grpc ../Proto/protoIn/currencyExchange.proto