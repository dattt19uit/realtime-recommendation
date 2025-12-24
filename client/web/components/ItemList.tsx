import { ItemDTO } from "@/lib/types";

type Props = {
  items: ItemDTO[];
  onClickItem: (itemId: string) => void | Promise<void>;
};

export default function ItemList({ items, onClickItem }: Props) {
  if (!items || items.length === 0) {
    return <p className="text-gray-500">No items</p>;
  }

  return (
    <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
      {items.map((item) => (
        <div
          key={item.itemId}
          className="border rounded p-3 cursor-pointer hover:shadow"
          onClick={() => onClickItem(item.itemId)}
        >
          <div className="font-semibold">Item #{item.itemId}</div>
          <div className="text-sm text-gray-600">
            Category: {item.categoryId}
          </div>
          <div className="text-sm">
            {item.available === "1" ? "✅ Available" : "❌ Out of stock"}
          </div>
        </div>
      ))}
    </div>
  );
}
