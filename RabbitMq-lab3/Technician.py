import pika
import sys
import random

# Var declarations
exchange = 'hospital'
exchange2 = 'administration'
adm_routing_key = 'admin'
user_routing_key = 'user'
specializations = ['knee', 'elbow', 'hip']

# Exchange, channels, topics declarations
connection = pika.BlockingConnection(
    pika.ConnectionParameters(host='localhost'))

channel = connection.channel()

channel.exchange_declare(exchange=exchange, exchange_type='topic')
channel.exchange_declare(exchange=exchange2, exchange_type='direct')

for specialization in specializations:
    channel.queue_declare(specialization)

channel.queue_declare(user_routing_key)

binding_key1 = input("Enter Bind Key1 \n")
binding_key2 = input("Enter Bind Key2 \n")

binding_keys = [binding_key1, binding_key2]

for binding_key in binding_keys:
    if binding_key not in specializations:
        sys.exit(1)


for binding_key in binding_keys:
    channel.queue_bind(exchange=exchange, queue=binding_key, routing_key=binding_key)


def on_request(ch, method, props, body):
    print(" [x] Received %r: %r" % (method.routing_key, body.decode("utf-8")))

    response = "Patient " + body.decode("utf-8") + " Results: " + str(random.randrange(1,5))
    ch.basic_publish(exchange=exchange,
                     routing_key=props.reply_to,
                     properties=pika.BasicProperties(correlation_id=props.correlation_id),
                     body=str(response))
    ch.basic_publish(exchange=exchange2, routing_key=adm_routing_key, body=str(response))
    ch.basic_ack(delivery_tag=method.delivery_tag)


def on_adm_response(ch, method, props, body):
    print(' [!] %r' % body.decode("utf-8"))


channel.basic_qos(prefetch_count=1)
for binding_key in binding_keys:
    channel.basic_consume(queue=binding_key, on_message_callback=on_request)
channel.basic_consume(queue=user_routing_key, on_message_callback=on_adm_response, auto_ack=True)
print(" [*] Awaiting for request")
channel.start_consuming()
