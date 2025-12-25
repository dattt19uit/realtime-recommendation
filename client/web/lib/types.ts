export type ItemDTO = {
  itemId: string;
  categoryId: string;
  categoryPath: string[];
  available: string; // backend đang trả string "0"/"1"
};

export type RecommendationResponse = {
  userId: string;
  recentItems: ItemDTO[];
  recommendedItems: ItemDTO[];
};

export type CategoryDTO = {
  id: string;
  name: string;
};
