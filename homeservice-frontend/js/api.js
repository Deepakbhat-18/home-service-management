

const BASE = 'http://localhost:8080/api';

async function request(method, path, body = null) {
  const opts = {
    method,
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
  };
  if (body) opts.body = JSON.stringify(body);

  const res = await fetch(BASE + path, opts);


  if (res.status === 204) return null;

  const data = await res.json().catch(() => ({}));

  if (!res.ok) {

    throw new Error(data.message || `Request failed (${res.status})`);
  }

  return data;
}

export const authAPI = {
  register: (body)  => request('POST', '/auth/register', body),
  login:    (body)  => request('POST', '/auth/login',    body),
  logout:   ()      => request('POST', '/auth/logout'),
  me:       ()      => request('GET',  '/auth/me'),
};

export const servicesAPI = {
  getAll: (q) => request('GET', q ? `/services?q=${encodeURIComponent(q)}` : '/services'),
};

export const bookingsAPI = {
  create:         (body)              => request('POST', '/bookings',              body),
  myBookings:     ()                  => request('GET',  '/bookings/my'),
  providerJobs:   ()                  => request('GET',  '/bookings/provider'),
  allBookings:    ()                  => request('GET',  '/bookings/all'),
  updateStatus:   (id, status)        => request('PUT',  `/bookings/${id}/status`, { status }),
  assignProvider: (id, providerId)    => request('PUT',  `/bookings/${id}/assign`, { providerId }),
};

export const adminAPI = {
  stats:     () => request('GET', '/admin/stats'),
  users:     () => request('GET', '/admin/users'),
  providers: () => request('GET', '/admin/providers'),
};