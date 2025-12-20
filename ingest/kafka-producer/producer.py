import csv
import json
import time
from kafka import KafkaProducer

producer = KafkaProducer(
    bootstrap_servers="localhost:9092",
    value_serializer=lambda v: json.dumps(v).encode("utf-8")
)

with open("..\data\events.csv", "r") as f:
    reader = csv.DictReader(f)

    for row in reader:
        event = {
            "event_time": int(row["timestamp"]),
            "user_id": row["visitorid"],
            "item_id": row["itemid"],
            "event_type": row["event"],
            "transaction_id": row["transactionid"] or None
        }

        producer.send("user-events", event)
        time.sleep(0.01)

producer.flush()
