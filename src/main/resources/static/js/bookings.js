(function () {
    const msgEl = document.getElementById("liveMsg");

    function showMsg(text, kind) {
        if (!msgEl) return;
        msgEl.className = "alert mt-2 " + (kind === "ok" ? "alert-success" : "alert-danger");
        msgEl.setAttribute("role", "alert");
        msgEl.textContent = text;
        msgEl.style.display = "block";
    }

    function tokenOrRedirect() {
        const t = localStorage.getItem("jwt");
        if (!t) {
            const next = encodeURIComponent(location.pathname + location.search);
            location.href = "/login?next=" + next;
            return null;
        }
        return t;
    }

    function markRowCancelled(id) {
        const row =
            document.querySelector(`[data-booking-item="${id}"]`) ||
            document.querySelector(`.js-cancel-booking[data-booking-id="${id}"]`)?.closest("tr");
        if (!row) return;

        const badge = row.querySelector(".badge");
        if (badge) {
            badge.classList.remove("bg-success");
            badge.classList.add("bg-secondary");
            badge.textContent = "CANCELLED";
        }

        row.querySelectorAll(".js-cancel-booking").forEach(btn => {
            btn.remove();
        });
    }


    document.addEventListener("click", async (e) => {
        const btn = e.target.closest(".js-cancel-booking");
        if (!btn) return;

        e.preventDefault();

        const id = btn.getAttribute("data-booking-id");
        const scope = btn.getAttribute("data-scope") || "user"; // "user" | "admin"
        if (!id) return;
        if (!confirm("Cancel this booking?")) return;

        const token = tokenOrRedirect();
        if (!token) return;

        const url = scope === "admin"
            ? `/admin/bookings/${id}/cancel`
            : `/bookings/${id}/cancel`;

        try {
            const res = await fetch(url, {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + token,
                    "X-Requested-With": "XMLHttpRequest"
                }
            });

            if (res.status === 401 || res.status === 403) {
                const next = encodeURIComponent(location.pathname + location.search);
                location.href = "/login?next=" + next;
                return;
            }

            if (!res.ok) {
                const txt = await res.text();
                showMsg("Cancel failed: " + txt, "err");
                return;
            }

            markRowCancelled(id);
            showMsg("Booking cancelled.", "ok");
        } catch (err) {
            showMsg("Network error: " + err, "err");
        }
    });
})();
