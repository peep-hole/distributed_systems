import socket
import select
import struct
import sys

SERVER_IP = "127.0.0.1"
PORT = 9009
MSG_LEN = 128
DISCONNECT_MESSAGE = "QQ"
UDP_PARAM = "-u"
MULTICAST_PARAM = "-m"
MULTICAST_GROUP = '224.1.1.1'
MULTICAST_PORT = 5007
MULTICAST_TTL = 2

# TCP SOCKET INIT
tcp_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
tcp_socket.connect((SERVER_IP, PORT))

# UDP SOCKET INIT
udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
udp_socket.bind(tcp_socket.getsockname())  # same socket address as tcp

# MULTICAST SOCKET INIT
multicast_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
multicast_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
multicast_socket.bind((MULTICAST_GROUP, MULTICAST_PORT))

# IT ALLOWS DATA TO BE RECEIVED CORRECTLY VIA MULTICAST
# FROM OFFICIAL PYTHON WIKI EXAMPLES https://wiki.python.org/moin/UdpCommunication
m_req = struct.pack("4sl", socket.inet_aton(MULTICAST_GROUP), socket.INADDR_ANY)
multicast_socket.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, m_req)


def init():

    name = input("LOGIN: ")
    tcp_socket.send(name.encode("utf-8"))

    try:
        while True:
            # [ready to read], [ready to write], [exceptional condition]
            readable, _, _ = select.select([tcp_socket, udp_socket, sys.stdin, multicast_socket], [], [])

            for source in readable:

                if source == tcp_socket:
                    msg = tcp_socket.recv(MSG_LEN).decode("utf-8")
                    print(msg)

                elif source == udp_socket:
                    msg, _ = udp_socket.recvfrom(1024)
                    print(msg.decode("utf-8"))

                elif source == multicast_socket:
                    msg = multicast_socket.recv(1024).decode('utf-8')
                    print(msg)

                else:
                    msg = sys.stdin.readline().strip()
                    if msg.strip() == DISCONNECT_MESSAGE:
                        tcp_socket.send(msg.strip().encode("utf-8"))
                        tcp_socket.close()
                        udp_socket.close()
                        return

                    elif UDP_PARAM in msg:
                        msg = msg.replace("-u", "")
                        udp_socket.sendto((name + " [UDP] > " + msg).strip().encode("utf-8"), (SERVER_IP, PORT))
                        continue

                    elif MULTICAST_PARAM in msg:
                        msg = msg.replace("-m", "")
                        msg = name + " [MULTICAST] > " + msg
                        multicast_socket.sendto(msg.strip().encode("utf-8"), (MULTICAST_GROUP, MULTICAST_PORT))
                        continue

                    msg = name + "> " + msg
                    tcp_socket.send(msg.encode("utf-8"))

    except KeyboardInterrupt:
        tcp_socket.send("QQ".encode("utf-8"))
        tcp_socket.close()
        udp_socket.close()


init()

