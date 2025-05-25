import CreateEquipmentForm from "~/components/forms/create-equipment";
import AllEquipmentTable from "~/components/tables/all-equipment-table";

export default function CreateEquipmentPage() {
  return (
    <div className="space-y-8">
      <div className="max-w-2xl mx-auto animate-fade-in">
        <div className="flex items-center mb-6">
          <h1 className="text-3xl font-bold">Create Equipment</h1>
        </div>

        <div className="bg-white shadow-md rounded-lg p-6">
          <CreateEquipmentForm />
        </div>
      </div>

      <div className="max-w-3xl mx-auto animate-fade-in">
        <div className="flex items-center mb-6">
          <h2 className="text-2xl font-bold">Existing Equipment</h2>
        </div>

        <div className="bg-white shadow-md rounded-lg p-6">
          <AllEquipmentTable />
        </div>
      </div>
    </div>
  );
}

export const CreateEquipmentCategoryPage = () => {};
