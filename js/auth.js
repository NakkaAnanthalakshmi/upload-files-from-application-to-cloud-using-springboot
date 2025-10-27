const baseUrl = 'http://localhost:8084/api/auth';

document.addEventListener('DOMContentLoaded', function() {
    const signupForm = document.getElementById('signupForm');
    const loginForm = document.getElementById('loginForm');
    const messageDiv = document.getElementById('message');

    if (signupForm) {
        signupForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            messageDiv.innerHTML = '';
            const username = document.getElementById('username').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value.trim();

            try {
                const response = await fetch(`${baseUrl}/signup`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({username, email, password})
                });
                if (!response.ok) {
                    const text = await response.text();
                    throw new Error(text);
                }
                messageDiv.style.color = 'green';
                messageDiv.innerText = 'Registration successful! Redirecting to login...';
                setTimeout(() => window.location.href = 'login.html', 2000);
            } catch (error) {
                messageDiv.style.color = 'red';
                messageDiv.innerText = error.message || 'Registration failed';
            }
        });
    }

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            messageDiv.innerHTML = '';
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value.trim();

            try {
                const response = await fetch(`${baseUrl}/signin`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({username, password})
                });
                if (!response.ok) {
                    const text = await response.text();
                    throw new Error(text);
                }
                const data = await response.json();
                localStorage.setItem('token', data.accessToken);
                localStorage.setItem('username', username);
                window.location.href = 'upload.html';
            } catch (error) {
                messageDiv.style.color = 'red';
                messageDiv.innerText = error.message || 'Login failed';
            }
        });
    }
});
