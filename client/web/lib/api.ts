import { RecommendationResponse } from "./types";
import categories from "@/data/categories.json";
import items from "@/data/items.json";
import { ItemDTO } from "@/lib/types";

const API_BASE = "http://localhost:8080";

export async function sendEvent(payload: {
  userId: string;
  itemId: string;
  eventType: string;
}) {
  await fetch(`${API_BASE}/event`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}

export async function getRecommendation(
  userId: string
): Promise<RecommendationResponse> {
  const res = await fetch(`${API_BASE}/api/recommend/${userId}`, {
    cache: "no-store",
  });

  if (!res.ok) {
    throw new Error(`HTTP error! status: ${res.status}`);
  }

  return res.json();
}

export async function getCategories() {
  return categories;
}

export async function getItemsByCategory(
  categoryId: string
): Promise<ItemDTO[]> {
  return items
    .filter((i) => i.categoryId === categoryId)
    .map((i) => ({
      itemId: i.itemId,
      categoryId: i.categoryId,
      categoryPath: [i.categoryId], // 👈 FIX QUAN TRỌNG
      available: i.available,
    }));
}
