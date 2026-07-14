import { api } from "../lib/api";
import type { AverageRating, Review } from "../types";

export const reviewService = {
  upsert: (payload: Review) => api.post<Review>("/reviews", payload).then((r) => r.data),
  listByAttraction: (attractionId: number) => api.get<Review[]>(`/attractions/${attractionId}/reviews`).then((r) => r.data),
  average: (attractionId: number) => api.get<AverageRating>(`/attractions/${attractionId}/reviews/average`).then((r) => r.data),
  listByTourist: (touristId: number) => api.get<Review[]>(`/tourists/${touristId}/reviews`).then((r) => r.data)
};
