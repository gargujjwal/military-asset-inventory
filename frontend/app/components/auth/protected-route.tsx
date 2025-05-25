import { Navigate, Outlet } from "react-router";
import { useAuth } from "~/context/auth-context";
import Loading from "../ui/loading";
import Navbar from "../ui/navbar/navbar";
import Footer from "../ui/footer";

export default function ProtectedRoute() {
  const { status } = useAuth();

  switch (status) {
    case "loading":
      return <Loading />;
    case "unauthenticated":
      return <Navigate to="/" replace />;
    case "authenticated":
      return (
        <div className="min-h-screen w-full flex flex-col justify-center">
          <Navbar />
          <main className="p-6 my-8 flex-grow">
            <Outlet />
          </main>
          <Footer />
        </div>
      );
  }
}
