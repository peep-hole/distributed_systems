import socket
import threading

PORT = 9009
IP_ADDRESS = "127.0.0.1"
MSG_LEN = 128
DISCONNECT_MESSAGE = "QQ"


print("Starting Chat Server")

server_socket_udp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
server_socket_udp.bind((IP_ADDRESS, PORT))

server_socket_tcp = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket_tcp.bind((IP_ADDRESS, PORT))

clients = {}  # address: [name, connection]


def send_to_all_tcp(sender_address, msg):
    for address in clients:

        if address != sender_address:

            try:
                clients[address][1].send(msg.encode('utf-8'))
            except BrokenPipeError:
                clients[address][1].close()
                clients.pop(address)


def establish_client_connection(connection, address):
    msg = connection.recv(MSG_LEN).decode("utf-8")

    clients[address] = [msg, connection]
    print(f"Connection for {msg} established successfully")

    while True:
        msg = connection.recv(MSG_LEN).decode("utf-8")

        if msg.strip() == DISCONNECT_MESSAGE:
            break

        send_to_all_tcp(address, msg)

    name = clients[address][0]
    print(f"Client: {name} disconnected")
    clients.pop(address)
    connection.close()
    send_to_all_tcp(None, "USER: " + name + " disconnected")


def udp_connection():
    while True:
        msg, udp_address = server_socket_udp.recvfrom(1024)

        for send_address in clients:

            if send_address != udp_address:
                print("UDP") # todo remove
                clients[send_address][1].sendto(msg, send_address)


def init_server():
    server_socket_tcp.listen()
    print("Waiting for client connections")

    threading.Thread(target=udp_connection, daemon=True).start()

    while True:
        connection, server_address = server_socket_tcp.accept()

        threading.Thread(target=establish_client_connection,
                         args=[connection, server_address],
                         daemon=True).start()  # daemon=True for Thread to run in background

        print("New Client Connected")


#
try:
    init_server()
except KeyboardInterrupt:

    for address in clients:
        clients[address][1].close()

    server_socket_udp.close()
    server_socket_tcp.close()
    print("Chat Server Shut Down")


# todo : add MultiCast






