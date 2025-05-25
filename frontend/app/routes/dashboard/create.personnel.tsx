import CreatePersonnelForm from "~/components/forms/create-personnel";
import AllUsersTable from "~/components/tables/all-users-table";

export default function CreatePersonnelPage() {
  return (
    <div className="space-y-8">
      <div className="max-w-2xl mx-auto animate-fade-in">
        <div className="flex items-center mb-6">
          <h1 className="text-3xl font-bold">Create Personnel</h1>
        </div>

        <div className="bg-white shadow-md rounded-lg p-6">
          <CreatePersonnelForm />
        </div>
      </div>

      <div className="max-w-3xl mx-auto animate-fade-in">
        <div className="flex items-center mb-6">
          <h2 className="text-2xl font-bold">All Personnel</h2>
        </div>

        <div className="bg-white shadow-md rounded-lg p-6">
          <AllUsersTable />
        </div>
      </div>
    </div>
  );
}
