document.addEventListener("DOMContentLoaded", function () {
    console.log("[login.js] DOM ready");

    const form = document.getElementById("loginFormSubmit");
    if (!form) {
        console.error("[login.js] #loginFormSubmit not found");
        return;
    }

    form.addEventListener("submit", async function(e) {
        e.preventDefault();
        console.log("[login.js] submit intercepted");

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;

        const errBox = document.getElementById("loginError");
        if (errBox) errBox.style.display = "none";

        try {
            const response = await fetch("/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({ username, password })
            });

            if (!response.ok) {
                if (errBox) errBox.style.display = "block";
                console.warn("[login.js] /auth/login failed with status", response.status);
                return;
            }

            const data = await response.json();
            localStorage.setItem("jwt", data.token);

            const params = new URLSearchParams(window.location.search);
            let dest = params.get("redirect") || params.get("next") || "/";
            if (!dest.startsWith("/")) dest = "/";
            window.location.href = dest;
        } catch (err) {
            console.error("[login.js] error", err);
            if (errBox) errBox.style.display = "block";
        }
    });
});
