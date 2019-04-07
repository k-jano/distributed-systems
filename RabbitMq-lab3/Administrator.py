import pika
from threading import Thread
import sys
import time

exchange = 'administration'
adm_routing_key = 'admin'
user_routing_key = 'user'
connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
channel.exchange_declare(exchange=exchange, exchange_type='direct')
channel.queue_declare(adm_routing_key)
channel.queue_declare(user_routing_key)
channel.queue_bind(exchange=exchange, queue=adm_routing_key, routing_key=adm_routing_key)


def on_request(ch, method, props, body):
    print(" [x] Received %r " % body.decode("utf-8"))
    # sys.stdout.flush()


def start_routine():
    print(" [*] Awaiting for request")
    while True:
        print(" [!] Input sth to send broadcast")
        # input(" [!] Input sth to send broadcast \n")
        body_send = sys.stdin.readline()[:-1]
        channel.basic_publish(exchange=exchange, routing_key=user_routing_key, body=body_send)


thread = Thread(target=start_routine)
thread.start()
channel.basic_consume(queue=adm_routing_key, on_message_callback=on_request, auto_ack=True)
channel.start_consuming()
#while True:
#    print("Log")
#    time.sleep(1)
