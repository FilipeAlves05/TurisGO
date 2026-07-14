import { api } from "../lib/api";
import type { ToggleFavoriteResponse, TouristAttraction, Trail } from "../types";

export const favoriteService = {
  toggleAttraction: (touristId: number, attractionId: number) =>
    api.post<ToggleFavoriteResponse>(`/tourists/${touristId}/favorites/attractions/${attractionId}`).then((r) => r.data),
  listAttractions: (touristId: number) =>
    api.get<TouristAttraction[]>(`/tourists/${touristId}/favorites/attractions`).then((r) => r.data),
  toggleTrail: (touristId: number, trailId: number) =>
    api.post<ToggleFavoriteResponse>(`/tourists/${touristId}/favorites/trails/${trailId}`).then((r) => r.data),
  listTrails: (touristId: number) => api.get<Trail[]>(`/tourists/${touristId}/favorites/trails`).then((r) => r.data)
};
