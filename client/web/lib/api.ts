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

export async function getRecommendation(userId: string) {
  try {
    const res = await fetch(`${API_BASE}/api/recommend/${userId}`, {
      cache: "no-store", // nếu cần data fresh
    });
    if (!res.ok) {
      throw new Error(`HTTP error! status: ${res.status}`);
    }
    return await res.json();
  } catch (error: any) {
    console.error("Fetch error:", error); // In ra terminal server
    console.error("Cause:", error.cause); // Thường show ECONNREFUSED hoặc chi tiết
    throw error; // hoặc return data fallback
  }
}
