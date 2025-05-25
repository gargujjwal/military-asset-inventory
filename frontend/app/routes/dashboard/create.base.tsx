import CreateBaseForm from "~/components/forms/create-base";
import AllBasesTable from "~/components/tables/all-bases-table";

export default function CreateBasePage() {
  return (
    <div className="bg-gray space-y-8">
      <div className="max-w-2xl mx-auto animate-fade-in">
        <div className="flex items-center mb-6">
          <h1 className="text-3xl font-bold">Create New Base</h1>
        </div>

        <div className="bg-white shadow-md rounded-lg p-6">
          <CreateBaseForm />
        </div>
      </div>

      <div className="max-w-2xl mx-auto">
        <div className="flex items-center mb-6">
          <h2 className="text-2xl text-center font-bold">Existing Bases</h2>
        </div>
        <AllBasesTable />
      </div>
    </div>
  );
}
