import {
  index,
  layout,
  prefix,
  route,
  type RouteConfig,
} from "@react-router/dev/routes";

export default [
  // /
  index("./routes/index.tsx"),
  layout("./components/auth/protected-route.tsx", [
    route("dashboard", "./routes/dashboard/index.tsx"),

    ...prefix("dashboard/create", [
      layout("./components/auth/admin-route.tsx", [
        // /dashboard/create/base
        route("base", "./routes/dashboard/create.base.tsx"),

        // /dashboard/create/equipment-category
        route(
          "equipment-category",
          "./routes/dashboard/create.equipment-category.tsx"
        ),

        // /dashboard/create/equipment
        route("equipment", "./routes/dashboard/create.equipment.tsx"),

        // /dashboard/create/personnel
        route("personnel", "./routes/dashboard/create.personnel.tsx"),

        // /dashboard/create/personnel-assignment
        route(
          "personnel-assignment",
          "./routes/dashboard/create.base-assignment.tsx"
        ),
      ]),

      ...prefix("transaction", [
        // /dashboard/create/transaction/transfer
        route("transfer", "./routes/dashboard/create.transaction.transfer.tsx"),

        // /dashboard/create/transaction/equipment
        route(
          "assignment",
          "./routes/dashboard/create.transaction.assignment.tsx"
        ),

        // /dashboard/create/transaction/transfer
        route(
          "expenditure",
          "./routes/dashboard/create.transaction.expenditure.tsx"
        ),

        // /dashboard/create/transaction/transfer
        route("purchase", "./routes/dashboard/create.transaction.purchase.tsx"),
      ]),
    ]),
  ]),
] satisfies RouteConfig;
