/**
 * CoVe - Student Project & Team Management Platform
 * CSE2006 Programming in Java - VIT Bhopal
 * Frontend Core Application Client
 */

const API_BASE = '/api';

// Current session manager
const Auth = {
    getUser() {
        const u = localStorage.getItem('cove_user');
        return u ? JSON.parse(u) : null;
    },
    setUser(user) {
        localStorage.setItem('cove_user', JSON.stringify(user));
    },
    logout() {
        localStorage.removeItem('cove_user');
        window.location.href = '/login.html';
    },
    requireAuth() {
        const user = this.getUser();
        if (!user) {
            window.location.href = '/login.html';
            return null;
        }
        return user;
    }
};

// UI Helpers
function showToast(message, isError = false) {
    const existing = document.getElementById('cove-toast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.id = 'cove-toast';
    toast.style.position = 'fixed';
    toast.style.bottom = '20px';
    toast.style.right = '20px';
    toast.style.backgroundColor = isError ? '#ef4444' : '#10b981';
    toast.style.color = '#fff';
    toast.style.padding = '12px 20px';
    toast.style.borderRadius = '8px';
    toast.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';
    toast.style.fontWeight = '600';
    toast.style.zIndex = '9999';
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => toast.remove(), 4000);
}

// Global Nav bar initialization
function initNavbar() {
    const user = Auth.getUser();
    const navUserEl = document.getElementById('navUser');
    if (navUserEl) {
        if (user) {
            navUserEl.innerHTML = `
                <span class="badge badge-primary">${user.role}</span>
                <span style="font-weight: 600; font-size: 0.9rem;">${user.name}</span>
                <a href="${user.dashboardUrl}" class="btn btn-sm btn-secondary">Dashboard</a>
                <button onclick="Auth.logout()" class="btn btn-sm btn-danger">Logout</button>
            `;
            loadNotificationBadge(user.id);
        } else {
            navUserEl.innerHTML = `
                <a href="/login.html" class="btn btn-sm btn-primary">Login</a>
                <a href="/register.html" class="btn btn-sm btn-secondary">Register</a>
            `;
        }
    }
}

async function loadNotificationBadge(userId) {
    try {
        const res = await fetch(`${API_BASE}/notifications/user/${userId}`);
        if (res.ok) {
            const data = await res.json();
            const badgeEl = document.getElementById('notifBadge');
            if (badgeEl && data.unreadCount > 0) {
                badgeEl.textContent = data.unreadCount;
                badgeEl.style.display = 'inline-block';
            }
        }
    } catch (e) {
        console.warn('Could not fetch notifications badge:', e);
    }
}

// Quick Demo Login helper (allows instantaneous faculty/student/admin testing)
async function quickLogin(role) {
    let email = 'tanu.gowda@vitbhopal.ac.in';
    let pass = 'student123';

    if (role === 'FACULTY') {
        email = 'ashwin.m@vitbhopal.ac.in';
        pass = 'faculty123';
    } else if (role === 'ADMIN') {
        email = 'admin@vitbhopal.ac.in';
        pass = 'admin123';
    }

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: email, password: pass })
        });
        const data = await res.json();
        if (res.ok) {
            Auth.setUser(data);
            showToast('Logged in as ' + data.name);
            setTimeout(() => window.location.href = data.dashboardUrl, 600);
        } else {
            showToast(data.message || 'Login failed', true);
        }
    } catch (err) {
        showToast('Network error during quick login', true);
    }
}

document.addEventListener('DOMContentLoaded', initNavbar);
