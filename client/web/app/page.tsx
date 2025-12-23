"use client";

import { useState } from "react";
import { sendEvent, getRecommendation } from "@/lib/api";
import ItemList from "@/components/ItemList";
import Recommendation from "@/components/Recommendation";

export default function Home() {
  const [userId, setUserId] = useState("544007");
  const [recommendations, setRecommendations] = useState<string[]>([]);
  const [recentItems, setRecentItems] = useState<string[]>([]);

  const loadRecommend = async () => {
    const data = await getRecommendation(userId);
    setRecommendations(data.recommendations || []);
    setRecentItems(data.recentItems || []);
  };

  return (
    <main className="p-8">
      <h1 className="text-2xl font-bold mb-4">Real-time Recommendation Demo</h1>

      <div className="mb-4">
        <label>User ID:</label>
        <input
          className="border ml-2 p-1"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
        />
        <button
          className="ml-2 px-4 py-1 bg-blue-500 text-white"
          onClick={loadRecommend}
        >
          Load
        </button>
      </div>

      <ItemList
        userId={userId}
        onClickItem={(itemId) =>
          sendEvent({ userId, itemId, eventType: "view" })
        }
      />

      <Recommendation
        recentItems={recentItems}
        recommendations={recommendations}
      />
    </main>
  );
}
