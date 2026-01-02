import { useState, useMemo } from "react";
import { ItemDTO } from "@/lib/types";

type Props = {
  items: ItemDTO[];
  onClickItem: (itemId: string) => void | Promise<void>;
  maxItemsPerPage?: number;
};

export default function ItemList({
  items,
  onClickItem,
  maxItemsPerPage = 12,
}: Props) {
  const [currentPage, setCurrentPage] = useState(1);

  const totalPages = Math.ceil(items.length / maxItemsPerPage);

  const paginatedItems = useMemo(() => {
    const start = (currentPage - 1) * maxItemsPerPage;
    const end = start + maxItemsPerPage;
    return items.slice(start, end);
  }, [items, currentPage, maxItemsPerPage]);

  if (!items || items.length === 0) {
    return <p className="text-gray-500">No items</p>;
  }

  return (
    <div className="space-y-4">
      {/* ITEM GRID */}
      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {paginatedItems.map((item) => (
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

      {/* PAGINATION */}
      {totalPages > 1 && (
        <div className="flex justify-center items-center gap-4">
          <button
            className="px-3 py-1 border rounded disabled:opacity-50"
            disabled={currentPage === 1}
            onClick={() => setCurrentPage((p) => p - 1)}
          >
            Prev
          </button>

          <span className="text-sm">
            Page {currentPage} / {totalPages}
          </span>

          <button
            className="px-3 py-1 border rounded disabled:opacity-50"
            disabled={currentPage === totalPages}
            onClick={() => setCurrentPage((p) => p + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
