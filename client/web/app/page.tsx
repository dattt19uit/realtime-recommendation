"use client";

import { useEffect, useState } from "react";
import {
  sendEvent,
  getRecommendation,
  getCategories,
  getItemsByCategory,
  getItemById,
} from "@/lib/api";
import { CategoryDTO, ItemDTO } from "@/lib/types";
import ItemList from "@/components/ItemList";
import Recommendation from "@/components/Recommendation";
import CategoryList from "@/components/CategoryList";

export default function Home() {
  const [userId, setUserId] = useState("544007");

  const [recentItems, setRecentItems] = useState<ItemDTO[]>([]);
  const [recommendedItems, setRecommendedItems] = useState<ItemDTO[]>([]);

  const [categories, setCategories] = useState<CategoryDTO[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<CategoryDTO | null>(
    null
  );
  const [items, setItems] = useState<ItemDTO[]>([]);

  // Search state
  const [searchId, setSearchId] = useState("");
  const [searchResult, setSearchResult] = useState<ItemDTO | null>(null);
  const [searchError, setSearchError] = useState("");

  useEffect(() => {
    const id = setInterval(loadRecommend, 1000);
    return () => clearInterval(id);
  }, [userId]);

  /** Load categories */
  useEffect(() => {
    async function init() {
      const cats = await getCategories(); // CategoryDTO[]
      setCategories(cats);

      if (cats.length > 0) {
        setSelectedCategory(cats[0]);
      }
    }
    init();
  }, []);

  /** Load items when category changes */
  useEffect(() => {
    if (!selectedCategory) return;

    async function loadItems() {
      const data = await getItemsByCategory(selectedCategory!.id);
      setItems(data);
    }

    loadItems();
  }, [selectedCategory]);

  /** Load recommendation */
  const loadRecommend = async () => {
    const data = await getRecommendation(userId);
    setRecentItems(data.recentItems || []);
    setRecommendedItems(data.recommendedItems || []);
  };

  /** Click item → send event → refresh recommendation */
  const handleClickItem = async (itemId: string) => {
    await sendEvent({ userId, itemId, eventType: "view" });

    setTimeout(() => {
      loadRecommend();
    }, 100); // 300–500ms là đẹp
  };

  /** Search item by id */
  const handleSearch = async () => {
    const id = searchId.trim();
    if (!id) return;

    // Optional: only allow digits
    if (!/^[0-9]+$/.test(id)) {
      setSearchError("Please enter a numeric item id");
      setSearchResult(null);
      return;
    }

    const res = await getItemById(id);
    if (!res) {
      setSearchError("Item not found");
      setSearchResult(null);
    } else {
      setSearchResult(res);
      setSearchError("");
    }
  };

  return (
    <main className="min-h-screen bg-gray-100 p-8">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-800">
          🔥 Realtime Recommendation System
        </h1>
        <p className="text-gray-600">
          Kappa Architecture • Kafka • Flink • Redis • Spring Boot
        </p>
      </div>

      {/* User Control */}
      <div className="bg-white p-4 rounded shadow mb-6 flex items-center gap-4">
        <label className="font-semibold">User ID</label>
        <input
          className="border rounded px-3 py-1 w-40"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
        />
        <button
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          onClick={loadRecommend}
        >
          Load Recommendation
        </button>

        {/* Item search */}
        <div className="ml-4 flex items-center gap-2">
          <input
            className="border rounded px-3 py-1 w-40"
            placeholder="Search Item ID"
            value={searchId}
            onChange={(e) => setSearchId(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
          />
          <button
            className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
            onClick={handleSearch}
          >
            Search
          </button>
          <button
            className="px-2 py-1 border rounded"
            onClick={() => {
              setSearchId("");
              setSearchResult(null);
              setSearchError("");
            }}
          >
            Clear
          </button>
        </div>
      </div>

      {/* Search result */}
      {searchResult ? (
        <div className="bg-white rounded shadow p-4 mb-6">
          <h3 className="font-semibold mb-2">Search Result</h3>
          <div
            className="border rounded p-3 cursor-pointer hover:shadow"
            role="button"
            onClick={() => handleClickItem(searchResult.itemId)}
            title="Click to view item"
          >
            <div className="font-semibold">Item #{searchResult.itemId}</div>
            <div className="text-sm text-gray-600">
              Category: {searchResult.categoryId}
            </div>
            <div className="text-sm">
              {searchResult.available === "1"
                ? "✅ Available"
                : "❌ Out of stock"}
            </div>
            <div className="text-xs text-gray-400 mt-2">
              Click to send view event
            </div>
          </div>
        </div>
      ) : (
        searchError && (
          <p className="text-sm text-red-500 mb-6">{searchError}</p>
        )
      )}

      {/* Main Content */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        {/* Category */}
        <div className="bg-white rounded shadow p-4">
          <h2 className="text-lg font-semibold mb-4">📂 Categories</h2>
          <CategoryList
            categories={categories}
            selected={selectedCategory}
            onSelect={setSelectedCategory}
          />
        </div>

        {/* Item list */}
        <div className="md:col-span-2 bg-white rounded shadow p-4">
          <h2 className="text-xl font-semibold mb-4">
            🛒 Items in Category{" "}
            <span className="text-blue-600">
              {selectedCategory?.name ?? "-"}
            </span>
          </h2>

          <ItemList
            items={items}
            onClickItem={handleClickItem}
            maxItemsPerPage={9}
          />
        </div>

        {/* Recommendation */}
        <div className="bg-white rounded shadow p-4">
          <Recommendation
            recentItems={recentItems}
            recommendedItems={recommendedItems}
          />
        </div>
      </div>
    </main>
  );
}
