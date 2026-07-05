import axios from "axios";

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const status = error.response?.status;
    const data = error.response?.data;

    if (typeof data?.message === "string") {
      return data.message;
    }

    if (typeof data === "string") {
      return data;
    }

    if (status === 400) {
      return "Bad request.";
    }

    if (status === 401) {
      return "Invalid username or password.";
    }

    if (status === 403) {
      return "You do not have permission.";
    }

    if (status === 404) {
      return "Resource not found.";
    }

    if (status && status >= 500) {
      return "Server error.";
    }

    return "Request failed.";
  }

  return "Unexpected error.";
}
__________________________
//api

export async function loginUser(
    data: LoginRequest
  ): Promise<LoginResponse> {
    try {
      const response = await api.post<LoginResponse>("/auth/login", data);
  
      return response.data;
    } catch (error) {
      throw new Error(getErrorMessage(error));
    }
  }


//comp

try {
    const result = await loginUser({
      username: form.username,
      password: form.password,
    });
  
    console.log(result.token);
  } catch (error) {
    if (error instanceof Error) {
      setError(error.message);
    } else {
      setError("Unexpected error.");
    }
  }




  import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});
