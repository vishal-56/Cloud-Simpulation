document.addEventListener("DOMContentLoaded", () => {
    const runButton = document.getElementById("runSimulation");
    const outputBody = document.getElementById("outputBody");

    runButton.addEventListener("click", async (event) => {
        event.preventDefault(); // Prevent any default form submission

        try {
            const response = await fetch("http://localhost:8080", {
                method: "POST",
                body: "START_SIMULATION",
            });

            if (!response.ok) throw new Error("Network response was not OK");

            const data = await response.json();
            localStorage.setItem("simulationData", JSON.stringify(data)); // Save data in browser storage
            populateTable(data);
        } catch (error) {
            console.error("Fetch error:", error);
        }
    });

    function populateTable(data) {
        outputBody.innerHTML = ""; // Clear existing data

        data.forEach(item => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${item.cloudletId ?? "N/A"}</td>
                <td>${item.vmId ?? "N/A"}</td>
                <td>${item.status ?? "N/A"}</td>
            `;
            outputBody.appendChild(row);
        });
    }

    // Load saved data when the page is reloaded
    const savedData = localStorage.getItem("simulationData");
    if (savedData) {
        populateTable(JSON.parse(savedData));
    }
});
