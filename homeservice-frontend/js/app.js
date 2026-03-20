
import { authAPI, servicesAPI, bookingsAPI, adminAPI } from './api.js';

let currentUser = null;
let allServices = [];

const $ = (sel, ctx = document) => ctx.querySelector(sel);
const $$ = (sel, ctx = document) => [...ctx.querySelectorAll(sel)];

function toast(msg, type = 'info') {
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  const icon = type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ';
  el.innerHTML = `<span>${icon}</span><span>${msg}</span>`;
  $('#toast-container').appendChild(el);
  setTimeout(() => el.remove(), 3500);
}

function showPage(id) {
  $$('.page').forEach(p => p.classList.remove('active'));
  const pg = $(`#page-${id}`);
  if (pg) pg.classList.add('active');
  $$('.nav-btn[data-page]').forEach(b => b.classList.toggle('active', b.dataset.page === id));
}

function badgeHtml(status) {
  const cls = {
    'Pending':     'badge-pending',
    'Accepted':    'badge-accepted',
    'In Progress': 'badge-inprogress',
    'Completed':   'badge-completed',
    'Rejected':    'badge-rejected',
  }[status] || 'badge-pending';
  return `<span class="badge ${cls}">${status}</span>`;
}

function fmtDate(d) {
  if (!d) return '—';
  return new Date(d).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

function loading(container) {
  container.innerHTML = `<div class="loading"><div class="spinner"></div><span>Loading…</span></div>`;
}

function renderNav() {
  const navLinks = $('#nav-links');
  const navUser  = $('#nav-user');

  if (!currentUser) {
    navLinks.innerHTML = `
      <button class="nav-btn" data-page="home">Home</button>
      <button class="nav-btn" data-page="login">Login</button>
      <button class="nav-btn" data-page="register">Register</button>`;
    navUser.innerHTML = '';
    return;
  }

  const role = currentUser.role;
  let links = `<button class="nav-btn" data-page="home">Home</button>`;
  if (role === 'customer') links += `<button class="nav-btn" data-page="services">Services</button><button class="nav-btn" data-page="my-bookings">My Bookings</button>`;
  if (role === 'provider') links += `<button class="nav-btn" data-page="provider-jobs">My Jobs</button>`;
  if (role === 'admin')    links += `<button class="nav-btn" data-page="admin">Admin</button>`;

  navLinks.innerHTML = links;
  navUser.innerHTML = `
    <div class="nav-avatar">${currentUser.fullname.charAt(0).toUpperCase()}</div>
    <span>${currentUser.fullname.split(' ')[0]}</span>
    <button class="btn-logout" id="btn-logout">Logout</button>`;

  $('#btn-logout')?.addEventListener('click', async () => {
    await authAPI.logout().catch(() => {});
    currentUser = null;
    renderNav();
    showPage('home');
    renderHome();
    toast('Logged out', 'info');
  });

  $$('.nav-btn[data-page]').forEach(btn => {
    btn.addEventListener('click', () => navigate(btn.dataset.page));
  });
}

function navigate(page) {

  const guarded = ['my-bookings', 'provider-jobs', 'admin'];
  if (guarded.includes(page) && !currentUser) {
    showPage('login');
    toast('Please login to access that page', 'info');
    return;
  }
  showPage(page);
  $$('.nav-btn[data-page]').forEach(b => b.classList.toggle('active', b.dataset.page === page));
  switch(page) {
    case 'home':          renderHome(); break;
    case 'services':      renderServices(); break;
    case 'my-bookings':   renderMyBookings(); break;
    case 'provider-jobs': renderProviderJobs(); break;
    case 'admin':         renderAdmin(); break;
  }
}


function renderHome() {
  const pg = $('#page-home');
  if (currentUser) {
    const role = currentUser.role;
    let cta = '';
    if (role === 'customer') cta = `<button class="btn btn-primary" onclick="navigate('services')"><i class="fas fa-search"></i> Browse Services</button>`;
    if (role === 'provider') cta = `<button class="btn btn-primary" onclick="navigate('provider-jobs')"><i class="fas fa-briefcase"></i> View My Jobs</button>`;
    if (role === 'admin')    cta = `<button class="btn btn-primary" onclick="navigate('admin')"><i class="fas fa-tachometer-alt"></i> Admin Dashboard</button>`;
    pg.querySelector('.hero-btns').innerHTML = cta + `<button class="btn btn-outline" onclick="navigate('services')">View Services</button>`;
  } else {
    pg.querySelector('.hero-btns').innerHTML = `
      <button class="btn btn-primary" onclick="navigate('register')"><i class="fas fa-user-plus"></i> Get Started</button>
      <button class="btn btn-outline" onclick="navigate('login')">Login</button>`;
  }
}

function setupLogin() {
  const form = $('#login-form');
  form.addEventListener('submit', async e => {
    e.preventDefault();
    const btn = form.querySelector('button[type=submit], button:not([type])');
    if (btn) { btn.disabled = true; btn.textContent = 'Logging in…'; }
    const alertEl = $('#login-alert');
    alertEl.className = 'alert'; alertEl.textContent = '';

    try {
      const data = await authAPI.login({
        email:    form.email.value,
        password: form.password.value,
      });
      currentUser = data;
      renderNav();
      toast(`Welcome back, ${data.fullname.split(' ')[0]}!`, 'success');
      const dest = data.role === 'admin' ? 'admin' : data.role === 'provider' ? 'provider-jobs' : 'services';
      navigate(dest);
    } catch(err) {
      alertEl.className = 'alert alert-error';
      alertEl.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${err.message}`;
    } finally {
      if (btn) { btn.disabled = false; btn.textContent = 'Login'; }
    }
  });
}

function setupRegister() {
  const form = $('#register-form');
  form.addEventListener('submit', async e => {
    e.preventDefault();
    const btn = form.querySelector('button[type=submit], button:not([type])');
    if (btn) { btn.disabled = true; btn.textContent = 'Registering…'; }
    const alertEl = $('#register-alert');
    alertEl.className = 'alert'; alertEl.textContent = '';

    try {
      await authAPI.register({
        email:    form.email.value,
        fullname: form.fullname.value,
        address:  form.address.value,
        phone:    form.phone.value,
        password: form.password.value,
        role:     form.role.value,
      });
      alertEl.className = 'alert alert-success';
      alertEl.innerHTML = `<i class="fas fa-check-circle"></i> Registration successful! Please login.`;
      form.reset();
      setTimeout(() => navigate('login'), 1800);
    } catch(err) {
      alertEl.className = 'alert alert-error';
      alertEl.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${err.message}`;
    } finally {
      if (btn) { btn.disabled = false; btn.textContent = 'Create Account'; }
    }
  });
}

async function renderServices() {
  const grid = $('#services-grid');
  const searchEl = $('#service-search');
  loading(grid);

  const newSearch = searchEl.cloneNode(true);
  searchEl.parentNode.replaceChild(newSearch, searchEl);

  try {
    allServices = await servicesAPI.getAll();
    renderServiceGrid(allServices);
  } catch(err) {
    grid.innerHTML = `<div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> Failed to load services.</div>`;
  }

  let timeout;
  newSearch.addEventListener('input', () => {
    clearTimeout(timeout);
    timeout = setTimeout(async () => {
      const q = newSearch.value.trim();
      loading(grid);
      try {
        const results = await servicesAPI.getAll(q || undefined);
        renderServiceGrid(results);
      } catch { renderServiceGrid([]); }
    }, 350);
  });
}

function renderServiceGrid(services) {
  const grid = $('#services-grid');
  if (!services.length) {
    grid.innerHTML = `<div class="empty-state" style="grid-column:1/-1"><div class="empty-state-icon">🔍</div><h4>No services found</h4><p>Try a different search term</p></div>`;
    return;
  }
  grid.innerHTML = services.map(s => `
    <div class="service-card" onclick="openBookingModal(${s.id}, '${s.name.replace(/'/g, "\\'")}', ${s.price})">
      <span class="service-category">${s.category}</span>
      <div class="service-icon"><i class="${s.iconClass}"></i></div>
      <div class="service-name">${s.name}</div>
      <div class="service-desc">${s.description}</div>
      <div class="service-price">₹${s.price}</div>
      ${currentUser?.role === 'customer' ? '<div style="margin-top:12px;font-size:.8rem;color:var(--saffron);font-weight:600;">Click to Book →</div>' : ''}
    </div>
  `).join('');
}
window.openBookingModal = function(serviceId, serviceName, price) {
  if (!currentUser) { navigate('login'); toast('Please login to book a service', 'info'); return; }
  if (currentUser.role !== 'customer') { toast('Only customers can book services', 'info'); return; }

  $('#booking-modal-title').textContent  = `Book: ${serviceName}`;
  $('#booking-modal-price').textContent  = `₹${price}`;
  $('#booking-modal-service').textContent = serviceName;
  $('#booking-form').reset();
  const today = new Date().toISOString().split('T')[0];
  $('#booking-date').min = today;
  $('#booking-service-type').value = serviceName;
  $('#booking-alert').className = 'alert';
  $('#booking-alert').textContent = '';
  $('#booking-modal').classList.add('open');
};

function setupBookingModal() {
  $('#booking-modal-close').addEventListener('click', () => $('#booking-modal').classList.remove('open'));
  $('#booking-modal').addEventListener('click', e => {
    if (e.target === e.currentTarget) e.currentTarget.classList.remove('open');
  });

  $('#booking-form').addEventListener('submit', async e => {
    e.preventDefault();
    const btn = e.target.querySelector('button[type=submit], button:not([type])');
    if (btn) { btn.disabled = true; btn.textContent = 'Booking…'; }
    const alertEl = $('#booking-alert');
    alertEl.className = 'alert'; alertEl.textContent = '';

    try {
      await bookingsAPI.create({
        serviceType: $('#booking-service-type').value,
        description: $('#booking-description').value,
        serviceDate: $('#booking-date').value,
      });
      toast('Booking confirmed! A provider will be assigned.', 'success');
      $('#booking-modal').classList.remove('open');
    } catch(err) {
      alertEl.className = 'alert alert-error';
      alertEl.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${err.message}`;
    } finally {
      if (btn) { btn.disabled = false; btn.textContent = 'Confirm Booking'; }
    }
  });
}

async function renderMyBookings() {
  const container = $('#my-bookings-list');
  loading(container);
  try {
    const bookings = await bookingsAPI.myBookings();
    if (!bookings.length) {
      container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">📋</div><h4>No bookings yet</h4><p>Browse services and make your first booking!</p></div>`;
      return;
    }
    container.innerHTML = bookings.map(b => `
      <div class="booking-card">
        <div>
          <div class="booking-service">${b.serviceType}</div>
          <div class="booking-meta">${b.description || 'No description'}</div>
          <div class="booking-date">
            <i class="fas fa-calendar-alt" style="color:var(--muted)"></i> ${fmtDate(b.serviceDate)}
            &nbsp;•&nbsp;
            Provider: <strong>${b.providerName || 'Assigning…'}</strong>
          </div>
        </div>
        <div>${badgeHtml(b.status)}</div>
      </div>
    `).join('');
  } catch(err) {
    container.innerHTML = `<div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> ${err.message || 'Failed to load bookings'}</div>`;
  }
}

async function renderProviderJobs() {
  const container = $('#provider-jobs-list');
  loading(container);
  try {
    const jobs = await bookingsAPI.providerJobs();
    if (!jobs.length) {
      container.innerHTML = `<div class="empty-state"><div class="empty-state-icon">🔧</div><h4>No jobs assigned yet</h4><p>New jobs will appear here when assigned.</p></div>`;
      return;
    }
    container.innerHTML = jobs.map(j => `
      <div class="booking-card">
        <div>
          <div class="booking-service">${j.serviceType}</div>
          <div class="booking-meta">
            <i class="fas fa-user"></i> ${j.customerName} &nbsp;•&nbsp;
            <i class="fas fa-phone"></i> ${j.customerPhone}
          </div>
          <div class="booking-meta" style="margin-top:3px">
            <i class="fas fa-map-marker-alt"></i> ${j.customerAddress}
          </div>
          <div class="booking-date"><i class="fas fa-calendar-alt" style="color:var(--muted)"></i> ${fmtDate(j.serviceDate)}</div>
          ${j.description ? `<div class="booking-meta" style="margin-top:4px;font-style:italic;">"${j.description}"</div>` : ''}
        </div>
        <div>
          ${badgeHtml(j.status)}
          ${j.status !== 'Completed' && j.status !== 'Rejected' ? `
            <div class="booking-actions" style="margin-top:10px">
              ${j.status === 'Pending'     ? `<button class="btn btn-sm btn-success" onclick="updateJobStatus(${j.id},'Accepted')">Accept</button>` : ''}
              ${j.status === 'Accepted'    ? `<button class="btn btn-sm btn-primary" onclick="updateJobStatus(${j.id},'In Progress')">Start</button>` : ''}
              ${j.status === 'In Progress' ? `<button class="btn btn-sm btn-success" onclick="updateJobStatus(${j.id},'Completed')">Complete</button>` : ''}
              <button class="btn btn-sm btn-ghost" onclick="updateJobStatus(${j.id},'Rejected')">Reject</button>
            </div>` : ''}
        </div>
      </div>
    `).join('');
  } catch(err) {
    container.innerHTML = `<div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> ${err.message || 'Failed to load jobs'}</div>`;
  }
}

window.updateJobStatus = async function(id, status) {
  try {
    await bookingsAPI.updateStatus(id, status);
    toast(`Status updated to ${status}`, 'success');
    renderProviderJobs();
  } catch(err) {
    toast(err.message || 'Failed to update status', 'error');
  }
};

async function renderAdmin() {
  await renderAdminStats();
  setupAdminTabs();
}

async function renderAdminStats() {
  const statsEl = $('#admin-stats');
  loading(statsEl);
  try {
    const s = await adminAPI.stats();
    statsEl.innerHTML = `
      <div class="stat-card"><div class="stat-icon">📋</div><div><div class="stat-label">Total Bookings</div><div class="stat-value">${s.totalBookings}</div></div></div>
      <div class="stat-card"><div class="stat-icon">⏳</div><div><div class="stat-label">Pending</div><div class="stat-value">${s.pendingBookings}</div></div></div>
      <div class="stat-card"><div class="stat-icon">👥</div><div><div class="stat-label">Customers</div><div class="stat-value">${s.totalCustomers}</div></div></div>
      <div class="stat-card"><div class="stat-icon">🔧</div><div><div class="stat-label">Providers</div><div class="stat-value">${s.totalProviders}</div></div></div>
    `;
  } catch(err) {
    statsEl.innerHTML = `<div class="alert alert-error">Failed to load stats: ${err.message}</div>`;
  }
}

function setupAdminTabs() {
  $$('#admin-tabs .tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      $$('#admin-tabs .tab-btn').forEach(b => b.classList.remove('active'));
      $$('.tab-panel').forEach(p => p.classList.remove('active'));
      btn.classList.add('active');
      $(`#tab-${btn.dataset.tab}`).classList.add('active');
      if (btn.dataset.tab === 'bookings') renderAdminBookings();
      if (btn.dataset.tab === 'users')    renderAdminUsers();
    });
  });
  
  renderAdminBookings();
}

async function renderAdminBookings() {
  const tbl = $('#admin-bookings-table');
  loading(tbl);
  try {
    const [bookings, providers] = await Promise.all([bookingsAPI.allBookings(), adminAPI.providers()]);
    if (!bookings.length) {
      tbl.innerHTML = `<div class="empty-state"><div class="empty-state-icon">📋</div><h4>No bookings yet</h4></div>`;
      return;
    }

    const providerOpts = providers.map(p => `<option value="${p.id}">${p.fullname}</option>`).join('');

    tbl.innerHTML = `
      <div class="table-wrap">
        <table>
          <thead><tr>
            <th>#</th><th>Service</th><th>Customer</th><th>Date</th>
            <th>Status</th><th>Provider</th><th>Assign</th>
          </tr></thead>
          <tbody>
            ${bookings.map(b => `
              <tr>
                <td style="color:var(--muted)">#${b.id}</td>
                <td><strong>${b.serviceType}</strong></td>
                <td>
                  <div>${b.customerName}</div>
                  <div style="font-size:.78rem;color:var(--muted)">${b.customerPhone}</div>
                </td>
                <td>${fmtDate(b.serviceDate)}</td>
                <td>${badgeHtml(b.status)}</td>
                <td>${b.providerName || '<span style="color:var(--muted)">Unassigned</span>'}</td>
                <td>
                  <div style="display:flex;gap:6px;align-items:center">
                    <select class="form-control" style="padding:6px 10px;font-size:.8rem;width:150px" id="prov-sel-${b.id}">
                      <option value="">Select…</option>
                      ${providerOpts}
                    </select>
                    <button class="btn btn-sm btn-primary" onclick="adminAssign(${b.id})">Assign</button>
                  </div>
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  } catch(err) {
    tbl.innerHTML = `<div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> ${err.message}</div>`;
  }
}

window.adminAssign = async function(bookingId) {
  const sel = $(`#prov-sel-${bookingId}`);
  const providerId = Number(sel.value);
  if (!providerId) { toast('Please select a provider', 'info'); return; }
  try {
    await bookingsAPI.assignProvider(bookingId, providerId);
    toast('Provider assigned!', 'success');
    renderAdminBookings();
  } catch(err) {
    toast(err.message || 'Failed to assign', 'error');
  }
};

async function renderAdminUsers() {
  const tbl = $('#admin-users-table');
  loading(tbl);
  try {
    const users = await adminAPI.users();
    tbl.innerHTML = `
      <div class="table-wrap">
        <table>
          <thead><tr><th>#</th><th>Name</th><th>Email</th><th>Phone</th><th>Address</th><th>Role</th></tr></thead>
          <tbody>
            ${users.map(u => `
              <tr>
                <td style="color:var(--muted)">${u.id}</td>
                <td><strong>${u.fullname}</strong></td>
                <td>${u.email}</td>
                <td>${u.phone}</td>
                <td style="max-width:180px;white-space:normal;font-size:.83rem">${u.address}</td>
                <td><span class="badge ${u.role === 'admin' ? 'badge-completed' : u.role === 'provider' ? 'badge-accepted' : 'badge-pending'}">${u.role}</span></td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  } catch(err) {
    tbl.innerHTML = `<div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> ${err.message}</div>`;
  }
}

async function init() {
  try {
    currentUser = await authAPI.me();
  } catch {
    currentUser = null; // 401 = not logged in, totally normal
  }

  renderNav();

  $$('.nav-btn[data-page]').forEach(btn => {
    btn.addEventListener('click', () => navigate(btn.dataset.page));
  });

  setupLogin();
  setupRegister();
  setupBookingModal();

  if (currentUser) {
    const dest = currentUser.role === 'admin' ? 'admin' : currentUser.role === 'provider' ? 'provider-jobs' : 'home';
    navigate(dest);
  } else {
    navigate('home');
  }
}

window.navigate = navigate;

document.addEventListener('DOMContentLoaded', init);