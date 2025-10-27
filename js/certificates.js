const certList = document.getElementById('certList');
const userInfo = document.getElementById('userInfo');
const token = localStorage.getItem('token');
const logoutBtn = document.getElementById('logoutBtn');
const qrPopup = document.getElementById('qrPopup');
const qrDisplay = document.getElementById('qrDisplay');
const closeQr = document.getElementById('closeQr');
const viewBtn = document.getElementById('viewBtn');
const downloadBtn = document.getElementById('downloadBtn');

let currentQrContent = null;

if (!token) {
    window.location.href = 'login.html';
}

logoutBtn.onclick = function () {
    localStorage.clear();
    window.location.href = 'login.html';
};

fetch('http://localhost:8084/api/certificates/my-certificates', {
    headers: {'Authorization': `Bearer ${token}`}
})
.then(res => res.json())
.then(data => {
    if (!data.length) {
        certList.innerHTML = '<li>No certificates uploaded yet.</li>';
        return;
    }
    userInfo.innerHTML = `<b>User:</b> ${data[0].username}<br/><b>Email:</b> ${data[0].email}`;
    certList.innerHTML = '';
    data.forEach(cert => {
        const li = document.createElement('li');
        li.className = "cert-list";
        li.innerHTML = `<a href="#" class="cert-link" data-qrcode="${cert.qrCodeContent}">${cert.certificateName}</a>`;
        certList.appendChild(li);
    });

    document.querySelectorAll('.cert-link').forEach(c => {
        c.onclick = function(e) {
            e.preventDefault();
            showQr(this.dataset.qrcode);
        }
    });
});

function showQr(qrCodeContent) {
    currentQrContent = qrCodeContent;
    const qrImgUrl = `http://localhost:8084/api/certificates/qrcode/${encodeURIComponent(qrCodeContent)}`;
    
    // Clear modal and show loading state
    qrDisplay.innerHTML = `<p>Loading QR code...</p>`;
    qrPopup.style.display = 'block'; // Open modal immediately (prevents jank)
    
    // Only replace content once QR is actually loaded
    const img = new Image();
    img.onload = function() {
        qrDisplay.innerHTML = `
            <p>Scan this QR code or click buttons below:</p>
            <img src="${qrImgUrl}" alt="QR Code" width="180" height="180"/>
        `;
    };
    img.onerror = function() {
        qrDisplay.innerHTML = `<p style="color: red;">Unable to load QR code for this certificate.</p>`;
    };
    img.src = qrImgUrl;
}

viewBtn.onclick = function() {
    if (currentQrContent)
        window.open(`http://localhost:8084/api/certificates/download?qrCodeContent=${encodeURIComponent(currentQrContent)}`, '_blank');
};
downloadBtn.onclick = function() {
    if (currentQrContent) {
        let a = document.createElement('a');
        a.href = `http://localhost:8084/api/certificates/download?qrCodeContent=${encodeURIComponent(currentQrContent)}`;
        a.download = 'certificate.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }
};

closeQr.onclick = function() { qrPopup.style.display = 'none'; };
window.onclick = function(event) {
    if (event.target == qrPopup) qrPopup.style.display = "none";
};
