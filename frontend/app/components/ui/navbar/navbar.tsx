import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Link, useNavigate } from "react-router";
import { useAuthenticatedUser } from "~/context/auth-context";
import { logoutMutation } from "~/lib/tanstack-query";

const adminNavbarMenus = [
  {
    label: "Create Entities",
    subMenu: [
      { label: "Create Base", url: "/dashboard/create/base" },
      {
        label: "Create Equipment Category",
        url: "/dashboard/create/equipment-category",
      },
      { label: "Create Equipment", url: "/dashboard/create/equipment" },
      { label: "Create Personnel", url: "/dashboard/create/personnel" },
      {
        label: "Assign Personnel to Base",
        url: "/dashboard/create/personnel-assignment",
      },
    ],
  },
  {
    label: "Record Transactions",
    subMenu: [
      {
        label: "Transfer Transaction",
        url: "/dashboard/create/transaction/transfer",
      },
      {
        label: "Expenditure Transaction",
        url: "/dashboard/create/transaction/expenditure",
      },
      {
        label: "Assignment Transaction",
        url: "/dashboard/create/transaction/assignment",
      },
      {
        label: "Purchase Transaction",
        url: "/dashboard/create/transaction/purchase",
      },
    ],
  },
] as const;

const commanderLogisticsNavbarMenus = [
  {
    label: "Record Transactions",
    subMenu: [
      {
        label: "Transfer Transaction",
        url: "/dashboard/create/transaction/transfer",
      },
      {
        label: "Expenditure Transaction",
        url: "/dashboard/create/transaction/expenditure",
      },
      {
        label: "Assignment Transaction",
        url: "/dashboard/create/transaction/assignment",
      },
      {
        label: "Purchase Transaction",
        url: "/dashboard/create/transaction/purchase",
      },
    ],
  },
] as const;

export default function Navbar() {
  const auth = useAuthenticatedUser();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const logoutUserMutation = useMutation({
    ...logoutMutation,
    onSuccess: () => {
      if (auth.status === "authenticated") {
        auth.update({ status: "unauthenticated" });
      }

      queryClient.invalidateQueries({
        queryKey: logoutMutation.invalidateKeys,
      });
      navigate("/");
    },
  });
  return (
    <div className="navbar bg-base-100 shadow-sm">
      <div className="navbar-start">
        <div className="dropdown">
          <div tabIndex={0} role="button" className="btn btn-ghost lg:hidden">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              {" "}
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M4 6h16M4 12h8m-8 6h16"
              />{" "}
            </svg>
          </div>
          <ul
            tabIndex={0}
            className="menu menu-sm dropdown-content bg-base-100 rounded-box z-1 mt-3 w-52 p-2 shadow"
          >
            {auth.user.role === "ADMIN" &&
              adminNavbarMenus.map((menu, i) => (
                <li key={i}>
                  <p>{menu.label}</p>
                  <ul className="p-2">
                    {menu.subMenu.map((subMenu, j) => (
                      <li key={j}>
                        <Link to={subMenu.url}>{subMenu.label}</Link>
                      </li>
                    ))}
                  </ul>
                </li>
              ))}
            {auth.user.role !== "ADMIN" &&
              commanderLogisticsNavbarMenus.map((menu, i) => (
                <li key={i}>
                  <p>{menu.label}</p>
                  <ul className="p-2">
                    {menu.subMenu.map((subMenu, j) => (
                      <li key={j}>
                        <Link to={subMenu.url}>{subMenu.label}</Link>
                      </li>
                    ))}
                  </ul>
                </li>
              ))}
          </ul>
        </div>
        <Link className="btn btn-ghost text-xl" to="/dashboard">
          Welcome, {auth.user.fullName}
        </Link>
      </div>
      <div className="navbar-center hidden lg:flex">
        <ul className="menu menu-horizontal px-1">
          {auth.user.role === "ADMIN" &&
            adminNavbarMenus.map((menu, i) => (
              <li key={i}>
                <details>
                  <summary>{menu.label}</summary>
                  <ul className="p-2">
                    {menu.subMenu.map((subMenu, j) => (
                      <li key={j}>
                        <Link to={subMenu.url}>{subMenu.label}</Link>
                      </li>
                    ))}
                  </ul>
                </details>
              </li>
            ))}
          {auth.user.role !== "ADMIN" &&
            commanderLogisticsNavbarMenus.map((menu, i) => (
              <li key={i}>
                <details>
                  <summary>{menu.label}</summary>
                  <ul className="p-2">
                    {menu.subMenu.map((subMenu, j) => (
                      <li key={j}>
                        <Link to={subMenu.url}>{subMenu.label}</Link>
                      </li>
                    ))}
                  </ul>
                </details>
              </li>
            ))}
        </ul>
      </div>
      <div className="navbar-end">
        <button
          className="btn"
          disabled={logoutUserMutation.isPending}
          onClick={() => logoutUserMutation.mutate()}
        >
          {logoutUserMutation.isPending && (
            <span className="loading loading-spinner"></span>
          )}
          Logout
        </button>
      </div>
    </div>
  );
}
