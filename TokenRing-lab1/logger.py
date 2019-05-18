import socket
import struct
from datetime import datetime

MCAST_GRP = '224.2.3.4'
MCAST_PORT = 9000

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)


sock.bind((MCAST_GRP, MCAST_PORT))
mreq = struct.pack("4sl", socket.inet_aton(MCAST_GRP), socket.INADDR_ANY)

sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

print("Logger ready")

while True:
    buff, address = sock.recvfrom(1024)
    print(datetime.now().strftime('%Y-%m-%d %H:%M:%S') + ": " + str(buff, 'utf-8'))