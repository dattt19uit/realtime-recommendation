import { CategoryDTO } from "@/lib/types";

type Props = {
  categories: CategoryDTO[];
  selected?: CategoryDTO | null;
  onSelect: (category: CategoryDTO) => void;
};

export default function CategoryList({
  categories,
  selected,
  onSelect,
}: Props) {
  if (!categories.length) {
    return (
      <div className="text-gray-400 italic text-sm">
        No categories available
      </div>
    );
  }

  return (
    <div className="space-y-2">
      {categories.map((cat) => {
        const isActive = selected?.id === cat.id;

        return (
          <button
            key={cat.id}
            onClick={() => !isActive && onSelect(cat)}
            className={`w-full text-left p-2 rounded transition
              ${
                isActive
                  ? "bg-blue-100 text-blue-700 font-semibold cursor-default"
                  : "hover:bg-gray-100"
              }`}
          >
            📁 {cat.name}
          </button>
        );
      })}
    </div>
  );
}
