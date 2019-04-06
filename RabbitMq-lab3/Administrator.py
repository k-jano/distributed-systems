import pika

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
    print(" [x] Received %r" % body.decode("utf-8"))


channel.basic_consume(queue=adm_routing_key, on_message_callback=on_request)
channel.start_consuming()

#print(" [*] Awaiting for request")
#while True:
#    input(" [!] Input sth to send broadcast \n")
#    body = 'Message from adm'
#    channel.basic_publish(exchange=exchange, routing_key=user_routing_key, body=body)
