const uploadForm = document.getElementById('uploadForm');
const qrCodeDiv = document.getElementById('qrCode');
const logoutBtn = document.getElementById('logoutBtn');
const showCertificatesBtn = document.getElementById('showCertificatesBtn');
const token = localStorage.getItem('token');

if (!token) {
    window.location.href = 'login.html';
}

logoutBtn.addEventListener('click', () => {
    localStorage.clear();
    window.location.href = 'login.html';
});

showCertificatesBtn.addEventListener('click', () => {
    window.location.href = 'certificates.html';
});

uploadForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    qrCodeDiv.innerHTML = '';

    const fileInput = document.getElementById('file');
    if (fileInput.files.length === 0) {
        alert('Please select a file');
        return;
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    try {
        const response = await fetch('http://localhost:8084/api/certificates/upload', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();

        // Display QR code image from server and success message
        const qrUrl = `http://localhost:8084/api/certificates/qrcode/${encodeURIComponent(data.qrCodeContent)}`;
        // Use full <img> tag, and add alt, style, and show a fallback if image is broken
        qrCodeDiv.innerHTML = `<p style="color: green; font-weight:bold;">Certificate uploaded successfully!</p>
            <p>Scan this QR code to download certificate:</p>
            <img src="${qrUrl}" alt="QR Code" width="180" height="180" onerror="this.src='images/qr-placeholder.png';" />
        `;
    } catch (error) {
        qrCodeDiv.innerHTML = `<p style="color: red;">${error.message || 'Upload failed'}</p>`;
    }
});
