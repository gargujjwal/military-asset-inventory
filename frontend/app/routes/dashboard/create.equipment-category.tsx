import CreateEquipmentCategoryForm from "~/components/forms/create-equipment-category";
import AllEquipmentCategoriesTable from "~/components/tables/all-equipment-categories-table";

export default function CreateEquipmentCategoryPage() {
  return (
    <div className="space-y-8">
      <div className="max-w-2xl mx-auto animate-fade-in">
        <div className="flex items-center mb-6">
          <h1 className="text-3xl font-bold">Create Equipment Category</h1>
        </div>

        <div className="bg-white shadow-md rounded-lg p-6">
          <CreateEquipmentCategoryForm />
        </div>
      </div>

      <div className="max-w-3xl mx-auto animate-fade-in">
        <div className="flex items-center mb-6">
          <h2 className="text-2xl font-bold">Existing Equipment Category</h2>
        </div>

        <div className="bg-white shadow-md rounded-lg p-6">
          <AllEquipmentCategoriesTable />
        </div>
      </div>
    </div>
  );
}
