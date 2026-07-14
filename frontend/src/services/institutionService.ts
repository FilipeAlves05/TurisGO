import { api } from "../lib/api";
import type { Institution } from "../types";

export const institutionService = {
  list: () => api.get<Institution[]>("/institutions").then((r) => r.data),
  get: (id: number) => api.get<Institution>(`/institutions/${id}`).then((r) => r.data),
  create: (payload: Institution) => api.post<string>("/institutions", payload).then((r) => r.data)
};
