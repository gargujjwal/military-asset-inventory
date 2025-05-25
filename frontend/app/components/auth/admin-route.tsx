import { Navigate, Outlet } from "react-router";
import { useAuthenticatedUser } from "~/context/auth-context";

export default function AdminRoute() {
  const {
    user: { role },
  } = useAuthenticatedUser();

  if (role === "ADMIN") return <Outlet />;
  return <Navigate to="/dashboard" />;
}
