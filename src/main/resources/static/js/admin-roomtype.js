(function () {
    const form = document.getElementById("roomTypeForm");
    if (!form) return;

    const msg = document.getElementById("formMsg");

    function showMsg(text, type) {
        if (!msg) return;
        msg.className = "alert mt-3 " + (type === "ok" ? "alert-success" : "alert-danger");
        msg.textContent = text;
        msg.style.display = "block";
    }

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const token = localStorage.getItem("jwt");
        if (!token) {
            const next = encodeURIComponent(window.location.pathname + window.location.search);
            window.location.href = "/login?next=" + next;
            return;
        }

        const mode = form.dataset.mode;
        const rtId = form.dataset.rtId;

        const name = document.getElementById("name")?.value?.trim() || "";
        const description = document.getElementById("description")?.value?.trim() || "";
        const imagePath = document.getElementById("imagePath")?.value?.trim() || "";

        const params = new URLSearchParams();
        params.append("name", name);
        params.append("description", description);
        params.append("imagePath", imagePath);

        if (mode === "create") {
            const typeCode = document.getElementById("typeCode")?.value;
            params.append("typeCode", typeCode);
        }

        const url = mode === "create" ? "/admin/roomtypes" : `/admin/roomtypes/${rtId}`;

        try {
            const res = await fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
                    "Authorization": "Bearer " + token
                },
                body: params.toString()
            });

            if (res.status === 401 || res.status === 403) {
                const next = encodeURIComponent(window.location.pathname + window.location.search);
                window.location.href = "/login?next=" + next;
                return;
            }

            if (!res.ok) {
                const txt = await res.text();
                showMsg("Save failed: " + txt, "err");
                return;
            }

            // succes → mergem înapoi la listă
            window.location.href = "/rooms";
        } catch (err) {
            showMsg("Network error: " + err, "err");
        }
    });
})();
