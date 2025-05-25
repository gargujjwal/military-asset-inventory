import { useQuery } from "@tanstack/react-query";
import { createContext, useContext, useEffect, useState } from "react";
import { meQuery } from "~/lib/tanstack-query";
import type { UserDto } from "~/types/backend-stubs";
import type { ChildrenProps } from "~/types/react-related";
import { BaseError } from "~/utils/error";

export type TAuthContext =
  | { status: "loading" }
  | {
      status: "authenticated";
      user: UserDto;
      update: (auth: TUpdateAuth) => void;
    }
  | {
      status: "unauthenticated";
      update: (auth: TUpdateAuth) => void;
    };

type TUpdateAuth =
  | {
      status: "authenticated";
      user: UserDto;
    }
  | {
      status: "unauthenticated";
    };

const AuthContext = createContext<TAuthContext>({ status: "loading" });

export function AuthProvider({ children }: ChildrenProps) {
  const [authState, setAuthState] = useState<TAuthContext>({
    status: "loading",
  });
  const authenticatedUser = useQuery({ ...meQuery, retry: 1 });

  function updateAuthState(auth: TUpdateAuth) {
    setAuthState({ ...auth, update: updateAuthState });
  }

  useEffect(() => {
    switch (authenticatedUser.status) {
      case "success":
        setAuthState({
          status: "authenticated",
          user: authenticatedUser.data.data,
          update: updateAuthState,
        });
        break;
      case "error":
        setAuthState({
          status: "unauthenticated",
          update: updateAuthState,
        });
    }
  }, [authenticatedUser.status]);

  return (
    <AuthContext.Provider value={authState}>{children}</AuthContext.Provider>
  );
}

export const useAuth = (): TAuthContext => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }

  return context;
};

export const useAuthenticatedUser = () => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  if (context.status === "loading" || context.status === "unauthenticated") {
    throw new BaseError(
      "useAuth can't be used with unauthenticated user or loading state",
      { context }
    );
  }

  return context;
};
