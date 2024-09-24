from time import sleep
from kafka import KafkaProducer
import random
from datetime import datetime

producer = None

try:
    producer = KafkaProducer(bootstrap_servers='localhost:9092')

    while True:
        temperature = round(random.uniform(-20, 40), 2)
        humidity = round(random.uniform(30, 90), 2)
        timestamp = datetime.now().isoformat()  
        data = f'{timestamp},{temperature},{humidity}'  
        producer.send('real-time-data', data.encode())
        sleep(15)
        print(data)

except Exception as e:
    print(f"Error: {e}")
finally:
    if producer is not None:
        producer.close()



