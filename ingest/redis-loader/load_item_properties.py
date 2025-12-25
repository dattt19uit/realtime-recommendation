import pandas as pd
import redis
from tqdm import tqdm

# Kết nối Redis
r = redis.Redis(host="localhost", port=6379, decode_responses=True)

# Tạo pipeline để tăng tốc độ ghi (batch nhiều lệnh cùng lúc)
pipeline = r.pipeline()

files = [
    "../data/item_properties_part1.csv",
    "../data/item_properties_part2.csv"
]

# Số lệnh tối đa trong một pipeline trước khi execute (có thể điều chỉnh)
BATCH_SIZE = 10000

total_processed = 0

for file in files:
    print(f"Đang xử lý file: {file}")
    df = pd.read_csv(file)

    # Chỉ giữ lại 2 property cần thiết
    df = df[df["property"].isin(["categoryid", "available"])]

    print(f"Số bản ghi sau khi lọc: {len(df)}")

    for _, row in tqdm(df.iterrows(), total=len(df), desc="Loading into Redis"):
        item_id = str(row["itemid"])
        key = f"item:{item_id}"
        prop = row["property"]
        value = str(row["value"])  # Đảm bảo value là string

        # Nếu key đã tồn tại nhưng không phải hash → xóa nó đi
        if r.exists(key):
            if r.type(key) != "hash":
                r.delete(key)

        # Thêm lệnh HSET vào pipeline
        pipeline.hset(key, prop, value)

        total_processed += 1

        # Mỗi BATCH_SIZE lệnh thì execute pipeline để ghi vào Redis
        if total_processed % BATCH_SIZE == 0:
            pipeline.execute()

    # Execute các lệnh còn lại trong pipeline sau khi vòng lặp kết thúc
    pipeline.execute()

# Reset pipeline cho lần sau (nếu cần)
pipeline.reset()

print("Item properties loaded into Redis successfully!")