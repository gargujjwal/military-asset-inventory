import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { Navigate, useNavigate } from "react-router";
import { useAuth } from "~/context/auth-context";
import { loginMutation } from "~/lib/tanstack-query";
import type { LoginRequest, ErrorResponse } from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";
import FormError from "../ui/form-error";
import Loading from "../ui/loading";

export default function LoginForm() {
  const auth = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const {
    register,
    handleSubmit,
    formState: { errors },
    clearErrors,
    setError,
  } = useForm<LoginRequest>();
  const loginUserMutation = useMutation({
    ...loginMutation,
    onSuccess: async (data) => {
      if (auth.status === "unauthenticated") {
        auth.update({ status: "authenticated", user: data.user });
      }

      queryClient.invalidateQueries({
        queryKey: loginMutation.invalidateKeys,
      });

      navigate("/dashboard");
    },
    onError: (error) => {
      if (error instanceof ApiError) {
        setError("root", error.response);
      }
    },
  });

  if (auth.status === "loading") {
    return <Loading />;
  }

  if (auth.status === "authenticated") {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <form
      onSubmit={handleSubmit((data) => {
        clearErrors();
        loginUserMutation.mutate(data);
      })}
    >
      {errors.root && (
        <FormError error={errors.root as unknown as ErrorResponse} />
      )}
      <div className="mb-4">
        <label
          htmlFor="username"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Username
        </label>
        <input
          id="username"
          type="text"
          {...register("username", { required: "Username is required" })}
          className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-military-navy/70 ${
            errors.username ? "border-red-500" : "border-gray-300"
          }`}
          placeholder="Enter your username"
        />
        {errors.username && (
          <p className="mt-1 text-sm text-red-600">{errors.username.message}</p>
        )}
      </div>

      <div className="mb-6">
        <label
          htmlFor="password"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Password
        </label>
        <input
          id="password"
          type="password"
          {...register("password", { required: "Password is required" })}
          className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-military-navy/70 ${
            errors.password ? "border-red-500" : "border-gray-300"
          }`}
          placeholder="Enter your password"
        />
        {errors.password && (
          <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
        )}
      </div>

      <button
        type="submit"
        disabled={loginUserMutation.isPending}
        className="btn btn-block"
      >
        {loginUserMutation.isPending ? (
          <span className="loading loading-ring loading-md"></span>
        ) : (
          "Login"
        )}
      </button>
    </form>
  );
}
