import sys
from threading import Thread

import pika
import uuid

# Var declarations
exchange = 'hospital'
exchange2 = 'administration'
adm_routing_key = 'admin'
user_routing_key = 'user'
specializations = ['knee', 'elbow', 'hip']
response = None
corr_id = str(uuid.uuid4())


# Exchange, channels, topics declarations
connection = pika.BlockingConnection(
    pika.ConnectionParameters(host='localhost'))
channel = connection.channel()

channel.exchange_declare(exchange=exchange, exchange_type='topic')
channel.exchange_declare(exchange=exchange2, exchange_type='direct')

result = channel.queue_declare('', exclusive=True)
callback_queue = result.method.queue
result_usr = channel.queue_declare(corr_id)
# user_queue = result_usr.method.queue
channel.queue_bind(exchange=exchange, queue=callback_queue, routing_key="#")
channel.queue_bind(exchange=exchange2, queue=corr_id, routing_key=user_routing_key)


def on_response(ch, method, props, body):
    if props.correlation_id == corr_id and props.reply_to is None:
        global response
        response = body.decode("utf-8")


def on_call(rout_key, body):
    global response
    response = None
    channel.basic_publish(exchange=exchange, routing_key=rout_key,
                          properties=pika.BasicProperties(reply_to=callback_queue, correlation_id=corr_id), body=body)
    channel.basic_publish(exchange=exchange2, routing_key=adm_routing_key, body=body)
    while response is None:
        connection.process_data_events()


def on_adm_response(ch, method, props, body):
    print(' [!] Msg from adm: %r' % body.decode("utf-8"))


channel.basic_consume(queue=corr_id, on_message_callback=on_adm_response)
channel.basic_consume(queue=callback_queue, on_message_callback=on_response)
while True:
    routing_key = ''
    while routing_key not in specializations:
        print("Enter routing key")
        routing_key = sys.stdin.readline()[:-1]
    print("Enter message")
    message = sys.stdin.readline()[:-1]
    print(" [X] Requesting %r: %r" % (routing_key, message))
    on_call(routing_key, message)
    print(" [.] Received %r" % str(response))
