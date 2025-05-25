import AssignPersonnelToBaseForm from "~/components/forms/assign-personnel-to-base";

export default function CreateBaseAssignmentPage() {
  return (
    <div className="bg-gray space-y-8">
      <div className="max-w-2xl mx-auto animate-fade-in">
        <div className="flex items-center mb-6">
          <h1 className="text-3xl font-bold">Assign Personnel to Base</h1>
        </div>

        <div className="bg-white shadow-md rounded-lg p-6">
          <AssignPersonnelToBaseForm />
        </div>
      </div>
    </div>
  );
}
