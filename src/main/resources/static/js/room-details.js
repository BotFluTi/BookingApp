(() => {
    const modal = document.getElementById('reserveModal');
    if (!modal) return;

    const rangeInput   = modal.querySelector('#dateRange');
    const checkInH     = modal.querySelector('#checkInHidden');
    const checkOutH    = modal.querySelector('#checkOutHidden');
    const submitBtn    = modal.querySelector('button[type="submit"]');

    let fp;
    let disabledRanges = [];

    const pad = n => String(n).padStart(2, '0');
    const ymd = d => `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}`;
    const parseYMD = s => { const [Y,M,D]=s.split('-').map(Number); return new Date(Y, M-1, D); };

    const apiToDisable = (arr) => arr.map(r => ({
        from: r.start,
        to: ymd(new Date(parseYMD(r.end).getTime() - 24*60*60*1000))
    }));

    modal.addEventListener('show.bs.modal', async (e) => {
        const btn = e.relatedTarget;
        if (!btn) return;

        modal.querySelector('#rmNum').value = btn.getAttribute('data-room-number');
        modal.querySelector('#rmId').value  = btn.getAttribute('data-room-id');

        rangeInput.value = '';
        checkInH.value = '';
        checkOutH.value = '';
        submitBtn.disabled = true;

        try {
            const roomId = btn.getAttribute('data-room-id');
            const res = await fetch(`/api/rooms/${roomId}/disabled-dates`, { headers: { 'Accept':'application/json' }});
            disabledRanges = await res.json(); // [{start,end(exclusive)}]
        } catch { disabledRanges = []; }

        if (fp) { fp.destroy(); }

        fp = flatpickr(rangeInput, {
            mode: 'range',
            minDate: 'today',
            dateFormat: 'Y-m-d',
            disable: apiToDisable(disabledRanges),
            onChange: (selectedDates) => {
                if (selectedDates.length === 2) {
                    const ci = selectedDates[0];
                    const co = selectedDates[1];

                    checkInH.value  = ymd(ci);
                    checkOutH.value = ymd(co);

                    submitBtn.disabled = false;
                } else {
                    checkInH.value = '';
                    checkOutH.value = '';
                    submitBtn.disabled = true;
                }
            }
        });
    });

    modal.addEventListener('hidden.bs.modal', () => {
        if (fp) { fp.clear(); submitBtn.disabled = true; }
    });
})();
