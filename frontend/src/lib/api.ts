import axios from "axios";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? "/api",
  withCredentials: true,
  headers: {
    "Content-Type": "application/json"
  }
});

export function getApiError(error: unknown) {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;
    if (typeof data === "string") return data;
    if (data && typeof data === "object" && "erro" in data) return String(data.erro);
    return error.message;
  }
  return "Não foi possível concluir a ação.";
}
