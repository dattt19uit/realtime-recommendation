type Props = {
  userId: string;
  onClickItem: (itemId: string) => void;
};

const items = ["6586", "12345", "190070", "343256", "158666"];

export default function ItemList({ onClickItem }: Props) {
  return (
    <div className="mb-6">
      <h2 className="font-semibold">Items</h2>
      <div className="flex gap-2 mt-2">
        {items.map((id) => (
          <button
            key={id}
            className="border px-3 py-1"
            onClick={() => onClickItem(id)}
          >
            Item {id}
          </button>
        ))}
      </div>
    </div>
  );
}
