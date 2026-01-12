import { ItemDTO } from "@/lib/types";

type Props = {
  recentItems: ItemDTO[];
  recommendedItems: ItemDTO[];
};

export default function Recommendation({
  recentItems,
  recommendedItems,
}: Props) {
  return (
    <div>
      <h2 className="text-xl font-semibold mb-4">🤖 Recommendation</h2>

      <section className="mb-6">
        <h3 className="font-semibold mb-2">Recent Viewed</h3>
        {recentItems.length === 0 ? (
          <p className="text-gray-400">No activity yet</p>
        ) : (
          <ul className="space-y-2">
            {recentItems.map((item, idx) => (
              <li
                key={idx}
                className="border rounded p-2 text-sm flex justify-between"
              >
                <span>Item #{item.itemId}</span>
                <span className="text-gray-500">Cat {item.categoryId}</span>
              </li>
            ))}
          </ul>
        )}
      </section>

      <section>
        <h3 className="font-semibold mb-2">Recommended For You</h3>
        {recommendedItems.length === 0 ? (
          <p className="text-gray-400">No recommendation</p>
        ) : (
          <ul className="space-y-2">
            {recommendedItems.map((item, idx) => (
              <li
                key={idx}
                className="border rounded p-2 text-sm flex justify-between"
              >
                <span>Item #{item.itemId}</span>
                <span className="text-gray-500">Cat {item.categoryId}</span>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}
