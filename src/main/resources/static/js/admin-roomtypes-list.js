(function () {
    const msg = document.getElementById("liveMsg");

    function showMsg(text, kind) {
        if (!msg) return;
        msg.className = "alert mt-2 " + (kind === "ok" ? "alert-success" : "alert-danger");
        msg.textContent = text;
        msg.style.display = "block";
    }

    function ensureTokenOrRedirect() {
        const token = localStorage.getItem("jwt");
        if (!token) {
            const next = encodeURIComponent(window.location.pathname + window.location.search);
            window.location.href = "/login?next=" + next;
            return null;
        }
        return token;
    }

    async function deleteRoomType(id) {
        const token = ensureTokenOrRedirect();
        if (!token) return;
        if (!confirm("Delete this room type?")) return;

        try {
            const res = await fetch(`/admin/roomtypes/delete/${id}`, {
                method: "POST",
                headers: { "Authorization": "Bearer " + token }
            });

            if (res.status === 401 || res.status === 403) {
                const next = encodeURIComponent(window.location.pathname + window.location.search);
                window.location.href = "/login?next=" + next;
                return;
            }

            if (!res.ok) {
                const txt = await res.text();
                showMsg("Delete failed: " + txt, "err");
                return;
            }

            window.location.reload();
        } catch (e) {
            showMsg("Network error: " + e, "err");
        }
    }

    document.addEventListener("click", function (e) {
        const btn = e.target.closest(".js-delete-roomtype");
        if (!btn) return;
        const id = btn.getAttribute("data-type-id");
        if (id) deleteRoomType(id);
    });
})();
