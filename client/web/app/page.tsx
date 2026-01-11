"use client";

import { useEffect, useState } from "react";
import {
  sendEvent,
  getRecommendation,
  getCategories,
  getItemsByCategory,
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
      </div>

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
