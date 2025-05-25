import {
  MutationCache,
  QueryCache,
  QueryClient,
  QueryClientProvider,
} from "@tanstack/react-query";
import { useState } from "react";
import { useNavigate } from "react-router";
import type { ChildrenProps } from "~/types/react-related";
import { ensureError, RefreshAuthError } from "~/utils/error";

export function TanstackQueryProvider({ children }: ChildrenProps) {
  const navigate = useNavigate();
  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 1000 * 60 * 5, // 10 minutes
          },
        },
        queryCache: new QueryCache({
          onError(error) {
            const err = ensureError(error);
            console.error("Error in api call", err.message);
            console.dir(err);
            if (err instanceof RefreshAuthError) {
              navigate("/");
            }
          },
        }),
        mutationCache: new MutationCache({
          onError(error) {
            const err = ensureError(error);
            console.error("Error in api call", err.message);
            console.dir(err);
            if (err instanceof RefreshAuthError) {
              navigate("/");
            }
          },
        }),
      })
  );
  return (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}
