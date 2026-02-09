const LIFE_MS = 3200;
const MAX_TOASTS = 4;

function host() {
    return document.getElementById('toastHost');
}

function msgsRoot() {
    return document.getElementById('msgs');
}

function escapeHtml(str) {
    return (str || '').replace(/[&<>"']/g, (m) => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[m]));
}

function severityFromNode(node) {
    // às vezes o JSF coloca a classe no LI, às vezes em SPAN dentro.
    const el = node.classList ? node : null;
    const has = (cls) => (el && el.classList.contains(cls)) || node.querySelector?.('.' + cls);

    if (has('jsf-error') || has('jsf-fatal')) return 'error';
    if (has('jsf-warn')) return 'warn';
    if (has('jsf-info')) return 'success';
    return 'info';
}

function readMsg(li) {
    // tenta spans: [summary][detail]
    const spans = li.querySelectorAll('span');
    if (spans.length >= 2) {
        const title = (spans[0].textContent || '').trim();
        const detail = (spans[1].textContent || '').trim();
        return { title, detail };
    }

    // fallback
    const text = (li.textContent || '').trim();
    if (!text) return { title: '', detail: '' };

    // tenta "Sucesso Usuário criado."
    const parts = text.split(/\s+/);
    if (parts.length > 2 && parts[0].length <= 12) {
        return { title: parts[0], detail: parts.slice(1).join(' ') };
    }

    return { title: text, detail: '' };
}

function iconChar(type) {
    if (type === 'success') return '✓';
    if (type === 'warn') return '!';
    if (type === 'error') return '✕';
    return 'i';
}

function dismiss(el) {
    if (!el) return;
    const t = el.dataset.timer;
    if (t) clearTimeout(Number(t));

    el.classList.remove('is-in');
    el.classList.add('is-out');
    setTimeout(() => el.remove(), 180);
}

function createToast({ type, title, detail }) {
    const h = host();
    if (!h) return;

    const toast = document.createElement('div');
    toast.className = `toastx toastx-${type}`;
    toast.setAttribute('role', 'status');

    toast.innerHTML = `
  <div class="toastx-icon" aria-hidden="true">${iconChar(type)}</div>
  <div class="toastx-body">
    <div class="toastx-title">${escapeHtml(title || '')}</div>
    ${detail ? `<div class="toastx-text">${escapeHtml(detail)}</div>` : ''}
  </div>
  <button class="toastx-close" type="button" aria-label="Fechar">×</button>
`;

    toast.querySelector('.toastx-close')
        .addEventListener('click', () => dismiss(toast));

    h.appendChild(toast);

    // entrada
    requestAnimationFrame(() => toast.classList.add('is-in'));

    // auto-close
    const timer = setTimeout(() => dismiss(toast), LIFE_MS);
    toast.dataset.timer = String(timer);

    // limita quantidade
    const all = h.querySelectorAll('.toastx');
    while (all.length > MAX_TOASTS) dismiss(all[0]);
}

function closeModalIfSuccess() {
    const msgs = msgsRoot();
    if (!msgs) return;

    const hasSuccess =
        msgs.querySelector('li.jsf-info') ||
        msgs.querySelector('.jsf-info');

    if (!hasSuccess) return;

    const el = document.getElementById('modalNewUser');
    if (!el || !window.bootstrap) return;

    // garante instância e fecha
    const modal = bootstrap.Modal.getOrCreateInstance(el);
    modal.hide();
}

function flushFacesMessages() {
    const msgs = msgsRoot();
    if (!msgs) return;

    // “cinto de segurança”: esconde mesmo se CSS falhar
    msgs.style.display = 'none';

    const lis = msgs.querySelectorAll('li');
    if (!lis.length) return;

    lis.forEach((li) => {
        if (li.dataset.shown === '1') return;
        li.dataset.shown = '1';

        const type = severityFromNode(li);
        const { title, detail } = readMsg(li);

        if (!title && !detail) return;
        createToast({ type, title, detail });
    });

    closeModalIfSuccess();
}

// expõe caso você queira chamar manualmente
window.toastFacesFlush = flushFacesMessages;

// primeira carga
document.addEventListener('DOMContentLoaded', flushFacesMessages);

// 🔥 OBSERVA mudanças no #msgs (AJAX re-render)
document.addEventListener('DOMContentLoaded', function () {
    const msgs = msgsRoot();
    if (!msgs) return;

    const obs = new MutationObserver(() => {
        // micro-delay pra garantir DOM final
        setTimeout(flushFacesMessages, 0);
    });

    obs.observe(msgs, { childList: true, subtree: true });
});


