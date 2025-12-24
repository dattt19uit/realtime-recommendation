import pandas as pd
import redis
from tqdm import tqdm

r = redis.Redis(host="localhost", port=6379, decode_responses=True)

df = pd.read_csv("..\data\category_tree.csv")

for _, row in tqdm(df.iterrows(), total=len(df)):
    category_id = str(row["categoryid"])
    parent_id = "" if pd.isna(row["parentid"]) else str(row["parentid"])

    r.hset(f"category:{category_id}", mapping={
        "parent": parent_id
    })

print("Category tree loaded into Redis")
