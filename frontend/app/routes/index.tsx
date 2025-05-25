import LoginForm from "~/components/auth/login-form";

export default function LoginPage() {
  return (
    <div className="min-h-screen bg-gray-100 flex flex-col justify-center items-center px-4">
      <div className="max-w-md w-full bg-white rounded-lg shadow-md overflow-hidden animate-fade-in">
        <div className="p-6 text-center">
          <h2 className="text-2xl font-bold text-black">
            Military Asset Management
          </h2>
          <p className="text-black">Login to your account</p>
        </div>

        <div className="p-6">
          <LoginForm />
          <div className="mt-4 text-center text-sm text-gray-600">
            <p>Demo credentials:</p>
            <p>Admin: admin / admin</p>
            <p>Commander: commander / commander</p>
            <p>Logistics: logistics / logistics</p>
          </div>
        </div>
      </div>
    </div>
  );
}
